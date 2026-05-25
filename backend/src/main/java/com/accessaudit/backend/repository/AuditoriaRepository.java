package com.accessaudit.backend.repository;

import com.accessaudit.backend.domain.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositório JPA para {@link Auditoria}.
 * Referência: TG3.5/01-classes_implementacao.puml — interface AuditoriaRepository.
 */
@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, UUID> {

    /**
     * Lista todas as auditorias ordenadas da mais recente para a mais antiga.
     * Usado pelo endpoint GET /api/auditorias (lista resumida — TG3.6).
     */
    List<Auditoria> findAllByOrderByDataExecucaoDesc();
}
