package com.accessaudit.backend.service;

import com.accessaudit.backend.domain.Auditoria;
import com.accessaudit.backend.domain.ProblemaAcessibilidade;
import com.accessaudit.backend.domain.Relatorio;
import com.accessaudit.backend.domain.TipoEntrada;
import com.accessaudit.backend.dto.AuditoriaDetalheResponse;
import com.accessaudit.backend.dto.AuditoriaRequest;
import com.accessaudit.backend.dto.AuditoriaResumoResponse;
import com.accessaudit.backend.dto.RelatorioResponse;
import com.accessaudit.backend.repository.AuditoriaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Serviço de auditoria — orquestra coletor, motor e persistência.
 * Referência: TG3.5/01-classes_implementacao.puml — class AuditoriaService.
 *
 * Atende RF01..RF11. Log estruturado por chamada cobre RNF08.
 */
@Service
@Slf4j
public class AuditoriaService {

    private final ColetorConteudo coletor;
    private final MotorAuditoria motor;
    private final AuditoriaRepository repository;

    public AuditoriaService(ColetorConteudo coletor,
                            MotorAuditoria motor,
                            AuditoriaRepository repository) {
        this.coletor = coletor;
        this.motor = motor;
        this.repository = repository;
    }

    /**
     * Executa uma auditoria a partir de URL ou HTML.
     *
     * @return {@link AuditoriaDetalheResponse} com auditoria.id e relatório
     *         embutido. Retorno enriquecido em relação à TG3.6 (que devolvia
     *         apenas o relatório) para permitir navegação no frontend.
     */
    @Transactional
    public AuditoriaDetalheResponse executar(AuditoriaRequest req) {
        long inicio = System.currentTimeMillis();

        // 1) Resolver o HTML a auditar
        String html = req.tipoEntrada() == TipoEntrada.URL
                ? coletor.obterHtml(req.url())
                : req.html();

        // 2) Executar todas as regras
        List<ProblemaAcessibilidade> problemas = motor.analisar(html);

        // 3) Montar entidades + relação bidirecional
        LocalDateTime agora = LocalDateTime.now();
        Auditoria auditoria = Auditoria.builder()
                .tipoEntrada(req.tipoEntrada())
                .url(req.tipoEntrada() == TipoEntrada.URL ? req.url() : null)
                .htmlBruto(req.tipoEntrada() == TipoEntrada.HTML ? req.html() : null)
                .dataExecucao(agora)
                .build();

        Relatorio relatorio = Relatorio.builder()
                .auditoria(auditoria)
                .geradoEm(agora)
                .totalProblemas(problemas.size())
                .build();

        problemas.forEach(p -> p.setRelatorio(relatorio));
        relatorio.getProblemas().addAll(problemas);
        auditoria.setRelatorio(relatorio);

        // 4) Persistir (cascade ALL em Auditoria → Relatorio → Problemas)
        Auditoria salva = repository.save(auditoria);

        // 5) Log estruturado da auditoria (RNF08)
        long duracaoMs = System.currentTimeMillis() - inicio;
        log.info("Auditoria executada: idAuditoria={}, tipoEntrada={}, duracaoMs={}, totalProblemas={}",
                salva.getId(), req.tipoEntrada(), duracaoMs, problemas.size());

        return AuditoriaMapper.toDetalheResponse(salva);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResumoResponse> listar() {
        return repository.findAllByOrderByDataExecucaoDesc().stream()
                .map(AuditoriaMapper::toResumoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AuditoriaDetalheResponse consultar(UUID id) {
        Auditoria auditoria = repository.findById(id)
                .orElseThrow(() -> new AuditoriaNaoEncontradaException(id));
        return AuditoriaMapper.toDetalheResponse(auditoria);
    }

    @Transactional(readOnly = true)
    public RelatorioResponse consultarRelatorio(UUID id) {
        Auditoria auditoria = repository.findById(id)
                .orElseThrow(() -> new AuditoriaNaoEncontradaException(id));
        return AuditoriaMapper.toRelatorioResponse(auditoria.getRelatorio());
    }
}
