package br.com.henrique.estudos.ironforge_logistica.controller;

import br.com.henrique.estudos.ironforge_logistica.controller.dto.RecebimentoCallbackLogisticaDTO;
import br.com.henrique.estudos.ironforge_logistica.service.EntregaService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecebimentoCallbackLogisticaController.class)
@WithMockUser
public class RecebimentoCallbackLogisticaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EntregaService entregaService;

    private static final String API_KEY_HEADER = "apiKey";
    private static final String API_KEY_VALUE = "chave-secret-api-123";

    @Test
    @DisplayName("POST /logistica/callback-logistica - Deve processar callback e retornar HTTP 200 OK quando o payload e a apiKey forem válidos")
    void deveProcessarCallbackComSucesso() throws Exception {
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
                        .with(csrf())
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk());

        verify(entregaService).processarCallbackTransportadora(any(RecebimentoCallbackLogisticaDTO.class));
    }

    @Test
    @DisplayName("POST /logistica/callback-logistica - Deve retornar HTTP 400 Bad Request quando o header apiKey estiver ausente")
    void deveRetornarBadRequestQuandoHeaderApiKeyAusente() throws Exception {
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
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /logistica/callback-logistica - Deve retornar HTTP 422 Unprocessable Content quando houver campos obrigatórios ausentes no JSON")
    void deveRetornarBadRequestQuandoDtoForInvalido() throws Exception {
        String jsonInvalido = """
                {
                    "chaveLogistica": "",
                    "codigoRastreio": ""
                }
                """;

        mockMvc.perform(post("/logistica/callback-logistica")
                        .with(csrf())
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @DisplayName("POST /logistica/callback-logistica - Deve retornar HTTP 404 Not Found quando a entrega não for encontrada no service")
    void deveRetornarNotFoundQuandoEntregaNaoExistir() throws Exception {
        String jsonPayload = """
                {
                    "id": "e9999999-9999-9999-9999-999999999999",
                    "pedidoId": "a9999999-9999-9999-9999-999999999999",
                    "chaveLogistica": "chave-inexistente",
                    "codigoRastreio": "BR123456789BR",
                    "status": "ENVIADO",
                    "transportadora": "Jadlog",
                    "dataPrevisao": "2026-10-15"
                }
                """;

        doThrow(new EntityNotFoundException("Entrega não encontrada"))
                .when(entregaService).processarCallbackTransportadora(any(RecebimentoCallbackLogisticaDTO.class));

        mockMvc.perform(post("/logistica/callback-logistica")
                        .with(csrf())
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isNotFound());
    }
}