package com.accessaudit.backend.dto;

import com.accessaudit.backend.domain.RegraCodigo;
import com.accessaudit.backend.domain.Severidade;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Regra de acessibilidade suportada pelo sistema.
 * Referência: TG3.6 (GET /api/regras).
 */
@Schema(description = "Regra de acessibilidade suportada pelo sistema")
public record RegraResponse(

        @Schema(description = "Código identificador da regra", example = "IMG_SEM_ALT")
        RegraCodigo codigo,

        @Schema(description = "Nome descritivo da regra", example = "Imagem sem ALT")
        String nome,

        @Schema(description = "Descrição da verificação realizada",
                example = "Verifica se imagens possuem o atributo alt.")
        String descricao,

        @Schema(description = "Severidade padrão atribuída a problemas desta regra")
        Severidade severidadePadrao
) {}
