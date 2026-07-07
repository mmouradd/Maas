package com.gsrm.maas.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "escales")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Escale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_escale")
    private Long id;

    @Column(name = "nom_escale", nullable = false)
    private String nomEscale;

    @Column(name = "code_iata", nullable = false, unique = true, length = 3)
    private String codeIata;

    @Column(name = "email_contact")
    private String emailContact;

    @Builder.Default
    @Column(nullable = false)
    private Boolean actif = true;
}
