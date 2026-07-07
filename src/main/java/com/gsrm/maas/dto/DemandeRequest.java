package com.gsrm.maas.dto;

import com.gsrm.maas.entity.TypeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class DemandeRequest {
    @NotNull private Long idEscale;
    private Long idAgence; // ignoré pour un compte AGENCE (déduit du compte) ; requis pour GESTIONNAIRE/ADMIN
    @NotNull private TypeService typeService;
    @NotNull @FutureOrPresent(message = "La date du service ne peut pas être dans le passé")
    private LocalDate dateService;
    @NotNull private LocalTime heureService;
    @NotNull @Min(1) private Integer nombrePassagers;
    @NotNull @DecimalMin(value = "0.0") private BigDecimal coutService;
    @DecimalMin(value = "0.0") private BigDecimal fraisAdditionnels;
    private String nomGreeter;
    @Valid private List<PassagerDto> passagers;
}
