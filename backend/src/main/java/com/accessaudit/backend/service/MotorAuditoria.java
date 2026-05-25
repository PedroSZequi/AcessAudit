package com.accessaudit.backend.service;

import com.accessaudit.backend.domain.ProblemaAcessibilidade;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Motor de auditoria — recebe o HTML, executa todas as regras WCAG e
 * devolve a lista consolidada de problemas.
 * Referência: TG3.5/01-classes_implementacao.puml — class MotorAuditoria.
 *
 * Atende RF07 (analisar conteúdo HTML) + RNF07 (modularidade via injeção
 * de {@code List<RegraAuditoria>} — basta criar um novo {@code @Component}
 * implementando {@link RegraAuditoria} para a regra entrar no motor).
 */
@Component
@Slf4j
public class MotorAuditoria {

    private final List<RegraAuditoria> regras;

    public MotorAuditoria(List<RegraAuditoria> regras) {
        this.regras = regras;
        log.info("MotorAuditoria carregado com {} regra(s): {}",
                regras.size(),
                regras.stream().map(r -> r.codigo().name()).toList());
    }

    /**
     * Analisa o HTML e retorna a lista de problemas encontrados.
     *
     * @param html código HTML completo da página
     * @return lista de problemas (sem {@code relatorio} setado — preenchido pelo service)
     */
    public List<ProblemaAcessibilidade> analisar(String html) {
        Document doc = Jsoup.parse(html);
        List<ProblemaAcessibilidade> consolidado = new ArrayList<>();

        for (RegraAuditoria regra : regras) {
            try {
                List<ProblemaAcessibilidade> problemas = regra.avaliar(doc);
                log.debug("Regra {} encontrou {} problema(s)",
                        regra.codigo(), problemas.size());
                consolidado.addAll(problemas);
            } catch (Exception e) {
                // Isolamento: uma regra com bug não derruba a auditoria toda
                log.error("Falha ao executar regra {}: {}",
                        regra.codigo(), e.getMessage(), e);
            }
        }

        return consolidado;
    }

    /** Acesso à lista de regras (usado pelo endpoint GET /api/regras). */
    public List<RegraAuditoria> getRegras() {
        return List.copyOf(regras);
    }
}
