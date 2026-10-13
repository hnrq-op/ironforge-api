package br.com.henrique.estudos.ironforge_pedidos.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeignClientConfigTest {

    private FeignClientConfig feignClientConfig;

    @Mock
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @Mock
    private OAuth2AuthorizedClient authorizedClient;

    @BeforeEach
    void setUp() {
        feignClientConfig = new FeignClientConfig();
    }

    @Test
    @DisplayName("Deve retornar o nível de log FULL para o Feign")
    void deveRetornarNivelLogFull() {
        Logger.Level level = feignClientConfig.feignLoggerLevel();
        assertThat(level).isEqualTo(Logger.Level.FULL);
    }

    @Test
    @DisplayName("Deve adicionar o cabeçalho Authorization com Bearer token quando o cliente for autorizado com sucesso")
    void deveAdicionarHeaderAuthorizationQuandoTokenExistir() {
        String tokenValue = "token-jwt-de-teste";
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                tokenValue,
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class))).thenReturn(authorizedClient);
        when(authorizedClient.getAccessToken()).thenReturn(accessToken);

        RequestInterceptor interceptor = feignClientConfig.requestInterceptor(authorizedClientManager);
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        Collection<String> authHeaders = template.headers().get("Authorization");
        assertThat(authHeaders).isNotNull().containsExactly("Bearer " + tokenValue);

        ArgumentCaptor<OAuth2AuthorizeRequest> captor = ArgumentCaptor.forClass(OAuth2AuthorizeRequest.class);
        verify(authorizedClientManager).authorize(captor.capture());
        assertThat(captor.getValue().getClientRegistrationId()).isEqualTo("ironforge-pedidos-service");
    }

    @Test
    @DisplayName("Não deve adicionar o cabeçalho Authorization se o cliente autorizado for nulo")
    void naoDeveAdicionarHeaderQuandoClienteForNulo() {
        when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class))).thenReturn(null);

        RequestInterceptor interceptor = feignClientConfig.requestInterceptor(authorizedClientManager);
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertThat(template.headers().get("Authorization")).isNull();
    }
}