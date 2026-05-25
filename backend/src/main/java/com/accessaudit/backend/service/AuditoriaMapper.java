package com.accessaudit.backend.service;

import com.accessaudit.backend.domain.Auditoria;
import com.accessaudit.backend.domain.ProblemaAcessibilidade;
import com.accessaudit.backend.domain.Relatorio;
import com.accessaudit.backend.dto.AuditoriaDetalheResponse;
import com.accessaudit.backend.dto.AuditoriaResumoResponse;
import com.accessaudit.backend.dto.ProblemaResponse;
import com.accessaudit.backend.dto.RelatorioResponse;

import java.util.List;

/**
 * Conversões entity ↔ DTO. Métodos estáticos — sem estado.
 */
final class AuditoriaMapper {

    private AuditoriaMapper() {
        // utility class
    }

    static AuditoriaResumoResponse toResumoResponse(Auditoria auditoria) {
        return new AuditoriaResumoResponse(
                auditoria.getId(),
                auditoria.getTipoEntrada(),
                auditoria.getUrl(),
                auditoria.getDataExecucao()
        );
    }

    static AuditoriaDetalheResponse toDetalheResponse(Auditoria auditoria) {
        return new AuditoriaDetalheResponse(
                auditoria.getId(),
                auditoria.getTipoEntrada(),
                auditoria.getUrl(),
                auditoria.getHtmlBruto(),
                auditoria.getDataExecucao(),
                toRelatorioResponse(auditoria.getRelatorio())
        );
    }

    static RelatorioResponse toRelatorioResponse(Relatorio relatorio) {
        List<ProblemaResponse> problemas = relatorio.getProblemas().stream()
                .map(AuditoriaMapper::toProblemaResponse)
                .toList();
        return new RelatorioResponse(
                relatorio.getId(),
                relatorio.getGeradoEm(),
                relatorio.getTotalProblemas(),
                problemas
        );
    }

    private static ProblemaResponse toProblemaResponse(ProblemaAcessibilidade problema) {
        return new ProblemaResponse(
                problema.getCodigoRegra(),
                problema.getSeveridade(),
                problema.getDescricao(),
                problema.getRecomendacao(),
                problema.getTrechoHtml()
        );
    }
}
