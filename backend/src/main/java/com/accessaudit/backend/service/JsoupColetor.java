package com.accessaudit.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

/**
 * Implementação de {@link ColetorConteudo} usando Jsoup.
 * Respeita o timeout configurado em {@code accessaudit.coletor.timeoutMs}
 * para atender RNF02 (resultado em até 5s).
 */
@Component
@Slf4j
public class JsoupColetor implements ColetorConteudo {

    private static final String USER_AGENT =
            "AccessAudit/1.0 (+https://github.com/PedroSZequi/AcessAudit)";

    private final int timeoutMs;

    public JsoupColetor(@Value("${accessaudit.coletor.timeoutMs}") int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    @Override
    public String obterHtml(String url) {
        long inicio = System.currentTimeMillis();
        try {
            String html = Jsoup.connect(url)
                    .timeout(timeoutMs)
                    .userAgent(USER_AGENT)
                    .followRedirects(true)
                    .ignoreContentType(false)
                    .get()
                    .outerHtml();

            log.info("Coleta concluída em {}ms para {}", System.currentTimeMillis() - inicio, url);
            return html;

        } catch (MalformedURLException e) {
            throw new ColetaException(ColetaException.Motivo.URL_INVALIDA,
                    "URL informada é inválida: " + url, e);

        } catch (SocketTimeoutException e) {
            throw new ColetaException(ColetaException.Motivo.TIMEOUT,
                    "Tempo limite (" + timeoutMs + "ms) excedido ao coletar a página.", e);

        } catch (HttpStatusException e) {
            throw new ColetaException(ColetaException.Motivo.STATUS_HTTP,
                    "Página retornou status " + e.getStatusCode() + ".", e);

        } catch (UnsupportedMimeTypeException e) {
            throw new ColetaException(ColetaException.Motivo.TIPO_NAO_SUPORTADO,
                    "Conteúdo da URL não é HTML (Content-Type: " + e.getMimeType() + ").", e);

        } catch (IOException e) {
            throw new ColetaException(ColetaException.Motivo.FALHA_REDE,
                    "Falha de rede ao acessar a página: " + e.getMessage(), e);
        }
    }
}
