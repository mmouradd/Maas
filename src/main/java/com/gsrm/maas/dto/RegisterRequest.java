package com.gsrm.maas.dto;

import com.gsrm.maas.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Création de compte utilisateur (réservée à l'administrateur). */
@Data
public class RegisterRequest {
    @NotBlank private String nom;
    @NotBlank private String prenom;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;
    @NotNull private Role role;
    private Long idAgence;   // requis si role = AGENCE
    private Long idEscale;   // requis si role = ESCALE
}
