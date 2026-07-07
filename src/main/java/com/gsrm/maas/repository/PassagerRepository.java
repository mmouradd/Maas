package com.gsrm.maas.repository;

import com.gsrm.maas.entity.Passager;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PassagerRepository extends JpaRepository<Passager, Long> {
    List<Passager> findByDemandeId(Long idDemande);
}
