package com.accessaudit.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Estrutura padrão para respostas de erro.
 * Referência: TG3.6 — todos os status 400/404 da API retornam este envelope.
 */
@Schema(description = "Estrutura padrão para respostas de erro")
public record ErroResponse(

        @Schema(description = "Código HTTP do erro", example = "400")
        int status,

        @Schema(description = "Nome curto do erro HTTP", example = "Bad Request")
        String erro,

        @Schema(description = "Mensagem detalhando o problema",
                example = "URL inválida informada para auditoria.")
        String mensagem,

        @Schema(description = "Caminho que originou o erro", example = "/api/auditorias")
        String caminho
) {}
