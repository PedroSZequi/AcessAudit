package com.accessaudit.backend.controller;

import com.accessaudit.backend.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Endpoints de auditorias.
 * Referência: TG3.6 (AccessAudit API.pdf).
 *
 * Todas as operações estão no estado <b>stub</b> (HTTP 501) nesta fase B3.
 * A implementação real entra nas fases B4-B7.
 */
@RestController
@RequestMapping("/api/auditorias")
@Tag(name = "Auditorias", description = "Executa, lista e consulta auditorias de acessibilidade")
public class AuditoriaController {

    @Operation(summary = "Executa uma auditoria de acessibilidade",
            description = """
                    Executa uma auditoria a partir de uma URL ou do código HTML informado.

                    Regras:
                    - se tipoEntrada=URL, o campo url deve ser informado
                    - se tipoEntrada=HTML, o campo html deve ser informado
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Auditoria executada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Entrada inválida",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping
    public ResponseEntity<RelatorioResponse> executar(@Valid @RequestBody AuditoriaRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Operation(summary = "Lista auditorias realizadas",
            description = "Retorna a lista resumida das auditorias executadas pelo sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de auditorias retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<AuditoriaResumoResponse>> listar() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Operation(summary = "Consulta uma auditoria específica",
            description = "Retorna os dados detalhados de uma auditoria, incluindo o relatório.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auditoria encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Auditoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuditoriaDetalheResponse> consultar(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Operation(summary = "Consulta o relatório de uma auditoria",
            description = "Retorna o relatório completo associado a uma auditoria específica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/{id}/relatorio")
    public ResponseEntity<RelatorioResponse> consultarRelatorio(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
