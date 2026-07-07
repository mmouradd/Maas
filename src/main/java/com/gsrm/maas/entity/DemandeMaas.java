package com.gsrm.maas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "demandes_maas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DemandeMaas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande")
    private Long id;

    @Column(name = "numero_demande", nullable = false, unique = true)
    private String numeroDemande;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_agence")
    private Agence agence;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_escale")
    private Escale escale;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_service", nullable = false)
    private TypeService typeService;

    @Column(name = "date_service", nullable = false)
    private LocalDate dateService;

    @Column(name = "heure_service", nullable = false)
    private LocalTime heureService;

    @Column(name = "nombre_passagers", nullable = false)
    private Integer nombrePassagers;

    @Column(name = "cout_service", nullable = false, precision = 10, scale = 2)
    private BigDecimal coutService;

    @Builder.Default
    @Column(name = "frais_additionnels", nullable = false, precision = 10, scale = 2)
    private BigDecimal fraisAdditionnels = BigDecimal.ZERO;

    /** Règle de gestion : coût total = coût service + frais additionnels (calculé automatiquement). */
    @Column(name = "cout_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal coutTotal;

    @Column(name = "nom_greeter")
    private String nomGreeter;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private StatutDemande statut = StatutDemande.NOUVELLE;

    @Column(name = "motif_refus_annulation")
    private String motifRefusAnnulation;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "cree_par")
    private Utilisateur creePar;

    @Builder.Default
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "date_maj")
    private LocalDateTime dateMaj;

    @Builder.Default
    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Passager> passagers = new ArrayList<>();

    @PrePersist @PreUpdate
    private void computeTotal() {
        BigDecimal frais = fraisAdditionnels != null ? fraisAdditionnels : BigDecimal.ZERO;
        this.coutTotal = coutService.add(frais);
        this.dateMaj = LocalDateTime.now();
    }
}
