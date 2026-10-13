package br.com.henrique.estudos.ironforge_produtos.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
    }

    @Test
    @DisplayName("CONVERTER - Deve converter roles do realm_access para GrantedAuthorities com prefixo ROLE_ em maiúsculo")
    void deveConverterRolesDoKeycloakComSucesso() {
        Jwt jwt = createJwt(Map.of("realm_access", Map.of("roles", List.of("cliente", "admin"))));

        var converter = securityConfig.jwtAuthenticationConverter();
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) converter.convert(jwt);

        assertNotNull(authenticationToken);
        Collection<GrantedAuthority> authorities = authenticationToken.getAuthorities();

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .toList();

        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_CLIENTE"));
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("CONVERTER - Deve retornar lista vazia quando a claim realm_access for nula")
    void deveRetornarListaVaziaQuandoRealmAccessForNulo() {
        Jwt jwt = createJwt(Map.of());

        var converter = securityConfig.jwtAuthenticationConverter();
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) converter.convert(jwt);

        assertNotNull(authenticationToken);

        List<String> roles = authenticationToken.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .toList();

        assertTrue(roles.isEmpty());
    }

    @Test
    @DisplayName("CONVERTER - Deve retornar lista vazia quando a chave roles não existir em realm_access")
    void deveRetornarListaVaziaQuandoChaveRolesNaoExistir() {
        Jwt jwt = createJwt(Map.of("realm_access", Map.of("other_claim", List.of("value"))));

        var converter = securityConfig.jwtAuthenticationConverter();
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) converter.convert(jwt);

        assertNotNull(authenticationToken);

        List<String> roles = authenticationToken.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .toList();

        assertTrue(roles.isEmpty());
    }

    private Jwt createJwt(Map<String, Object> customClaims) {
        Map<String, Object> claims = new HashMap<>(customClaims);
        claims.putIfAbsent("sub", "usuario-teste");

        return new Jwt(
                "mock-token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                claims
        );
    }
}