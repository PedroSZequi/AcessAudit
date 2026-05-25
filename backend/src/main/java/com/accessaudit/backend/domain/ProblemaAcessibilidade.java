package com.accessaudit.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Problema de acessibilidade identificado em uma auditoria.
 * Referência: TG3.5/01-classes_implementacao.puml.
 *
 * O campo {@code trechoHtml} foi acordado em adendo ao contrato da TG3.6
 * para suportar a Tela 4 dos wireframes (modal de detalhe do problema).
 */
@Entity
@Table(name = "problema_acessibilidade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemaAcessibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relatorio_id", nullable = false)
    private Relatorio relatorio;

    @Enumerated(EnumType.STRING)
    @Column(name = "codigo_regra", nullable = false, length = 50)
    private RegraCodigo codigoRegra;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Severidade severidade;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String recomendacao;

    @Column(name = "trecho_html", columnDefinition = "TEXT")
    private String trechoHtml;
}
