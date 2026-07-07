package com.gsrm.maas.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "agences")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agence")
    private Long id;

    @Column(name = "nom_agence", nullable = false)
    private String nomAgence;

    @Column(name = "code_agence", nullable = false, unique = true)
    private String codeAgence;

    @Column(name = "email_contact")
    private String emailContact;

    private String telephone;

    @Builder.Default
    @Column(nullable = false)
    private Boolean actif = true;
}
