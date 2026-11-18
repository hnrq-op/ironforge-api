package br.com.henrique.estudos.ironforge_gateway.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CorrelationLoggingFilterTest {

    private final CorrelationLoggingFilter filter = new CorrelationLoggingFilter();

    @Test
    @DisplayName("Deve gerar e propagar um novo X-Correlation-Id quando ausente na requisição")
    void deveGerarCorrelationIdQuandoAusenteNaRequisicao() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/produtos").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        AtomicReference<String> propagatedHeader = new AtomicReference<>();
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any(ServerWebExchange.class))).thenAnswer(invocation -> {
            ServerWebExchange mutatedExchange = invocation.getArgument(0);
            propagatedHeader.set(mutatedExchange.getRequest().getHeaders().getFirst("X-Correlation-Id"));
            return Mono.empty();
        });

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(propagatedHeader.get()).isNotBlank();
        assertThat(exchange.getResponse().getHeaders().getFirst("X-Correlation-Id"))
                .isEqualTo(propagatedHeader.get());
    }

    @Test
    @DisplayName("Deve preservar e propagar o X-Correlation-Id existente enviado na requisição")
    void devePreservarCorrelationIdExistenteNaRequisicao() {
        String existingId = "req-ironforge-98765";
        MockServerHttpRequest request = MockServerHttpRequest.get("/pedidos")
                .header("X-Correlation-Id", existingId)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(exchange.getResponse().getHeaders().getFirst("X-Correlation-Id"))
                .isEqualTo(existingId);
    }
}