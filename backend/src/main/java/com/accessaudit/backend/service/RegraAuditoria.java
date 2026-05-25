package com.accessaudit.backend.service;

import com.accessaudit.backend.domain.ProblemaAcessibilidade;
import com.accessaudit.backend.domain.RegraCodigo;
import com.accessaudit.backend.domain.Severidade;
import org.jsoup.nodes.Document;

import java.util.List;

/**
 * Regra de auditoria de acessibilidade.
 * Referência: TG3.5/01-classes_implementacao.puml — interface RegraAuditoria.
 *
 * Cada implementação cobre um critério WCAG. Atende RNF07
 * (código modular, permitindo adicionar novas regras).
 *
 * Métodos de metadado ({@link #codigo}, {@link #nome}, etc.) alimentam o
 * endpoint {@code GET /api/regras} (TG3.6).
 */
public interface RegraAuditoria {

    /** Código único da regra (enum). */
    RegraCodigo codigo();

    /** Nome legível para exibição (ex.: "Imagem sem ALT"). */
    String nome();

    /** Descrição curta do que a regra verifica. */
    String descricao();

    /** Severidade atribuída por padrão aos problemas detectados por esta regra. */
    Severidade severidadePadrao();

    /**
     * Avalia o documento e retorna a lista de problemas detectados.
     * As instâncias retornadas NÃO devem ter {@code relatorio} setado — o
     * service preenche essa relação antes de persistir.
     */
    List<ProblemaAcessibilidade> avaliar(Document doc);
}
