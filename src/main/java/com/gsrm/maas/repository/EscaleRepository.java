package com.gsrm.maas.repository;

import com.gsrm.maas.entity.Escale;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EscaleRepository extends JpaRepository<Escale, Long> {
    Optional<Escale> findByCodeIata(String codeIata);
    boolean existsByCodeIata(String codeIata);
}
