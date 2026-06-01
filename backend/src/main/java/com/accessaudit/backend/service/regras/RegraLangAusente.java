package com.accessaudit.backend.service.regras;

import com.accessaudit.backend.domain.ProblemaAcessibilidade;
import com.accessaudit.backend.domain.RegraCodigo;
import com.accessaudit.backend.domain.Severidade;
import com.accessaudit.backend.service.RegraAuditoria;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Regra WCAG: o elemento {@code <html>} precisa declarar o idioma da página.
 * Critério: WCAG 2.1 — 3.1.1 Language of Page.
 *
 * Gera no máximo 1 problema. Aceita tanto {@code lang} quanto {@code xml:lang}
 * (compatibilidade com XHTML).
 */
@Component
public class RegraLangAusente implements RegraAuditoria {

    @Override
    public RegraCodigo codigo() {
        return RegraCodigo.LANG_AUSENTE;
    }

    @Override
    public String nome() {
        return "Atributo lang ausente no <html>";
    }

    @Override
    public String descricao() {
        return "Verifica se o elemento <html> declara o idioma da página via atributo lang.";
    }

    @Override
    public Severidade severidadePadrao() {
        return Severidade.BAIXA;
    }

    @Override
    public List<ProblemaAcessibilidade> avaliar(Document doc) {
        Element html = doc.selectFirst("html");
        if (html == null) {
            // sem <html> — outro problema, fora do escopo desta regra
            return List.of();
        }

        boolean temLang = !html.attr("lang").isBlank() || !html.attr("xml:lang").isBlank();
        if (temLang) {
            return List.of();
        }

        return List.of(ProblemaAcessibilidade.builder()
                .codigoRegra(codigo())
                .severidade(severidadePadrao())
                .descricao("Elemento <html> sem atributo lang.")
                .recomendacao(
                        "Adicione o atributo lang no <html> indicando o idioma principal "
                                + "da página (ex.: lang=\"pt-BR\"). Leitores de tela usam isso "
                                + "para escolher a pronúncia correta."
                )
                .trechoHtml(snippetInicial(html))
                .build());
    }

    /** Devolve só a tag de abertura de &lt;html&gt; (evita despejar a página inteira). */
    private String snippetInicial(Element html) {
        String raw = html.outerHtml();
        int fimAbertura = raw.indexOf('>');
        if (fimAbertura > 0 && fimAbertura < 200) {
            return raw.substring(0, fimAbertura + 1);
        }
        return "<html>";
    }
}
