# AccessAudit

Ferramenta web para auditoria de acessibilidade digital a partir de uma URL ou do código HTML de uma página.

## Tecnologias
- Java 21
- Spring Boot
- PostgreSQL
- Docker

## Como executar localmente
```bash
git clone https://github.com/SEU-USUARIO/AccessAudit.git
cd AccessAudit
docker-compose up --build
```

## CI/CD
Utilizamos GitHub Actions. Nesta etapa, o pipeline executa uma validação inicial do repositório a cada push e pull request.

## Licença
MIT — veja o arquivo LICENSE.

## Endpoints da API

### accessaudit-service (`:8080`)

| Método | Endpoint | Acesso | Descrição |
|---|---|---|---|
| POST | `/api/auditorias` | Público | Executa uma auditoria de acessibilidade a partir de uma URL ou de um código HTML |
| GET | `/api/auditorias` | Público | Lista as auditorias realizadas |
| GET | `/api/auditorias/{id}` | Público | Consulta uma auditoria específica |
| GET | `/api/auditorias/{id}/relatorio` | Público | Consulta o relatório de uma auditoria específica |
| GET | `/api/regras` | Público | Lista as regras de acessibilidade suportadas |
