package com.accessaudit.backend.dto;

import com.accessaudit.backend.domain.RegraCodigo;
import com.accessaudit.backend.domain.Severidade;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Problema de acessibilidade identificado em uma auditoria.
 * Referência: TG3.6 (esquema problemas[].*) + adendo {@code trechoHtml} para wireframe Tela 4.
 */
@Schema(description = "Problema de acessibilidade identificado na auditoria")
public record ProblemaResponse(

        @Schema(description = "Código da regra violada", example = "IMG_SEM_ALT")
        RegraCodigo codigoRegra,

        @Schema(description = "Nível de severidade do problema", example = "ALTA")
        Severidade severidade,

        @Schema(description = "Descrição do problema identificado", example = "Imagem sem atributo alt.")
        String descricao,

        @Schema(description = "Recomendação para correção",
                example = "Adicione um atributo alt descritivo à imagem.")
        String recomendacao,

        @Schema(description = "Trecho HTML identificado onde o problema ocorre",
                example = "<img src=\"banner.png\">")
        String trechoHtml
) {}
