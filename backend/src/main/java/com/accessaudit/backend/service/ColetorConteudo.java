package com.accessaudit.backend.service;

/**
 * Coleta o conteúdo HTML de uma página externa.
 * Referência: TG3.5/01-classes_implementacao.puml — interface ColetorConteudo.
 *
 * Atende RF06 (obter conteúdo da página quando uma URL for informada).
 */
public interface ColetorConteudo {

    /**
     * Obtém o HTML bruto da URL informada.
     *
     * @param url URL absoluta (http/https)
     * @return código HTML da página
     * @throws ColetaException se a coleta falhar (timeout, 4xx/5xx, MIME inválido, etc.)
     */
    String obterHtml(String url) throws ColetaException;
}
