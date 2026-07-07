package com.gsrm.maas.controller;

import com.gsrm.maas.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Tableaux de bord de suivi (Espace Gestionnaire GSRM). Accès restreint dans SecurityConfig. */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> statistiques() {
        return ResponseEntity.ok(dashboardService.statistiques());
    }
}
