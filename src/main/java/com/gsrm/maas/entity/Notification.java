package com.gsrm.maas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_demande")
    private DemandeMaas demande;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_destinataire")
    private Utilisateur destinataire;

    @Column(name = "type_notification", nullable = false)
    private String typeNotification;

    @Column(nullable = false, length = 500)
    private String message;

    @Builder.Default
    @Column(nullable = false)
    private Boolean lu = false;

    @Builder.Default
    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi = LocalDateTime.now();
}
