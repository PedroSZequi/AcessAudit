package com.accessaudit.backend.service;

import lombok.Getter;

import java.util.UUID;

/**
 * Auditoria com o ID informado não existe.
 * Mapeada para HTTP 404 pelo {@code @RestControllerAdvice} (fase B7).
 */
@Getter
public class AuditoriaNaoEncontradaException extends RuntimeException {

    private final UUID id;

    public AuditoriaNaoEncontradaException(UUID id) {
        super("Auditoria não encontrada: " + id);
        this.id = id;
    }
}
