package com.accessaudit.backend.domain;

/**
 * Código identificador de cada regra de acessibilidade suportada pelo sistema.
 * Referência: TG3.5/01-classes_implementacao.puml e TG3.6 (enum codigoRegra).
 *
 * As 5 regras cobrem RF08 (identificar problemas de acessibilidade).
 */
public enum RegraCodigo {
    IMG_SEM_ALT,
    INPUT_SEM_LABEL,
    LINK_TEXTO_GENERICO,
    LANG_AUSENTE,
    TITLE_AUSENTE
}
