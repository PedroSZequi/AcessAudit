package com.accessaudit.backend.dto;

import com.accessaudit.backend.domain.TipoEntrada;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

/**
 * Dados necessários para executar uma auditoria.
 * Referência: TG3.6 (body do POST /api/auditorias).
 *
 * Regras:
 *  - se {@code tipoEntrada == URL}, o campo {@code url} é obrigatório
 *  - se {@code tipoEntrada == HTML}, o campo {@code html} é obrigatório
 */
@Schema(description = "Dados necessários para executar uma auditoria")
public record AuditoriaRequest(

        @Schema(description = "Tipo de entrada informada para a auditoria",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "tipoEntrada é obrigatório")
        TipoEntrada tipoEntrada,

        @Schema(description = "URL da página a ser auditada", example = "https://exemplo.com")
        String url,

        @Schema(description = "Código HTML da página a ser auditada",
                example = "<html><body><img src=\"a.png\"></body></html>")
        String html
) {

    @AssertTrue(message = "Quando tipoEntrada=URL, o campo url é obrigatório. " +
            "Quando tipoEntrada=HTML, o campo html é obrigatório.")
    @Schema(hidden = true)
    public boolean isEntradaConsistente() {
        if (tipoEntrada == null) {
            // @NotNull cuida da mensagem específica
            return true;
        }
        return switch (tipoEntrada) {
            case URL -> url != null && !url.isBlank();
            case HTML -> html != null && !html.isBlank();
        };
    }
}
