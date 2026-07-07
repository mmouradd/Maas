package com.gsrm.maas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @Builder @AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long idUtilisateur;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private Long idAgence;
    private Long idEscale;
}
