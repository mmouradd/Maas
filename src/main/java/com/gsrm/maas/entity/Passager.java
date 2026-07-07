package com.gsrm.maas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "passagers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Passager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_passager")
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_demande")
    private DemandeMaas demande;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(name = "numero_vol")
    private String numeroVol;

    @Column(name = "numero_document")
    private String numeroDocument;
}
