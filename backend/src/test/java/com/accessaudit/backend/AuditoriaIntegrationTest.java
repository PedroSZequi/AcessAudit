package com.accessaudit.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integração ponta-a-ponta — sobe contexto Spring inteiro
 * (controllers + service + JPA + Flyway + regras WCAG) e exercita os
 * fluxos críticos via MockMvc.
 *
 * @Transactional faz cada teste rolar-back no fim, mantendo o banco
 * limpo entre execuções (sem precisar de testcontainers para um MVP).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuditoriaIntegrationTest {

    private static final String HTML_COM_PROBLEMAS = """
            <!DOCTYPE html>
            <html>
              <head></head>
              <body>
                <img src="a.png">
                <input type="text" name="nome">
              </body>
            </html>
            """;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void postExecutaAuditoriaPorHtmlEDetectaProblemas() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("tipoEntrada", "HTML", "html", HTML_COM_PROBLEMAS));

        mockMvc.perform(post("/api/auditorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.tipoEntrada").value("HTML"))
                .andExpect(jsonPath("$.url").doesNotExist())
                .andExpect(jsonPath("$.relatorio.totalProblemas").value(3))
                .andExpect(jsonPath("$.relatorio.problemas[*].codigoRegra",
                        containsInAnyOrder("IMG_SEM_ALT", "INPUT_SEM_LABEL", "TITLE_AUSENTE")))
                // trechoHtml preenchido para a img sem alt
                .andExpect(jsonPath(
                        "$.relatorio.problemas[?(@.codigoRegra=='IMG_SEM_ALT')].trechoHtml")
                        .value(org.hamcrest.Matchers.hasItem("<img src=\"a.png\">")));
    }

    @Test
    void postRetorna400ParaTipoEntradaUrlSemUrl() throws Exception {
        mockMvc.perform(post("/api/auditorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoEntrada\":\"URL\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Bad Request"))
                .andExpect(jsonPath("$.caminho").value("/api/auditorias"))
                .andExpect(jsonPath("$.mensagem", notNullValue()));
    }

    @Test
    void getRetorna404ParaAuditoriaInexistente() throws Exception {
        mockMvc.perform(get("/api/auditorias/{id}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Not Found"));
    }

    @Test
    void getRegrasRetornaRegrasCarregadas() throws Exception {
        mockMvc.perform(get("/api/regras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].codigo",
                        containsInAnyOrder("IMG_SEM_ALT", "INPUT_SEM_LABEL", "TITLE_AUSENTE")));
    }

    @Test
    void fluxoCompleto_PostEntaoGetDetalheERelatorio() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("tipoEntrada", "HTML", "html", HTML_COM_PROBLEMAS));

        String resposta = mockMvc.perform(post("/api/auditorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String idAuditoria = objectMapper.readTree(resposta).get("id").asText();

        // Flush parcial: precisa commitar antes do próximo GET para ele enxergar
        // O @Transactional do teste seria um problema aqui — mas como a request
        // continua dentro do mesmo Spring context, JPA flush antes do GET é suficiente.

        mockMvc.perform(get("/api/auditorias/{id}", idAuditoria))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idAuditoria))
                .andExpect(jsonPath("$.relatorio.totalProblemas").value(3));

        mockMvc.perform(get("/api/auditorias/{id}/relatorio", idAuditoria))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProblemas").value(3))
                .andExpect(jsonPath("$.problemas[*].codigoRegra",
                        containsInAnyOrder("IMG_SEM_ALT", "INPUT_SEM_LABEL", "TITLE_AUSENTE")));
    }
}
