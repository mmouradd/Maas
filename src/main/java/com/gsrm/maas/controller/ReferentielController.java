package com.gsrm.maas.controller;

import com.gsrm.maas.entity.Agence;
import com.gsrm.maas.entity.Escale;
import com.gsrm.maas.repository.AgenceRepository;
import com.gsrm.maas.repository.EscaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** Référentiels en lecture (listes d'escales/agences pour les formulaires). */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReferentielController {

    private final EscaleRepository escaleRepository;
    private final AgenceRepository agenceRepository;

    @GetMapping("/escales")
    public ResponseEntity<List<Escale>> escales() {
        return ResponseEntity.ok(escaleRepository.findAll());
    }

    @GetMapping("/agences")
    public ResponseEntity<List<Agence>> agences() {
        return ResponseEntity.ok(agenceRepository.findAll());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
