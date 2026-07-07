package com.gsrm.maas.repository;

import com.gsrm.maas.entity.Agence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AgenceRepository extends JpaRepository<Agence, Long> {
    Optional<Agence> findByCodeAgence(String codeAgence);
    boolean existsByCodeAgence(String codeAgence);
}
