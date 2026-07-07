package com.gsrm.maas.controller;

import com.gsrm.maas.entity.*;
import com.gsrm.maas.service.AuthService;
import com.gsrm.maas.service.DemandeService;
import com.gsrm.maas.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/** Export Excel / PDF des demandes (Espace Gestionnaire GSRM). Accès restreint dans SecurityConfig. */
@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;
    private final DemandeService demandeService;
    private final AuthService authService;

    @GetMapping("/demandes/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) Long idAgence,
            @RequestParam(required = false) Long idEscale,
            @RequestParam(required = false) StatutDemande statut,
            @RequestParam(required = false) TypeService typeService,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        List<DemandeMaas> demandes = filtrer(idAgence, idEscale, statut, typeService, dateDebut, dateFin, userDetails);
        byte[] contenu = exportService.exportExcel(demandes);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=demandes_maas.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(contenu);
    }

    @GetMapping("/demandes/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) Long idAgence,
            @RequestParam(required = false) Long idEscale,
            @RequestParam(required = false) StatutDemande statut,
            @RequestParam(required = false) TypeService typeService,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<DemandeMaas> demandes = filtrer(idAgence, idEscale, statut, typeService, dateDebut, dateFin, userDetails);
        byte[] contenu = exportService.exportPdf(demandes);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=demandes_maas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(contenu);
    }

    private List<DemandeMaas> filtrer(Long idAgence, Long idEscale, StatutDemande statut,
                                      TypeService typeService, LocalDate dateDebut, LocalDate dateFin,
                                      UserDetails userDetails) {
        Utilisateur utilisateur = authService.getCurrentUser(userDetails.getUsername());
        return demandeService.rechercher(utilisateur, idAgence, idEscale, statut, typeService, dateDebut, dateFin);
    }
}
