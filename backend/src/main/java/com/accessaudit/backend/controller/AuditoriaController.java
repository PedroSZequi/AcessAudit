package com.accessaudit.backend.controller;

import com.accessaudit.backend.dto.*;
import com.accessaudit.backend.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Endpoints de auditorias.
 * Referência: TG3.6 (AccessAudit API.pdf).
 *
 * <b>Nota de contrato:</b> {@code POST /api/auditorias} retorna
 * {@link AuditoriaDetalheResponse} (não apenas {@link RelatorioResponse}
 * como descrito na TG3.6). A diferença é necessária porque o frontend
 * precisa do {@code auditoria.id} para navegar para
 * {@code GET /api/auditorias/{id}/relatorio} (Tela 3 do wireframe).
 */
@RestController
@RequestMapping("/api/auditorias")
@Tag(name = "Auditorias", description = "Executa, lista e consulta auditorias de acessibilidade")
public class AuditoriaController {

    private final AuditoriaService service;

    public AuditoriaController(AuditoriaService service) {
        this.service = service;
    }

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
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "422", description = "Coleta da URL falhou",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping
    public ResponseEntity<AuditoriaDetalheResponse> executar(
            @Valid @RequestBody AuditoriaRequest request) {
        AuditoriaDetalheResponse detalhe = service.executar(request);
        return ResponseEntity
                .created(URI.create("/api/auditorias/" + detalhe.id()))
                .body(detalhe);
    }

    @Operation(summary = "Lista auditorias realizadas",
            description = "Retorna a lista resumida das auditorias executadas pelo sistema, " +
                    "ordenadas da mais recente para a mais antiga.")
    @ApiResponse(responseCode = "200", description = "Lista de auditorias retornada com sucesso")
    @GetMapping
    public List<AuditoriaResumoResponse> listar() {
        return service.listar();
    }

    @Operation(summary = "Consulta uma auditoria específica",
            description = "Retorna os dados detalhados de uma auditoria, incluindo o relatório.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auditoria encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Auditoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/{id}")
    public AuditoriaDetalheResponse consultar(@PathVariable UUID id) {
        return service.consultar(id);
    }

    @Operation(summary = "Consulta o relatório de uma auditoria",
            description = "Retorna o relatório completo associado a uma auditoria específica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/{id}/relatorio")
    public RelatorioResponse consultarRelatorio(@PathVariable UUID id) {
        return service.consultarRelatorio(id);
    }
}
