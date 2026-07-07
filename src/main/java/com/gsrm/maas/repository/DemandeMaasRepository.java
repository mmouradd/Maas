package com.gsrm.maas.repository;

import com.gsrm.maas.entity.DemandeMaas;
import com.gsrm.maas.entity.StatutDemande;
import com.gsrm.maas.entity.TypeService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface DemandeMaasRepository extends JpaRepository<DemandeMaas, Long> {

    /**
     * Recherche multi-critères. Les filtres null sont ignorés.
     * idAgence / idEscale servent aussi au cloisonnement par rôle.
     */
    @Query("""
        SELECT d FROM DemandeMaas d
        WHERE (:idAgence IS NULL OR d.agence.id = :idAgence)
          AND (:idEscale IS NULL OR d.escale.id = :idEscale)
          AND (:statut IS NULL OR d.statut = :statut)
          AND (:typeService IS NULL OR d.typeService = :typeService)
          AND (:dateDebut IS NULL OR d.dateService >= :dateDebut)
          AND (:dateFin IS NULL OR d.dateService <= :dateFin)
        ORDER BY d.dateCreation DESC
        """)
    List<DemandeMaas> search(@Param("idAgence") Long idAgence,
                             @Param("idEscale") Long idEscale,
                             @Param("statut") StatutDemande statut,
                             @Param("typeService") TypeService typeService,
                             @Param("dateDebut") LocalDate dateDebut,
                             @Param("dateFin") LocalDate dateFin);

    boolean existsByNumeroDemande(String numeroDemande);

    long countByStatut(StatutDemande statut);

    @Query("SELECT d.escale.codeIata, COUNT(d) FROM DemandeMaas d GROUP BY d.escale.codeIata")
    List<Object[]> countByEscale();

    @Query("SELECT d.statut, COUNT(d) FROM DemandeMaas d GROUP BY d.statut")
    List<Object[]> countGroupByStatut();

    @Query("SELECT d.agence.nomAgence, COUNT(d) FROM DemandeMaas d GROUP BY d.agence.nomAgence")
    List<Object[]> countByAgence();
}
