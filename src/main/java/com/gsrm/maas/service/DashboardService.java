package com.gsrm.maas.service;

import com.gsrm.maas.entity.StatutDemande;
import com.gsrm.maas.repository.DemandeMaasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DemandeMaasRepository demandeRepository;

    /** Indicateurs pour le tableau de bord du Gestionnaire GSRM. */
    public Map<String, Object> statistiques() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDemandes", demandeRepository.count());

        Map<String, Long> parStatut = new LinkedHashMap<>();
        for (Object[] row : demandeRepository.countGroupByStatut()) {
            parStatut.put(((StatutDemande) row[0]).getLibelle(), (Long) row[1]);
        }
        stats.put("parStatut", parStatut);

        Map<String, Long> parEscale = new LinkedHashMap<>();
        for (Object[] row : demandeRepository.countByEscale()) {
            parEscale.put((String) row[0], (Long) row[1]);
        }
        stats.put("parEscale", parEscale);

        Map<String, Long> parAgence = new LinkedHashMap<>();
        for (Object[] row : demandeRepository.countByAgence()) {
            parAgence.put((String) row[0], (Long) row[1]);
        }
        stats.put("parAgence", parAgence);

        stats.put("enAttente", demandeRepository.countByStatut(StatutDemande.NOUVELLE)
                + demandeRepository.countByStatut(StatutDemande.NOTIFIEE));
        return stats;
    }
}
