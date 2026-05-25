package com.accessaudit.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Relatório gerado após a execução de uma auditoria.
 * Referência: TG3.6 (esquema do POST /api/auditorias e GET /api/auditorias/{id}/relatorio).
 */
@Schema(description = "Relatório gerado após a auditoria")
public record RelatorioResponse(

        @Schema(description = "Identificador do relatório")
        UUID id,

        @Schema(description = "Data e hora de geração do relatório")
        LocalDateTime geradoEm,

        @Schema(description = "Quantidade total de problemas encontrados", example = "3")
        int totalProblemas,

        @Schema(description = "Lista de problemas encontrados")
        List<ProblemaResponse> problemas
) {}
