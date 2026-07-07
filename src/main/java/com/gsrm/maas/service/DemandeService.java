package com.gsrm.maas.service;

import com.gsrm.maas.dto.DemandeRequest;
import com.gsrm.maas.dto.PassagerDto;
import com.gsrm.maas.dto.StatutUpdateRequest;
import com.gsrm.maas.entity.*;
import com.gsrm.maas.exception.BusinessRuleException;
import com.gsrm.maas.exception.NotFoundException;
import com.gsrm.maas.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class DemandeService {

    private final DemandeMaasRepository demandeRepository;
    private final AgenceRepository agenceRepository;
    private final EscaleRepository escaleRepository;
    private final HistoriqueStatutRepository historiqueRepository;
    private final NotificationService notificationService;

    /** Transitions de statut autorisées (cycle de vie, section 5 du cahier des charges). */
    private static final Map<StatutDemande, Set<StatutDemande>> TRANSITIONS = new EnumMap<>(StatutDemande.class);
    static {
        TRANSITIONS.put(StatutDemande.NOUVELLE, EnumSet.of(StatutDemande.NOTIFIEE, StatutDemande.EN_COURS, StatutDemande.ANNULEE));
        TRANSITIONS.put(StatutDemande.NOTIFIEE, EnumSet.of(StatutDemande.EN_COURS, StatutDemande.CONFIRMEE, StatutDemande.REFUSEE, StatutDemande.ANNULEE));
        TRANSITIONS.put(StatutDemande.EN_COURS, EnumSet.of(StatutDemande.CONFIRMEE, StatutDemande.REFUSEE, StatutDemande.ANNULEE));
        TRANSITIONS.put(StatutDemande.CONFIRMEE, EnumSet.of(StatutDemande.TRAITEE, StatutDemande.ANNULEE));
        TRANSITIONS.put(StatutDemande.REFUSEE, EnumSet.noneOf(StatutDemande.class));
        TRANSITIONS.put(StatutDemande.TRAITEE, EnumSet.noneOf(StatutDemande.class));
        TRANSITIONS.put(StatutDemande.ANNULEE, EnumSet.noneOf(StatutDemande.class));
    }

    // ------------------------------------------------------------------ création

    @Transactional
    public DemandeMaas creer(DemandeRequest request, Utilisateur auteur) {
        if (auteur.getRole() == Role.ESCALE) {
            throw new AccessDeniedException("Un compte escale ne peut pas créer de demande.");
        }

        // Règle : une demande ne peut viser qu'une escale paramétrée et active
        Escale escale = escaleRepository.findById(request.getIdEscale())
                .orElseThrow(() -> new NotFoundException("Escale introuvable."));
        if (!Boolean.TRUE.equals(escale.getActif())) {
            throw new BusinessRuleException("Cette escale est désactivée : aucune demande ne peut lui être adressée.");
        }

        // Agence émettrice : déduite du compte pour un rôle AGENCE, sinon fournie
        Agence agence;
        if (auteur.getRole() == Role.AGENCE) {
            agence = auteur.getAgence();
            if (agence == null) throw new BusinessRuleException("Ce compte agence n'est rattaché à aucune agence.");
        } else {
            if (request.getIdAgence() == null)
                throw new BusinessRuleException("L'agence émettrice doit être précisée.");
            agence = agenceRepository.findById(request.getIdAgence())
                    .orElseThrow(() -> new NotFoundException("Agence introuvable."));
        }
        if (!Boolean.TRUE.equals(agence.getActif())) {
            throw new BusinessRuleException("Cette agence est désactivée.");
        }

        DemandeMaas demande = DemandeMaas.builder()
                .numeroDemande(genererNumero(escale.getCodeIata()))
                .agence(agence)
                .escale(escale)
                .typeService(request.getTypeService())
                .dateService(request.getDateService())
                .heureService(request.getHeureService())
                .nombrePassagers(request.getNombrePassagers())
                .coutService(request.getCoutService())
                .fraisAdditionnels(request.getFraisAdditionnels() == null
                        ? java.math.BigDecimal.ZERO : request.getFraisAdditionnels())
                .nomGreeter(request.getNomGreeter())
                .statut(StatutDemande.NOUVELLE)
                .creePar(auteur)
                .build();

        if (request.getPassagers() != null) {
            for (PassagerDto p : request.getPassagers()) {
                demande.getPassagers().add(Passager.builder()
                        .demande(demande)
                        .nom(p.getNom())
                        .prenom(p.getPrenom())
                        .numeroVol(p.getNumeroVol())
                        .numeroDocument(p.getNumeroDocument())
                        .build());
            }
        }

        demande = demandeRepository.save(demande);

        // Historique : création
        journaliser(demande, null, StatutDemande.NOUVELLE, auteur, "Création de la demande");

        // Notification automatique du Gestionnaire et de l'escale, puis passage à "Notifiée"
        notificationService.notifierNouvelleDemande(demande);
        StatutDemande ancien = demande.getStatut();
        demande.setStatut(StatutDemande.NOTIFIEE);
        demande = demandeRepository.save(demande);
        journaliser(demande, ancien, StatutDemande.NOTIFIEE, auteur, "Notification automatique envoyée");

        return demande;
    }

    // ------------------------------------------------------------------ consultation (cloisonnée par rôle)

    public List<DemandeMaas> rechercher(Utilisateur utilisateur, Long idAgence, Long idEscale,
                                        StatutDemande statut, TypeService typeService,
                                        LocalDate dateDebut, LocalDate dateFin) {
        // Cloisonnement : une agence ne voit que ses demandes, une escale que les siennes
        if (utilisateur.getRole() == Role.AGENCE) {
            idAgence = utilisateur.getAgence().getId();
        } else if (utilisateur.getRole() == Role.ESCALE) {
            idEscale = utilisateur.getEscale().getId();
        }
        return demandeRepository.search(idAgence, idEscale, statut, typeService, dateDebut, dateFin);
    }

    public DemandeMaas obtenir(Long id, Utilisateur utilisateur) {
        DemandeMaas demande = demandeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Demande introuvable."));
        verifierAccesLecture(demande, utilisateur);
        return demande;
    }

    public List<HistoriqueStatut> historique(Long idDemande, Utilisateur utilisateur) {
        obtenir(idDemande, utilisateur); // vérifie l'accès
        return historiqueRepository.findByDemandeIdOrderByDateModificationAsc(idDemande);
    }

    // ------------------------------------------------------------------ modification

    @Transactional
    public DemandeMaas modifier(Long id, DemandeRequest request, Utilisateur utilisateur) {
        DemandeMaas demande = obtenir(id, utilisateur);

        boolean estGestionnaire = utilisateur.getRole() == Role.GESTIONNAIRE || utilisateur.getRole() == Role.ADMIN;
        boolean estAgenceProprietaire = utilisateur.getRole() == Role.AGENCE
                && demande.getAgence().getId().equals(utilisateur.getAgence().getId());

        if (!estGestionnaire && !estAgenceProprietaire) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier cette demande.");
        }
        if (demande.getStatut() == StatutDemande.TRAITEE || demande.getStatut() == StatutDemande.ANNULEE
                || demande.getStatut() == StatutDemande.REFUSEE) {
            throw new BusinessRuleException("Une demande " + demande.getStatut().getLibelle().toLowerCase()
                    + " ne peut plus être modifiée.");
        }

        Escale escale = escaleRepository.findById(request.getIdEscale())
                .orElseThrow(() -> new NotFoundException("Escale introuvable."));
        if (!Boolean.TRUE.equals(escale.getActif())) {
            throw new BusinessRuleException("Cette escale est désactivée.");
        }

        demande.setEscale(escale);
        demande.setTypeService(request.getTypeService());
        demande.setDateService(request.getDateService());
        demande.setHeureService(request.getHeureService());
        demande.setNombrePassagers(request.getNombrePassagers());
        demande.setCoutService(request.getCoutService());
        demande.setFraisAdditionnels(request.getFraisAdditionnels() == null
                ? java.math.BigDecimal.ZERO : request.getFraisAdditionnels());
        demande.setNomGreeter(request.getNomGreeter());

        if (request.getPassagers() != null) {
            demande.getPassagers().clear();
            for (PassagerDto p : request.getPassagers()) {
                demande.getPassagers().add(Passager.builder()
                        .demande(demande)
                        .nom(p.getNom())
                        .prenom(p.getPrenom())
                        .numeroVol(p.getNumeroVol())
                        .numeroDocument(p.getNumeroDocument())
                        .build());
            }
        }
        return demandeRepository.save(demande);
    }

    // ------------------------------------------------------------------ changement de statut

    @Transactional
    public DemandeMaas changerStatut(Long id, StatutUpdateRequest request, Utilisateur utilisateur) {
        DemandeMaas demande = obtenir(id, utilisateur);
        StatutDemande ancien = demande.getStatut();
        StatutDemande nouveau = request.getNouveauStatut();

        if (ancien == nouveau) {
            throw new BusinessRuleException("La demande est déjà au statut \"" + nouveau.getLibelle() + "\".");
        }
        if (!TRANSITIONS.getOrDefault(ancien, EnumSet.noneOf(StatutDemande.class)).contains(nouveau)) {
            throw new BusinessRuleException("Transition non autorisée : \"" + ancien.getLibelle()
                    + "\" → \"" + nouveau.getLibelle() + "\".");
        }

        verifierDroitChangementStatut(demande, utilisateur, nouveau);

        // Règle : motif obligatoire pour un refus ou une annulation
        if ((nouveau == StatutDemande.REFUSEE || nouveau == StatutDemande.ANNULEE)) {
            if (request.getMotif() == null || request.getMotif().isBlank()) {
                throw new BusinessRuleException("Un motif est obligatoire pour refuser ou annuler une demande.");
            }
            demande.setMotifRefusAnnulation(request.getMotif());
        }

        demande.setStatut(nouveau);
        demande = demandeRepository.save(demande);

        journaliser(demande, ancien, nouveau, utilisateur, request.getCommentaire());
        notificationService.notifierChangementStatut(demande, ancien, nouveau);

        return demande;
    }

    // ------------------------------------------------------------------ helpers

    private void verifierAccesLecture(DemandeMaas demande, Utilisateur utilisateur) {
        switch (utilisateur.getRole()) {
            case AGENCE -> {
                if (utilisateur.getAgence() == null
                        || !demande.getAgence().getId().equals(utilisateur.getAgence().getId()))
                    throw new AccessDeniedException("Cette demande n'appartient pas à votre agence.");
            }
            case ESCALE -> {
                if (utilisateur.getEscale() == null
                        || !demande.getEscale().getId().equals(utilisateur.getEscale().getId()))
                    throw new AccessDeniedException("Cette demande ne concerne pas votre escale.");
            }
            default -> { /* GESTIONNAIRE et ADMIN : accès total */ }
        }
    }

    /**
     * Règles de gestion :
     * - seule l'escale concernée peut confirmer / refuser / traiter (le gestionnaire/admin le peut aussi) ;
     * - l'agence émettrice ne peut qu'annuler sa demande ;
     * - le gestionnaire/admin peut tout faire.
     */
    private void verifierDroitChangementStatut(DemandeMaas demande, Utilisateur utilisateur, StatutDemande nouveau) {
        switch (utilisateur.getRole()) {
            case GESTIONNAIRE, ADMIN -> { /* autorisé sur tous les statuts */ }
            case ESCALE -> {
                if (!demande.getEscale().getId().equals(utilisateur.getEscale().getId()))
                    throw new AccessDeniedException("Seule l'escale concernée peut traiter cette demande.");
                if (nouveau == StatutDemande.ANNULEE)
                    throw new AccessDeniedException("Une escale ne peut pas annuler une demande (annulation réservée à l'agence ou au gestionnaire).");
            }
            case AGENCE -> {
                if (nouveau != StatutDemande.ANNULEE)
                    throw new AccessDeniedException("Une agence ne peut qu'annuler ses propres demandes.");
            }
        }
    }

    private void journaliser(DemandeMaas demande, StatutDemande ancien, StatutDemande nouveau,
                             Utilisateur auteur, String commentaire) {
        historiqueRepository.save(HistoriqueStatut.builder()
                .demande(demande)
                .statutPrecedent(ancien != null ? ancien.name() : null)
                .nouveauStatut(nouveau.name())
                .modifiePar(auteur)
                .commentaire(commentaire)
                .build());
    }

    /** Référence lisible et unique, ex. : MAAS-FRA-2026-48213 */
    private String genererNumero(String codeIata) {
        String numero;
        do {
            numero = "MAAS-" + codeIata + "-" + Year.now().getValue() + "-"
                    + String.format("%05d", ThreadLocalRandom.current().nextInt(100000));
        } while (demandeRepository.existsByNumeroDemande(numero));
        return numero;
    }
}
