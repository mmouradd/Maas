package com.gsrm.maas.controller;

import com.gsrm.maas.dto.DemandeRequest;
import com.gsrm.maas.dto.StatutUpdateRequest;
import com.gsrm.maas.entity.*;
import com.gsrm.maas.service.AuthService;
import com.gsrm.maas.service.DemandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
public class DemandeController {

    private final DemandeService demandeService;
    private final AuthService authService;

    /** Création d'une demande MAAS (Espace Agences ; aussi ouvert au gestionnaire/admin). */
    @PostMapping
    public ResponseEntity<DemandeMaas> creer(@Valid @RequestBody DemandeRequest request,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur auteur = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(demandeService.creer(request, auteur));
    }

    /**
     * Liste avec filtres (escale, agence, statut, type, plage de dates).
     * Le cloisonnement par rôle est appliqué côté service :
     * une agence ne voit que ses demandes, une escale uniquement les siennes.
     */
    @GetMapping
    public ResponseEntity<List<DemandeMaas>> rechercher(
            @RequestParam(required = false) Long idAgence,
            @RequestParam(required = false) Long idEscale,
            @RequestParam(required = false) StatutDemande statut,
            @RequestParam(required = false) TypeService typeService,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur utilisateur = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(demandeService.rechercher(
                utilisateur, idAgence, idEscale, statut, typeService, dateDebut, dateFin));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemandeMaas> obtenir(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur utilisateur = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(demandeService.obtenir(id, utilisateur));
    }

    /** Historique complet et immuable des statuts (traçabilité). */
    @GetMapping("/{id}/historique")
    public ResponseEntity<List<HistoriqueStatut>> historique(@PathVariable Long id,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur utilisateur = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(demandeService.historique(id, utilisateur));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DemandeMaas> modifier(@PathVariable Long id,
                                                @Valid @RequestBody DemandeRequest request,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur utilisateur = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(demandeService.modifier(id, request, utilisateur));
    }

    /** Changement de statut (confirmation, refus, traitement, annulation…). */
    @PatchMapping("/{id}/statut")
    public ResponseEntity<DemandeMaas> changerStatut(@PathVariable Long id,
                                                     @Valid @RequestBody StatutUpdateRequest request,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur utilisateur = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(demandeService.changerStatut(id, request, utilisateur));
    }
}
