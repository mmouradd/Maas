package com.gsrm.maas.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final UserDetails user = new User("agence@test.tn", "x", List.of());

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret",
                "une-cle-de-test-suffisamment-longue-pour-hs256!!");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 60_000L);
    }

    @Test
    void tokenGenere_contientLeSujetEtEstValide() {
        String token = jwtService.generateToken(user, Map.of("role", "AGENCE"));
        assertEquals("agence@test.tn", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void tokenExpire_estRejete() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
        String token = jwtService.generateToken(user, Map.of());
        assertFalse(jwtService.isTokenValid(token, user));
    }

    @Test
    void tokenPourUnAutreUtilisateur_estRejete() {
        String token = jwtService.generateToken(user, Map.of());
        UserDetails autre = new User("autre@test.tn", "x", List.of());
        assertFalse(jwtService.isTokenValid(token, autre));
    }
}
