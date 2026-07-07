package com.gsrm.maas.service;

import com.gsrm.maas.dto.AuthResponse;
import com.gsrm.maas.dto.LoginRequest;
import com.gsrm.maas.entity.Utilisateur;
import com.gsrm.maas.repository.UtilisateurRepository;
import com.gsrm.maas.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse()));

        Utilisateur u = utilisateurRepository.findByEmail(request.getEmail()).orElseThrow();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", u.getRole().name());
        if (u.getAgence() != null) claims.put("idAgence", u.getAgence().getId());
        if (u.getEscale() != null) claims.put("idEscale", u.getEscale().getId());

        String token = jwtService.generateToken(
                new User(u.getEmail(), u.getMotDePasse(), List.of()), claims);

        return AuthResponse.builder()
                .token(token)
                .idUtilisateur(u.getId())
                .nom(u.getNom())
                .prenom(u.getPrenom())
                .email(u.getEmail())
                .role(u.getRole().name())
                .idAgence(u.getAgence() != null ? u.getAgence().getId() : null)
                .idEscale(u.getEscale() != null ? u.getEscale().getId() : null)
                .build();
    }

    public Utilisateur getCurrentUser(String email) {
        return utilisateurRepository.findByEmail(email).orElseThrow();
    }
}
