# AccessAudit

[![CI](https://github.com/PedroSZequi/AcessAudit/actions/workflows/ci.yml/badge.svg)](https://github.com/PedroSZequi/AcessAudit/actions/workflows/ci.yml)

Sistema de **auditoria de acessibilidade digital** para páginas web. O usuário informa uma URL ou cola um HTML; o sistema executa regras WCAG 2.1, classifica os problemas por severidade e devolve um relatório com descrição e recomendação de correção para cada item.

Projeto de **Trabalho de Graduação** — Laboratório de Engenharia de Software 06N.
Mihael Rommel Barbosa Xavier (RA 10239617) · Pedro de Souza Zequi (RA 10419805).

---

## Monorepo

Este repositório hospeda duas aplicações separadas, cada uma com sua própria documentação detalhada:

| Aplicação | Stack | README |
|---|---|---|
| **Backend** — API REST | Java 21 · Spring Boot 4 · PostgreSQL · Jsoup | [`backend/README.md`](backend/README.md) |
| **Frontend** — Interface web | React 19 · TypeScript · Vite · Tailwind v4 | [`frontend/README.md`](frontend/README.md) |

A orquestração de produção (Postgres + backend + frontend, com Nginx fazendo proxy de `/api/*`) está em [`docker-compose.yml`](docker-compose.yml).

---

## Como subir tudo de uma vez

Com **Docker** (recomendado para experimentar o produto):

```bash
docker compose up --build
```

Sobe os 3 contêineres. Acesse:
- Aplicação completa: `http://localhost`
- API direta (porta exposta para debug): `http://localhost:8080/api/...`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

Para parar: `docker compose down`. Para apagar o volume do banco: `docker compose down -v`.

### Sem Docker (para desenvolvimento)

Cada aplicação tem instruções próprias nos respectivos READMEs ([backend](backend/README.md) · [frontend](frontend/README.md)).

---

## Pipeline de CI

Configurado em [`.github/workflows/ci.yml`](.github/workflows/ci.yml). A cada push e pull request em `main`, executa dois jobs em paralelo:

| Job | Faz |
|---|---|
| **Backend** | Sobe Postgres em service container · `./mvnw verify` · `./mvnw test` (unitários + integração) · publica relatório do Surefire como artifact |
| **Frontend** | `npm ci` · `npm run lint` · `npx tsc --noEmit` · `npm run build` · publica o `dist/` como artifact |

O badge no topo deste arquivo reflete o estado do último run em `main`. Histórico completo em [Actions](https://github.com/PedroSZequi/AcessAudit/actions).

---

## Status do projeto

### ✅ Pronto
- **Backend**: API REST com 5 endpoints, 5 regras WCAG, persistência com Flyway, error handling padronizado, observabilidade via Actuator, cobertura de testes unitários e integração. Detalhes em [backend/README.md](backend/README.md).
- **Frontend**: 4 telas do wireframe (Home, Loading, Relatório, Modal) + página de Histórico, cliente HTTP tipado, acessibilidade própria. Detalhes em [frontend/README.md](frontend/README.md).
- **Infra**: containers multi-stage, `docker-compose` orquestrando os 3 serviços com reverse proxy Nginx.
- **CI**: pipeline GitHub Actions com jobs reais de build e teste.

### 🚧 Pendente (depende de decisão / recurso do time)

| Item | Bloqueador |
|---|---|
| Deploy em AWS (EC2 + RDS + VPC + SG) | Credencial AWS, escolha entre Elastic Beanstalk / ECS Fargate / EC2 puro, provisionamento de domínio |
| Certificado TLS (ACM) e domínio público | Definição do domínio |
| Saída de logs em JSON estruturado | Decisão do agregador (CloudWatch nativo? outro?) |

---

## Licença

MIT — veja [LICENSE](LICENSE).
