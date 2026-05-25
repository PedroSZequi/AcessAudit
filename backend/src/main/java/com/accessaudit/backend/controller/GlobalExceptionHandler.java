package com.accessaudit.backend.controller;

import com.accessaudit.backend.dto.ErroResponse;
import com.accessaudit.backend.service.AuditoriaNaoEncontradaException;
import com.accessaudit.backend.service.ColetaException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * Tradutor centralizado de exceções para o envelope padrão definido pela
 * TG3.6 ({@code { status, erro, mensagem, caminho }}).
 *
 * Cobre tudo que estava vazando como 500 + stack trace na fase anterior.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** Bean Validation falhou (ex.: @NotNull, @AssertTrue). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest req) {

        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(this::descricaoCampo)
                .collect(Collectors.joining("; "));

        if (mensagem.isBlank()) {
            mensagem = "Dados de entrada inválidos.";
        }

        return resposta(HttpStatus.BAD_REQUEST, mensagem, req);
    }

    /** JSON malformado, enum desconhecido, body ausente. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> handleBodyMalformado(
            HttpMessageNotReadableException ex, HttpServletRequest req) {

        String mensagem = "Corpo da requisição inválido ou malformado.";
        if (ex.getMostSpecificCause() != null
                && ex.getMostSpecificCause().getMessage() != null
                && ex.getMostSpecificCause().getMessage().contains("not one of the values")) {
            mensagem = "Valor de enum inválido. Verifique tipoEntrada (URL ou HTML).";
        }
        return resposta(HttpStatus.BAD_REQUEST, mensagem, req);
    }

    /** Path param com tipo inválido (UUID malformado, etc.). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> handleTipoIncompativel(
            MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String mensagem = "Parâmetro '" + ex.getName() + "' tem valor inválido.";
        return resposta(HttpStatus.BAD_REQUEST, mensagem, req);
    }

    /** Auditoria com o ID solicitado não existe. */
    @ExceptionHandler(AuditoriaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handleNaoEncontrada(
            AuditoriaNaoEncontradaException ex, HttpServletRequest req) {
        return resposta(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    /** Coleta da página externa falhou. */
    @ExceptionHandler(ColetaException.class)
    public ResponseEntity<ErroResponse> handleColeta(
            ColetaException ex, HttpServletRequest req) {
        log.warn("Falha de coleta ({}): {}", ex.getMotivo(), ex.getMessage());
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req);
    }

    /** Fallback — qualquer coisa não prevista. Não vaza stack trace para o cliente. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGenerico(
            Exception ex, HttpServletRequest req) {
        log.error("Erro não tratado em {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor. Tente novamente em instantes.", req);
    }

    // ---------- helpers ----------

    private ResponseEntity<ErroResponse> resposta(HttpStatus status, String mensagem, HttpServletRequest req) {
        ErroResponse body = new ErroResponse(
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                req.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    private String descricaoCampo(FieldError erro) {
        // mensagens compostas pelo Bean Validation já são bem descritivas
        return erro.getDefaultMessage() != null && !erro.getDefaultMessage().isBlank()
                ? erro.getDefaultMessage()
                : "Campo '" + erro.getField() + "' inválido.";
    }
}
