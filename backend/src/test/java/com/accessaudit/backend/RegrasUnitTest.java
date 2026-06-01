package com.accessaudit.backend;

import com.accessaudit.backend.domain.ProblemaAcessibilidade;
import com.accessaudit.backend.domain.RegraCodigo;
import com.accessaudit.backend.domain.Severidade;
import com.accessaudit.backend.service.regras.RegraImgSemAlt;
import com.accessaudit.backend.service.regras.RegraInputSemLabel;
import com.accessaudit.backend.service.regras.RegraLangAusente;
import com.accessaudit.backend.service.regras.RegraLinkTextoGenerico;
import com.accessaudit.backend.service.regras.RegraTitleAusente;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários isolados das 5 regras WCAG. Não precisam de Spring context.
 */
class RegrasUnitTest {

    private static Document parse(String html) {
        return Jsoup.parse(html);
    }

    // =========================================================================
    @Nested
    @DisplayName("RegraImgSemAlt")
    class TestImgSemAlt {

        private final RegraImgSemAlt regra = new RegraImgSemAlt();

        @Test
        void sinalizaImagemSemAtributoAlt() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse("<img src='a.png'>"));
            assertThat(p).hasSize(1);
            assertThat(p.get(0).getCodigoRegra()).isEqualTo(RegraCodigo.IMG_SEM_ALT);
            assertThat(p.get(0).getSeveridade()).isEqualTo(Severidade.ALTA);
            assertThat(p.get(0).getTrechoHtml()).contains("<img src=\"a.png\">");
        }

        @Test
        void naoSinalizaImagemComAltVazio() {
            // alt="" é correto para imagens decorativas
            List<ProblemaAcessibilidade> p = regra.avaliar(parse("<img src='a.png' alt=''>"));
            assertThat(p).isEmpty();
        }

        @Test
        void naoSinalizaImagemComAltPreenchido() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse("<img src='a.png' alt='Banner'>"));
            assertThat(p).isEmpty();
        }

        @Test
        void sinalizaTodasAsImagensSemAlt() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse(
                    "<img src='a.png'><img src='b.png'><img src='c.png' alt='ok'>"));
            assertThat(p).hasSize(2);
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("RegraInputSemLabel")
    class TestInputSemLabel {

        private final RegraInputSemLabel regra = new RegraInputSemLabel();

        @Test
        void sinalizaInputSemRotulo() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse("<input type='text'>"));
            assertThat(p).hasSize(1);
            assertThat(p.get(0).getCodigoRegra()).isEqualTo(RegraCodigo.INPUT_SEM_LABEL);
        }

        @Test
        void aceitaInputComAriaLabel() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse("<input aria-label='Nome'>"));
            assertThat(p).isEmpty();
        }

        @Test
        void aceitaInputComLabelFor() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse(
                    "<label for='e'>Email</label><input id='e' type='email'>"));
            assertThat(p).isEmpty();
        }

        @Test
        void aceitaInputEnvolvidoPorLabel() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse(
                    "<label>Email <input type='email'></label>"));
            assertThat(p).isEmpty();
        }

        @Test
        void ignoraInputsTipoNaoInterativo() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse(
                    "<input type='hidden'><input type='submit'><button type='button'>x</button>"));
            assertThat(p).isEmpty();
        }

        @Test
        void sinalizaTextareaSemRotulo() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse("<textarea></textarea>"));
            assertThat(p).hasSize(1);
        }

        @Test
        void sinalizaSelectSemRotulo() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse(
                    "<select><option>a</option></select>"));
            assertThat(p).hasSize(1);
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("RegraTitleAusente")
    class TestTitleAusente {

        private final RegraTitleAusente regra = new RegraTitleAusente();

        @Test
        void sinalizaTitleAusente() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse("<html><head></head></html>"));
            assertThat(p).hasSize(1);
            assertThat(p.get(0).getDescricao()).contains("sem título");
        }

        @Test
        void sinalizaTitleVazio() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse(
                    "<html><head><title></title></head></html>"));
            assertThat(p).hasSize(1);
            assertThat(p.get(0).getDescricao()).contains("vazio");
        }

        @Test
        void naoSinalizaTitlePreenchido() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse(
                    "<html><head><title>Minha Página</title></head></html>"));
            assertThat(p).isEmpty();
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("RegraLinkTextoGenerico")
    class TestLinkTextoGenerico {

        private final RegraLinkTextoGenerico regra = new RegraLinkTextoGenerico();

        @Test
        void sinalizaCliqueAqui() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse(
                    "<a href='/p'>Clique aqui</a>"));
            assertThat(p).hasSize(1);
            assertThat(p.get(0).getCodigoRegra()).isEqualTo(RegraCodigo.LINK_TEXTO_GENERICO);
        }

        @Test
        void sinalizaSaibaMaisIndependentementeDoCase() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse(
                    "<a href='/p'>SAIBA MAIS</a>"));
            assertThat(p).hasSize(1);
        }

        @Test
        void naoSinalizaLinkComTextoDescritivo() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse(
                    "<a href='/p'>Política de privacidade</a>"));
            assertThat(p).isEmpty();
        }

        @Test
        void aceitaLinkGenericoComAriaLabel() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse(
                    "<a href='/p' aria-label='Política de privacidade'>Clique aqui</a>"));
            assertThat(p).isEmpty();
        }

        @Test
        void naoSinalizaLinkVazio() {
            // link vazio é outro tipo de problema, fora do escopo desta regra
            List<ProblemaAcessibilidade> p = regra.avaliar(parse("<a href='/p'></a>"));
            assertThat(p).isEmpty();
        }
    }

    // =========================================================================
    @Nested
    @DisplayName("RegraLangAusente")
    class TestLangAusente {

        private final RegraLangAusente regra = new RegraLangAusente();

        @Test
        void sinalizaHtmlSemLang() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse("<html><body></body></html>"));
            assertThat(p).hasSize(1);
            assertThat(p.get(0).getCodigoRegra()).isEqualTo(RegraCodigo.LANG_AUSENTE);
            assertThat(p.get(0).getSeveridade()).isEqualTo(Severidade.BAIXA);
        }

        @Test
        void naoSinalizaHtmlComLang() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse(
                    "<html lang='pt-BR'><body></body></html>"));
            assertThat(p).isEmpty();
        }

        @Test
        void naoSinalizaHtmlComXmlLang() {
            List<ProblemaAcessibilidade> p = regra.avaliar(parse(
                    "<html xml:lang='en'><body></body></html>"));
            assertThat(p).isEmpty();
        }
    }
}
