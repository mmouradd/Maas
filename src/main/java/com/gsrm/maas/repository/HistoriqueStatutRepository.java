package com.gsrm.maas.repository;

import com.gsrm.maas.entity.HistoriqueStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoriqueStatutRepository extends JpaRepository<HistoriqueStatut, Long> {
    List<HistoriqueStatut> findByDemandeIdOrderByDateModificationAsc(Long idDemande);
}
