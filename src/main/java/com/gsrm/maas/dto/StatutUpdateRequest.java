package com.gsrm.maas.dto;

import com.gsrm.maas.entity.StatutDemande;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatutUpdateRequest {
    @NotNull private StatutDemande nouveauStatut;
    /** Obligatoire si REFUSEE ou ANNULEE (règle de gestion). */
    private String motif;
    private String commentaire;
}
