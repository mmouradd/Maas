package com.gsrm.maas.config;

import com.gsrm.maas.entity.Escale;
import com.gsrm.maas.entity.Role;
import com.gsrm.maas.entity.Utilisateur;
import com.gsrm.maas.repository.EscaleRepository;
import com.gsrm.maas.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

/**
 * Initialisation des données de référence :
 * - les 5 escales du cahier des charges (MUC, DUS, FRA, HAM, STR) ;
 * - un compte administrateur par défaut (mot de passe défini dans application.properties,
 *   à changer immédiatement en production).
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Value("${app.admin.email:admin@gsrm.local}")
    private String adminEmail;

    @Value("${app.admin.password:ChangeMe123!}")
    private String adminPassword;

    @org.springframework.context.annotation.Bean
    CommandLineRunner initData(EscaleRepository escaleRepository,
                               UtilisateurRepository utilisateurRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            Map<String, String> escales = Map.of(
                    "MUC", "Munich",
                    "DUS", "Düsseldorf",
                    "FRA", "Francfort",
                    "HAM", "Hambourg",
                    "STR", "Stuttgart");
            escales.forEach((code, ville) -> {
                if (!escaleRepository.existsByCodeIata(code)) {
                    escaleRepository.save(Escale.builder()
                            .codeIata(code)
                            .nomEscale("Aéroport de " + ville)
                            .build());
                    log.info("Escale initialisée : {} ({})", code, ville);
                }
            });

            if (!utilisateurRepository.existsByEmail(adminEmail)) {
                utilisateurRepository.save(Utilisateur.builder()
                        .nom("Admin")
                        .prenom("GSRM")
                        .email(adminEmail)
                        .motDePasse(passwordEncoder.encode(adminPassword))
                        .role(Role.ADMIN)
                        .build());
                log.warn("Compte administrateur par défaut créé ({}). Changez le mot de passe !", adminEmail);
            }
        };
    }
}
