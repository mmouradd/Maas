package com.gsrm.maas.repository;

import com.gsrm.maas.entity.Role;
import com.gsrm.maas.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Utilisateur> findByRole(Role role);
    List<Utilisateur> findByRoleAndEscaleIdAndActifTrue(Role role, Long idEscale);
    List<Utilisateur> findByRoleAndAgenceIdAndActifTrue(Role role, Long idAgence);
    List<Utilisateur> findByRoleInAndActifTrue(List<Role> roles);
}
