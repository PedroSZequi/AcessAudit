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
import java.util.Set;

/**
 * Regra WCAG: links devem ter texto descritivo do destino.
 * Critério: WCAG 2.1 — 2.4.4 Link Purpose (In Context), 2.4.9 Link Purpose (Link Only).
 *
 * Detecta {@code <a>} cujo texto visível pertence a uma lista de termos
 * genéricos ("clique aqui", "saiba mais", "leia mais", ...).
 *
 * Não sinaliza links com {@code aria-label} ou {@code aria-labelledby}
 * preenchido (assume-se que o autor compensou ali) nem links totalmente vazios
 * (esses são outro problema — não cobertos por esta regra).
 */
@Component
public class RegraLinkTextoGenerico implements RegraAuditoria {

    private static final Set<String> TERMOS_GENERICOS = Set.of(
            "clique aqui",
            "clique",
            "aqui",
            "saiba mais",
            "leia mais",
            "veja mais",
            "ver mais",
            "mais",
            "link",
            "este link",
            "esse link",
            "neste link",
            "nesse link",
            "click here",
            "click",
            "here",
            "read more",
            "learn more",
            "more"
    );

    @Override
    public RegraCodigo codigo() {
        return RegraCodigo.LINK_TEXTO_GENERICO;
    }

    @Override
    public String nome() {
        return "Link com texto genérico";
    }

    @Override
    public String descricao() {
        return "Verifica se links têm texto descritivo do destino "
                + "(evita \"clique aqui\", \"saiba mais\", etc.).";
    }

    @Override
    public Severidade severidadePadrao() {
        return Severidade.MEDIA;
    }

    @Override
    public List<ProblemaAcessibilidade> avaliar(Document doc) {
        Elements links = doc.select("a[href]");
        return links.stream()
                .filter(this::temTextoGenerico)
                .map(this::montarProblema)
                .toList();
    }

    private boolean temTextoGenerico(Element link) {
        // aria-label não-vazio supre o texto visível
        if (!link.attr("aria-label").isBlank()) return false;
        if (!link.attr("aria-labelledby").isBlank()) return false;

        String texto = link.text().trim().toLowerCase();
        if (texto.isEmpty()) return false; // link vazio é outro problema (fora do escopo)

        return TERMOS_GENERICOS.contains(texto);
    }

    private ProblemaAcessibilidade montarProblema(Element link) {
        return ProblemaAcessibilidade.builder()
                .codigoRegra(codigo())
                .severidade(severidadePadrao())
                .descricao("Link com texto genérico: \"" + link.text().trim() + "\".")
                .recomendacao(
                        "Substitua por um texto que descreva o destino do link "
                                + "(ex.: \"Política de privacidade\" em vez de \"Clique aqui\"). "
                                + "Alternativa: forneça contexto via aria-label."
                )
                .trechoHtml(link.outerHtml())
                .build();
    }
}
