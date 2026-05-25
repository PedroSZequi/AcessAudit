package com.accessaudit.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Relatório de auditoria — agrega os problemas de acessibilidade identificados.
 * Referência: TG3.5/01-classes_implementacao.puml.
 *
 * Relação 1:1 com {@link Auditoria}.
 * Relação 1:N com {@link ProblemaAcessibilidade}.
 */
@Entity
@Table(name = "relatorio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auditoria_id", nullable = false, unique = true)
    private Auditoria auditoria;

    @Column(name = "gerado_em", nullable = false)
    private LocalDateTime geradoEm;

    @Column(name = "total_problemas", nullable = false)
    private int totalProblemas;

    @OneToMany(mappedBy = "relatorio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProblemaAcessibilidade> problemas = new ArrayList<>();
}
