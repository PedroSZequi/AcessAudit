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
 * Regra WCAG: imagens devem possuir texto alternativo via atributo {@code alt}.
 * Critério: WCAG 2.1 — 1.1.1 Non-text Content.
 *
 * Sinaliza qualquer {@code <img>} sem o atributo {@code alt}. Imagens
 * decorativas devem usar explicitamente {@code alt=""} (não são sinalizadas).
 */
@Component
public class RegraImgSemAlt implements RegraAuditoria {

    @Override
    public RegraCodigo codigo() {
        return RegraCodigo.IMG_SEM_ALT;
    }

    @Override
    public String nome() {
        return "Imagem sem ALT";
    }

    @Override
    public String descricao() {
        return "Verifica se imagens possuem o atributo alt.";
    }

    @Override
    public Severidade severidadePadrao() {
        return Severidade.ALTA;
    }

    @Override
    public List<ProblemaAcessibilidade> avaliar(Document doc) {
        Elements imagensSemAlt = doc.select("img:not([alt])");
        return imagensSemAlt.stream()
                .map(this::montarProblema)
                .toList();
    }

    private ProblemaAcessibilidade montarProblema(Element img) {
        return ProblemaAcessibilidade.builder()
                .codigoRegra(codigo())
                .severidade(severidadePadrao())
                .descricao("Imagem sem atributo alt.")
                .recomendacao(
                        "Adicione o atributo alt com uma descrição significativa "
                                + "(ex.: alt=\"Banner de promoção de verão\"). "
                                + "Para imagens decorativas, use alt=\"\"."
                )
                .trechoHtml(img.outerHtml())
                .build();
    }
}
