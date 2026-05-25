package com.accessaudit.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Auditoria de acessibilidade executada pelo sistema.
 * Referência: TG3.5/01-classes_implementacao.puml e DCL da TG1.2.
 */
@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entrada", nullable = false, length = 10)
    private TipoEntrada tipoEntrada;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(name = "html_bruto", columnDefinition = "TEXT")
    private String htmlBruto;

    @Column(name = "data_execucao", nullable = false)
    private LocalDateTime dataExecucao;

    @OneToOne(mappedBy = "auditoria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Relatorio relatorio;
}
