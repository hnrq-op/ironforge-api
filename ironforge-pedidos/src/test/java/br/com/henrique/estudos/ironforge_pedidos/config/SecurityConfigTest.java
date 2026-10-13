package br.com.henrique.estudos.ironforge_pedidos.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
    }

    @Test
    @DisplayName("Deve converter roles do claim realm_access para GrantedAuthorities com prefixo ROLE_ em caixa alta")
    void deveConverterRolesDoJwtCorretamente() {
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();

        Map<String, Object> realmAccess = Map.of("roles", List.of("admin", "cliente_vip"));
        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", "usuario-123", "realm_access", realmAccess)
        );

        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) converter.convert(jwt);

        assertThat(authenticationToken).isNotNull();

        List<String> roles = authenticationToken.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .toList();

        assertThat(roles).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_CLIENTE_VIP");
    }

    @Test
    @DisplayName("Deve retornar lista vazia de authorities quando o claim realm_access for ausente ou não contiver roles")
    void deveRetornarAuthoritiesVazioQuandoSemRoles() {
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();

        Jwt jwtSemRoles = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", "usuario-123")
        );

        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) converter.convert(jwtSemRoles);

        assertThat(authenticationToken).isNotNull();

        List<String> roles = authenticationToken.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .toList();

        assertThat(roles).isEmpty();
    }

    @Test
    @DisplayName("Deve inicializar a configuração de segurança e os beans no contexto do Spring")
    void deveCarregarBeansDeSeguranca() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                .withUserConfiguration(SecurityConfig.class)
                .withBean(JwtDecoder.class, () -> mock(JwtDecoder.class))
                .withBean(ClientRegistrationRepository.class, () -> mock(ClientRegistrationRepository.class))
                .withBean(OAuth2AuthorizedClientService.class, () -> mock(OAuth2AuthorizedClientService.class));

        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SecurityConfig.class);
            assertThat(context).hasBean("securityFilterChain");
            assertThat(context).hasBean("jwtAuthenticationConverter");
            assertThat(context).hasBean("authorizedClientManager");
        });
    }
}