package com.gsrm.maas.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Vérifie la règle de gestion : coût total = coût service + frais additionnels
 * (calculé automatiquement avant chaque persistance).
 */
class DemandeMaasTest {

    private DemandeMaas nouvelleDemande(BigDecimal cout, BigDecimal frais) {
        return DemandeMaas.builder()
                .numeroDemande("MAAS-FRA-2026-00001")
                .typeService(TypeService.ARRIVAL)
                .dateService(LocalDate.now().plusDays(7))
                .heureService(LocalTime.of(10, 30))
                .nombrePassagers(2)
                .coutService(cout)
                .fraisAdditionnels(frais)
                .build();
    }

    private void invoquerCalcul(DemandeMaas demande) throws Exception {
        Method m = DemandeMaas.class.getDeclaredMethod("computeTotal");
        m.setAccessible(true);
        m.invoke(demande);
    }

    @Test
    void coutTotal_estLaSommeDuServiceEtDesFrais() throws Exception {
        DemandeMaas demande = nouvelleDemande(new BigDecimal("120.00"), new BigDecimal("30.50"));
        invoquerCalcul(demande);
        assertEquals(new BigDecimal("150.50"), demande.getCoutTotal());
    }

    @Test
    void coutTotal_fraisNullsTraitesCommeZero() throws Exception {
        DemandeMaas demande = nouvelleDemande(new BigDecimal("99.99"), null);
        invoquerCalcul(demande);
        assertEquals(new BigDecimal("99.99"), demande.getCoutTotal());
    }

    @Test
    void statutParDefaut_estNouvelle() {
        DemandeMaas demande = nouvelleDemande(BigDecimal.TEN, BigDecimal.ZERO);
        assertEquals(StatutDemande.NOUVELLE, demande.getStatut());
    }

    @Test
    void libellesDesStatuts_correspondentAuCahierDesCharges() {
        assertEquals("Nouvelle", StatutDemande.NOUVELLE.getLibelle());
        assertEquals("Notifiée", StatutDemande.NOTIFIEE.getLibelle());
        assertEquals("En cours de traitement", StatutDemande.EN_COURS.getLibelle());
        assertEquals("Confirmée", StatutDemande.CONFIRMEE.getLibelle());
        assertEquals("Refusée", StatutDemande.REFUSEE.getLibelle());
        assertEquals("Traitée", StatutDemande.TRAITEE.getLibelle());
        assertEquals("Annulée", StatutDemande.ANNULEE.getLibelle());
    }
}
