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
 * Regra WCAG: campos de formulário precisam de um rótulo acessível.
 * Critério: WCAG 2.1 — 3.3.2 Labels or Instructions, 4.1.2 Name, Role, Value.
 *
 * Aceita como rótulo qualquer um destes:
 *  - {@code aria-label} não-vazio
 *  - {@code aria-labelledby} não-vazio
 *  - {@code id} referenciado por um {@code <label for="...">} no documento
 *  - um ancestral {@code <label>} envolvendo o campo
 *
 * Ignora tipos não-interativos: {@code hidden}, {@code submit}, {@code button},
 * {@code reset}, {@code image}.
 */
@Component
public class RegraInputSemLabel implements RegraAuditoria {

    private static final String SELECTOR_CAMPOS =
            "input:not([type=hidden]):not([type=submit]):not([type=button]):not([type=reset]):not([type=image]),"
                    + " textarea,"
                    + " select";

    @Override
    public RegraCodigo codigo() {
        return RegraCodigo.INPUT_SEM_LABEL;
    }

    @Override
    public String nome() {
        return "Campo de formulário sem label";
    }

    @Override
    public String descricao() {
        return "Verifica se campos de formulário possuem rótulo acessível "
                + "(label[for], aria-label, aria-labelledby ou label envolvendo o campo).";
    }

    @Override
    public Severidade severidadePadrao() {
        return Severidade.ALTA;
    }

    @Override
    public List<ProblemaAcessibilidade> avaliar(Document doc) {
        Elements campos = doc.select(SELECTOR_CAMPOS);
        return campos.stream()
                .filter(campo -> !temLabelAcessivel(campo, doc))
                .map(this::montarProblema)
                .toList();
    }

    private boolean temLabelAcessivel(Element campo, Document doc) {
        if (!campo.attr("aria-label").isBlank()) {
            return true;
        }
        if (!campo.attr("aria-labelledby").isBlank()) {
            return true;
        }

        String id = campo.id();
        if (!id.isEmpty()) {
            // CSS attribute selector — escape simples; ids HTML não costumam ter aspas
            Elements labels = doc.select("label[for=\"" + id.replace("\"", "\\\"") + "\"]");
            if (!labels.isEmpty()) {
                return true;
            }
        }

        Element ancestral = campo.parent();
        while (ancestral != null) {
            if ("label".equalsIgnoreCase(ancestral.tagName())) {
                return true;
            }
            ancestral = ancestral.parent();
        }

        return false;
    }

    private ProblemaAcessibilidade montarProblema(Element campo) {
        return ProblemaAcessibilidade.builder()
                .codigoRegra(codigo())
                .severidade(severidadePadrao())
                .descricao("Campo de formulário sem label associado.")
                .recomendacao(
                        "Associe um rótulo usando <label for=\"id-do-campo\"> ou envolvendo "
                                + "o campo. Como alternativa, use aria-label ou aria-labelledby."
                )
                .trechoHtml(campo.outerHtml())
                .build();
    }
}
