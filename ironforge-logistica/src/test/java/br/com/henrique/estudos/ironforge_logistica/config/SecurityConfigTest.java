package br.com.henrique.estudos.ironforge_logistica.config;

import br.com.henrique.estudos.ironforge_logistica.controller.RecebimentoCallbackLogisticaController;
import br.com.henrique.estudos.ironforge_logistica.service.EntregaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecebimentoCallbackLogisticaController.class)
@Import(SecurityConfig.class)
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EntregaService entregaService;

    @Test
    @DisplayName("Security - Deve permitir acesso público à rota /logistica/callback-logistica sem autenticação")
    void devePermitirAcessoPublicoACallbackLogistica() throws Exception {
        String jsonPayload = """
                {
                    "id": "e1111111-1111-1111-1111-111111111111",
                    "pedidoId": "a1111111-1111-1111-1111-111111111111",
                    "chaveLogistica": "chave-logistica-123",
                    "codigoRastreio": "BR123456789BR",
                    "status": "ENVIADO",
                    "transportadora": "Jadlog",
                    "dataPrevisao": "2026-10-15"
                }
                """;

        mockMvc.perform(post("/logistica/callback-logistica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Security - Deve exigir autenticação (403 Forbidden) para qualquer outra rota não liberada")
    void deveExigirAutenticacaoParaOutrasRotas() throws Exception {
        mockMvc.perform(get("/logistica/outra-rota"))
                .andExpect(status().isForbidden());
    }
}