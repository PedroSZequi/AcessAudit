package com.accessaudit.backend.dto;

import com.accessaudit.backend.domain.TipoEntrada;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Dados resumidos de uma auditoria — usado na lista (GET /api/auditorias).
 * Referência: TG3.6.
 */
@Schema(description = "Dados resumidos de uma auditoria")
public record AuditoriaResumoResponse(

        @Schema(description = "Identificador único da auditoria")
        UUID id,

        @Schema(description = "Tipo de entrada utilizada na auditoria")
        TipoEntrada tipoEntrada,

        @Schema(description = "URL utilizada na auditoria, quando aplicável")
        String url,

        @Schema(description = "Data e hora da execução da auditoria")
        LocalDateTime dataExecucao
) {}
