package com.accessaudit.backend.controller;

import com.accessaudit.backend.dto.RegraResponse;
import com.accessaudit.backend.service.MotorAuditoria;
import com.accessaudit.backend.service.RegraAuditoria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

/**
 * Endpoint que lista as regras de acessibilidade carregadas no motor.
 * Referência: TG3.6 (GET /api/regras).
 */
@RestController
@RequestMapping("/api/regras")
@Tag(name = "Regras", description = "Lista as regras de acessibilidade suportadas")
public class RegraController {

    private final MotorAuditoria motorAuditoria;

    public RegraController(MotorAuditoria motorAuditoria) {
        this.motorAuditoria = motorAuditoria;
    }

    @Operation(summary = "Lista as regras de acessibilidade suportadas",
            description = """
                    Retorna a lista de regras de auditoria disponíveis no sistema.

                    Cada regra registrada como @Component implementando RegraAuditoria
                    é automaticamente incluída — atende ao RNF07 (modularidade).
                    """)
    @ApiResponse(responseCode = "200", description = "Lista de regras retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<RegraResponse>> listar() {
        List<RegraResponse> resposta = motorAuditoria.getRegras().stream()
                .sorted(Comparator.comparing(r -> r.codigo().name()))
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(resposta);
    }

    private RegraResponse toResponse(RegraAuditoria regra) {
        return new RegraResponse(
                regra.codigo(),
                regra.nome(),
                regra.descricao(),
                regra.severidadePadrao()
        );
    }
}
