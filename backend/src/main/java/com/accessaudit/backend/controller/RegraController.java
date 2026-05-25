package com.accessaudit.backend.controller;

import com.accessaudit.backend.dto.RegraResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints de regras suportadas.
 * Referência: TG3.6 (GET /api/regras).
 *
 * Stub (HTTP 501) na fase B3. Implementação real na fase B5.
 */
@RestController
@RequestMapping("/api/regras")
@Tag(name = "Regras", description = "Lista as regras de acessibilidade suportadas")
public class RegraController {

    @Operation(summary = "Lista as regras de acessibilidade suportadas",
            description = "Retorna a lista de regras de auditoria disponíveis no sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de regras retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<RegraResponse>> listar() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
