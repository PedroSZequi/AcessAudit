-- AccessAudit V1: schema inicial
-- Referências: TG3.5/01-classes_implementacao.puml, TG1.2/DCU_DCL_ARQUITETURA.pdf

CREATE TABLE auditoria (
    id              UUID PRIMARY KEY,
    tipo_entrada    VARCHAR(10) NOT NULL CHECK (tipo_entrada IN ('URL', 'HTML')),
    url             TEXT,
    html_bruto      TEXT,
    data_execucao   TIMESTAMP NOT NULL
);

CREATE TABLE relatorio (
    id                  UUID PRIMARY KEY,
    auditoria_id        UUID NOT NULL UNIQUE REFERENCES auditoria(id) ON DELETE CASCADE,
    gerado_em           TIMESTAMP NOT NULL,
    total_problemas     INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE problema_acessibilidade (
    id              UUID PRIMARY KEY,
    relatorio_id    UUID NOT NULL REFERENCES relatorio(id) ON DELETE CASCADE,
    codigo_regra    VARCHAR(50) NOT NULL CHECK (codigo_regra IN (
        'IMG_SEM_ALT',
        'INPUT_SEM_LABEL',
        'LINK_TEXTO_GENERICO',
        'LANG_AUSENTE',
        'TITLE_AUSENTE'
    )),
    severidade      VARCHAR(10) NOT NULL CHECK (severidade IN ('BAIXA', 'MEDIA', 'ALTA')),
    descricao       TEXT NOT NULL,
    recomendacao    TEXT NOT NULL,
    trecho_html     TEXT
);

-- Índices de apoio
CREATE INDEX idx_auditoria_data_execucao   ON auditoria(data_execucao DESC);
CREATE INDEX idx_problema_relatorio_id     ON problema_acessibilidade(relatorio_id);
CREATE INDEX idx_problema_codigo_regra     ON problema_acessibilidade(codigo_regra);
CREATE INDEX idx_problema_severidade       ON problema_acessibilidade(severidade);
