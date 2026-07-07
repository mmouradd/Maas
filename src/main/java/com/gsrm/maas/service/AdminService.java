package com.gsrm.maas.service;

import com.gsrm.maas.dto.RegisterRequest;
import com.gsrm.maas.entity.*;
import com.gsrm.maas.exception.BusinessRuleException;
import com.gsrm.maas.exception.NotFoundException;
import com.gsrm.maas.repository.AgenceRepository;
import com.gsrm.maas.repository.EscaleRepository;
import com.gsrm.maas.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UtilisateurRepository utilisateurRepository;
    private final AgenceRepository agenceRepository;
    private final EscaleRepository escaleRepository;
    private final PasswordEncoder passwordEncoder;

    // -------------------- utilisateurs --------------------

    @Transactional
    public Utilisateur creerUtilisateur(RegisterRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Un compte existe déjà avec cet email.");
        }

        Agence agence = null;
        Escale escale = null;
        if (request.getRole() == Role.AGENCE) {
            if (request.getIdAgence() == null)
                throw new BusinessRuleException("Un compte agence doit être rattaché à une agence.");
            agence = agenceRepository.findById(request.getIdAgence())
                    .orElseThrow(() -> new NotFoundException("Agence introuvable."));
        }
        if (request.getRole() == Role.ESCALE) {
            if (request.getIdEscale() == null)
                throw new BusinessRuleException("Un compte escale doit être rattaché à une escale.");
            escale = escaleRepository.findById(request.getIdEscale())
                    .orElseThrow(() -> new NotFoundException("Escale introuvable."));
        }

        return utilisateurRepository.save(Utilisateur.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(request.getRole())
                .agence(agence)
                .escale(escale)
                .build());
    }

    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    @Transactional
    public Utilisateur activerDesactiverUtilisateur(Long id, boolean actif) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable."));
        u.setActif(actif);
        return utilisateurRepository.save(u);
    }

    // -------------------- agences --------------------

    @Transactional
    public Agence creerAgence(Agence agence) {
        if (agenceRepository.existsByCodeAgence(agence.getCodeAgence())) {
            throw new BusinessRuleException("Une agence existe déjà avec ce code.");
        }
        agence.setId(null);
        return agenceRepository.save(agence);
    }

    @Transactional
    public Agence modifierAgence(Long id, Agence maj) {
        Agence agence = agenceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Agence introuvable."));
        agence.setNomAgence(maj.getNomAgence());
        agence.setCodeAgence(maj.getCodeAgence());
        agence.setEmailContact(maj.getEmailContact());
        agence.setTelephone(maj.getTelephone());
        if (maj.getActif() != null) agence.setActif(maj.getActif());
        return agenceRepository.save(agence);
    }

    // -------------------- escales --------------------

    @Transactional
    public Escale creerEscale(Escale escale) {
        if (escaleRepository.existsByCodeIata(escale.getCodeIata())) {
            throw new BusinessRuleException("Une escale existe déjà avec ce code IATA.");
        }
        escale.setId(null);
        return escaleRepository.save(escale);
    }

    @Transactional
    public Escale modifierEscale(Long id, Escale maj) {
        Escale escale = escaleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Escale introuvable."));
        escale.setNomEscale(maj.getNomEscale());
        escale.setCodeIata(maj.getCodeIata());
        escale.setEmailContact(maj.getEmailContact());
        if (maj.getActif() != null) escale.setActif(maj.getActif());
        return escaleRepository.save(escale);
    }
}
