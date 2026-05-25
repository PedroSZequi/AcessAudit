package com.accessaudit.backend.service.regras;

import com.accessaudit.backend.domain.ProblemaAcessibilidade;
import com.accessaudit.backend.domain.RegraCodigo;
import com.accessaudit.backend.domain.Severidade;
import com.accessaudit.backend.service.RegraAuditoria;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Regra WCAG: a página precisa ter um {@code <title>} descritivo.
 * Critério: WCAG 2.1 — 2.4.2 Page Titled.
 *
 * Gera no máximo 1 problema (ausente OU vazio).
 */
@Component
public class RegraTitleAusente implements RegraAuditoria {

    @Override
    public RegraCodigo codigo() {
        return RegraCodigo.TITLE_AUSENTE;
    }

    @Override
    public String nome() {
        return "Página sem título";
    }

    @Override
    public String descricao() {
        return "Verifica se a página possui um elemento <title> com texto descritivo.";
    }

    @Override
    public Severidade severidadePadrao() {
        return Severidade.BAIXA;
    }

    @Override
    public List<ProblemaAcessibilidade> avaliar(Document doc) {
        Elements titles = doc.select("head > title");

        if (titles.isEmpty()) {
            return List.of(ProblemaAcessibilidade.builder()
                    .codigoRegra(codigo())
                    .severidade(severidadePadrao())
                    .descricao("Página sem título descritivo.")
                    .recomendacao(
                            "Adicione um <title> dentro de <head> com um nome conciso e "
                                    + "significativo da página."
                    )
                    .trechoHtml(null)
                    .build());
        }

        Element title = titles.first();
        if (title == null || title.text().isBlank()) {
            return List.of(ProblemaAcessibilidade.builder()
                    .codigoRegra(codigo())
                    .severidade(severidadePadrao())
                    .descricao("Página com <title> vazio.")
                    .recomendacao("Preencha o <title> com um nome significativo da página.")
                    .trechoHtml(title != null ? title.outerHtml() : null)
                    .build());
        }

        return List.of();
    }
}
