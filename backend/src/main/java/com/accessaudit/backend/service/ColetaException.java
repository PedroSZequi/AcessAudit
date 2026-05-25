package com.accessaudit.backend.service;

import lombok.Getter;

/**
 * Falha na coleta do conteúdo de uma página externa.
 * Será mapeada para HTTP 422 pelo {@code @RestControllerAdvice} (fase B7).
 */
@Getter
public class ColetaException extends RuntimeException {

    public enum Motivo {
        URL_INVALIDA,
        TIMEOUT,
        STATUS_HTTP,
        TIPO_NAO_SUPORTADO,
        FALHA_REDE
    }

    private final Motivo motivo;

    public ColetaException(Motivo motivo, String mensagem) {
        super(mensagem);
        this.motivo = motivo;
    }

    public ColetaException(Motivo motivo, String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.motivo = motivo;
    }
}
