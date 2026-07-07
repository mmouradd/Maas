package com.gsrm.maas.controller;

import com.gsrm.maas.dto.RegisterRequest;
import com.gsrm.maas.entity.Agence;
import com.gsrm.maas.entity.Escale;
import com.gsrm.maas.entity.Utilisateur;
import com.gsrm.maas.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Espace Administration : utilisateurs, agences, escales. Réservé au rôle ADMIN (cf. SecurityConfig). */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // -------------------- utilisateurs --------------------

    @PostMapping("/utilisateurs")
    public ResponseEntity<Utilisateur> creerUtilisateur(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.creerUtilisateur(request));
    }

    @GetMapping("/utilisateurs")
    public ResponseEntity<List<Utilisateur>> listerUtilisateurs() {
        return ResponseEntity.ok(adminService.listerUtilisateurs());
    }

    @PatchMapping("/utilisateurs/{id}/actif")
    public ResponseEntity<Utilisateur> activerDesactiver(@PathVariable Long id, @RequestParam boolean actif) {
        return ResponseEntity.ok(adminService.activerDesactiverUtilisateur(id, actif));
    }

    // -------------------- agences --------------------

    @PostMapping("/agences")
    public ResponseEntity<Agence> creerAgence(@RequestBody Agence agence) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.creerAgence(agence));
    }

    @PutMapping("/agences/{id}")
    public ResponseEntity<Agence> modifierAgence(@PathVariable Long id, @RequestBody Agence agence) {
        return ResponseEntity.ok(adminService.modifierAgence(id, agence));
    }

    // -------------------- escales --------------------

    @PostMapping("/escales")
    public ResponseEntity<Escale> creerEscale(@RequestBody Escale escale) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.creerEscale(escale));
    }

    @PutMapping("/escales/{id}")
    public ResponseEntity<Escale> modifierEscale(@PathVariable Long id, @RequestBody Escale escale) {
        return ResponseEntity.ok(adminService.modifierEscale(id, escale));
    }
}
