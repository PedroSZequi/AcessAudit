package com.accessaudit.backend.dto;

import com.accessaudit.backend.domain.TipoEntrada;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Dados detalhados de uma auditoria — inclui o relatório completo.
 * Referência: TG3.6 (GET /api/auditorias/{id}).
 */
@Schema(description = "Dados detalhados de uma auditoria")
public record AuditoriaDetalheResponse(

        @Schema(description = "Identificador único da auditoria")
        UUID id,

        @Schema(description = "Tipo de entrada utilizada na auditoria")
        TipoEntrada tipoEntrada,

        @Schema(description = "URL utilizada na auditoria, quando aplicável")
        String url,

        @Schema(description = "Código HTML utilizado na auditoria, quando aplicável")
        String html,

        @Schema(description = "Data e hora da execução da auditoria")
        LocalDateTime dataExecucao,

        @Schema(description = "Relatório gerado após a auditoria")
        RelatorioResponse relatorio
) {}
