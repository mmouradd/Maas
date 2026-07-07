package com.gsrm.maas.service;

import com.gsrm.maas.entity.*;
import com.gsrm.maas.exception.NotFoundException;
import com.gsrm.maas.repository.NotificationRepository;
import com.gsrm.maas.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EmailService emailService;

    /**
     * Nouvelle demande : notifie le(s) Gestionnaire(s) GSRM, les admins
     * et les utilisateurs de l'escale concernée (in-app + email).
     */
    @Transactional
    public void notifierNouvelleDemande(DemandeMaas demande) {
        String message = "Nouvelle demande " + demande.getNumeroDemande()
                + " (" + demande.getEscale().getCodeIata() + ", "
                + demande.getTypeService() + " le " + demande.getDateService() + ") créée par l'agence "
                + demande.getAgence().getNomAgence() + ".";

        List<Utilisateur> destinataires = new ArrayList<>();
        destinataires.addAll(utilisateurRepository.findByRoleInAndActifTrue(List.of(Role.GESTIONNAIRE, Role.ADMIN)));
        destinataires.addAll(utilisateurRepository.findByRoleAndEscaleIdAndActifTrue(Role.ESCALE, demande.getEscale().getId()));

        for (Utilisateur dest : destinataires) {
            creer(demande, dest, "NOUVELLE_DEMANDE", message);
        }
        // Email à l'adresse de contact de l'escale
        emailService.send(demande.getEscale().getEmailContact(),
                "[MAAS] Nouvelle demande " + demande.getNumeroDemande(), message);
    }

    /**
     * Changement de statut : l'agence émettrice est systématiquement notifiée (règle de gestion).
     */
    @Transactional
    public void notifierChangementStatut(DemandeMaas demande, StatutDemande ancien, StatutDemande nouveau) {
        String message = "La demande " + demande.getNumeroDemande() + " est passée de \""
                + ancien.getLibelle() + "\" à \"" + nouveau.getLibelle() + "\".";
        if (demande.getMotifRefusAnnulation() != null
                && (nouveau == StatutDemande.REFUSEE || nouveau == StatutDemande.ANNULEE)) {
            message += " Motif : " + demande.getMotifRefusAnnulation();
        }

        List<Utilisateur> agents = utilisateurRepository
                .findByRoleAndAgenceIdAndActifTrue(Role.AGENCE, demande.getAgence().getId());
        for (Utilisateur dest : agents) {
            creer(demande, dest, "CHANGEMENT_STATUT", message);
        }
        emailService.send(demande.getAgence().getEmailContact(),
                "[MAAS] Mise à jour de la demande " + demande.getNumeroDemande(), message);
    }

    private void creer(DemandeMaas demande, Utilisateur destinataire, String type, String message) {
        notificationRepository.save(Notification.builder()
                .demande(demande)
                .destinataire(destinataire)
                .typeNotification(type)
                .message(message)
                .build());
    }

    public List<Notification> mesNotifications(Long idUtilisateur, boolean nonLuesSeulement) {
        return nonLuesSeulement
                ? notificationRepository.findByDestinataireIdAndLuFalseOrderByDateEnvoiDesc(idUtilisateur)
                : notificationRepository.findByDestinataireIdOrderByDateEnvoiDesc(idUtilisateur);
    }

    public long compterNonLues(Long idUtilisateur) {
        return notificationRepository.countByDestinataireIdAndLuFalse(idUtilisateur);
    }

    @Transactional
    public void marquerCommeLue(Long idNotification, Long idUtilisateur) {
        Notification n = notificationRepository.findById(idNotification)
                .orElseThrow(() -> new NotFoundException("Notification introuvable"));
        if (!n.getDestinataire().getId().equals(idUtilisateur)) {
            throw new org.springframework.security.access.AccessDeniedException("Cette notification ne vous appartient pas.");
        }
        n.setLu(true);
        notificationRepository.save(n);
    }
}
