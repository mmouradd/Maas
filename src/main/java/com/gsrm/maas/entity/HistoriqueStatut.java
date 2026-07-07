package com.gsrm.maas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Historique immuable des changements de statut (traçabilité).
 * Insert-only : aucun endpoint de modification/suppression n'est exposé.
 */
@Entity
@Table(name = "historique_statuts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistoriqueStatut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_demande")
    private DemandeMaas demande;

    @Column(name = "statut_precedent")
    private String statutPrecedent;

    @Column(name = "nouveau_statut", nullable = false)
    private String nouveauStatut;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "modifie_par")
    private Utilisateur modifiePar;

    private String commentaire;

    @Builder.Default
    @Column(name = "date_modification", nullable = false, updatable = false)
    private LocalDateTime dateModification = LocalDateTime.now();
}
