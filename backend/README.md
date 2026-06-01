# AccessAudit — Backend

API REST que executa auditorias de acessibilidade WCAG 2.1 a partir de uma URL ou de código HTML, e devolve um relatório com os problemas encontrados, classificados por severidade.

## Stack

- **Java 21** (LTS)
- **Spring Boot 4.0**
- **Maven** + wrapper `./mvnw`
- **PostgreSQL 16** + **Flyway** (migrations versionadas)
- **Spring Data JPA**
- **Jsoup 1.18** (parsing HTML e coleta de URLs)
- **Springdoc OpenAPI 2.7** (Swagger UI)
- **Lombok**
- **JUnit 5** + **MockMvc** + **AssertJ**

## Arquitetura

Padrão em camadas, conforme [diagrama de classes da TG3.5](https://github.com/PedroSZequi/AcessAudit/wiki):

```
HTTP / JSON
     ↓
┌──────────────────────────────────────────────────┐
│ controller/                                      │
│   AuditoriaController · RegraController          │
│   GlobalExceptionHandler (envelope padrão)       │
└──────────────────────┬───────────────────────────┘
                       ↓
┌──────────────────────────────────────────────────┐
│ service/                                         │
│   AuditoriaService (orquestração)                │
│   ┌──────────────────────────────────────────┐   │
│   │ ColetorConteudo ← JsoupColetor           │   │
│   │ MotorAuditoria ← List<RegraAuditoria>    │   │
│   │   ├─ RegraImgSemAlt                      │   │
│   │   ├─ RegraInputSemLabel                  │   │
│   │   ├─ RegraLinkTextoGenerico              │   │
│   │   ├─ RegraTitleAusente                   │   │
│   │   └─ RegraLangAusente                    │   │
│   └──────────────────────────────────────────┘   │
└──────────────────────┬───────────────────────────┘
                       ↓
┌──────────────────────────────────────────────────┐
│ repository/  AuditoriaRepository (Spring Data)   │
└──────────────────────┬───────────────────────────┘
                       ↓
                 PostgreSQL
```

## Estrutura

```
backend/
├── src/main/java/com/accessaudit/backend/
│   ├── BackendApplication.java
│   ├── config/        WebConfig (CORS)
│   ├── controller/    REST + GlobalExceptionHandler
│   ├── dto/           DTOs de entrada/saída
│   ├── domain/        Entidades JPA + enums
│   ├── repository/    Spring Data JPA
│   └── service/       Service + Coletor + Motor + Regras
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/  Flyway V1__init.sql
├── src/test/java/...  Testes (unitários + integração)
├── Dockerfile         Multi-stage (maven → jre)
└── pom.xml
```

## Como rodar localmente

### Pré-requisitos
- JDK 21 (recomendado: Temurin)
- Maven (ou usar o wrapper incluso)
- PostgreSQL 16 rodando local com banco `accessaudit`

```bash
# Setup do banco (apenas na primeira vez)
brew install postgresql@16
brew services start postgresql@16
createdb accessaudit

# Subir a aplicação
./mvnw spring-boot:run
```

Sobe em `http://localhost:8080`.

### Variáveis de ambiente (com defaults)

| Variável | Default | Descrição |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/accessaudit` | URL JDBC do Postgres |
| `SPRING_DATASOURCE_USERNAME` | usuário do macOS | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | (vazio) | Senha do banco |
| `ACCESSAUDIT_CORS_ALLOWEDORIGINS` | `http://localhost:5173,http://127.0.0.1:5173` | Origens permitidas para CORS |
| `accessaudit.coletor.timeoutMs` | `4000` | Timeout em ms da coleta Jsoup (RNF02 — SLA 5s) |

## Endpoints

Documentação interativa: **`http://localhost:8080/swagger-ui/index.html`**

| Método | Path | Descrição |
|---|---|---|
| POST | `/api/auditorias` | Executa uma auditoria por URL ou HTML |
| GET | `/api/auditorias` | Lista resumida de auditorias executadas |
| GET | `/api/auditorias/{id}` | Auditoria com relatório embutido |
| GET | `/api/auditorias/{id}/relatorio` | Apenas o relatório |
| GET | `/api/regras` | Regras WCAG carregadas no motor |

### Envelope de erro padrão

Todas as exceções são traduzidas pelo `GlobalExceptionHandler` para:

```json
{
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "URL inválida informada para auditoria.",
  "caminho": "/api/auditorias"
}
```

Códigos:
- **400** — body/path inválido (validação ou enum desconhecido)
- **404** — auditoria não encontrada
- **422** — coleta da URL falhou (timeout, 4xx/5xx na página, MIME não-HTML)
- **500** — fallback (sem vazar stack trace)

## Regras WCAG

| Código | Severidade | Critério WCAG 2.1 |
|---|---|---|
| `IMG_SEM_ALT` | ALTA | 1.1.1 Non-text Content |
| `INPUT_SEM_LABEL` | ALTA | 3.3.2 Labels or Instructions · 4.1.2 Name, Role, Value |
| `LINK_TEXTO_GENERICO` | MÉDIA | 2.4.4 Link Purpose (In Context) |
| `LANG_AUSENTE` | BAIXA | 3.1.1 Language of Page |
| `TITLE_AUSENTE` | BAIXA | 2.4.2 Page Titled |

### Como adicionar uma nova regra (RNF07 — modularidade)

Crie uma classe em `service/regras/` implementando `RegraAuditoria`:

```java
@Component
public class RegraImgAltRedundante implements RegraAuditoria {
    @Override public RegraCodigo codigo() { return RegraCodigo.IMG_ALT_REDUNDANTE; }
    @Override public String nome() { return "Imagem com ALT redundante"; }
    @Override public String descricao() { return "..."; }
    @Override public Severidade severidadePadrao() { return Severidade.MEDIA; }

    @Override
    public List<ProblemaAcessibilidade> avaliar(Document doc) {
        return doc.select("img[alt~=^(imagem|foto)$]").stream()
            .map(img -> ProblemaAcessibilidade.builder()
                .codigoRegra(codigo())
                .severidade(severidadePadrao())
                .descricao("ALT redundante: \"" + img.attr("alt") + "\".")
                .recomendacao("Descreva o conteúdo, não a mídia.")
                .trechoHtml(img.outerHtml())
                .build())
            .toList();
    }
}
```

Spring descobre o bean automaticamente. A regra entra no `MotorAuditoria` e no endpoint `GET /api/regras` sem nenhuma outra mudança. (Lembrar de adicionar o código ao enum `RegraCodigo` e ao constraint `CHECK` da migration Flyway.)

## Testes

```bash
./mvnw test
```

Cobertura:
- `AuditoriaIntegrationTest` — `@SpringBootTest` + MockMvc, exercita os 5 endpoints contra Postgres real
- `RegrasUnitTest` — testa cada uma das 5 regras isoladamente sem subir Spring (apenas Jsoup)

## Actuator / observabilidade

| Endpoint | Descrição |
|---|---|
| `/actuator/health` | Status geral |
| `/actuator/health/liveness` | Probe de liveness (K8s/ECS) |
| `/actuator/health/readiness` | Probe de readiness (ALB AWS) |
| `/actuator/info` | Metadados do app |

## Banco de dados

Schema gerenciado por **Flyway** — migrations em `src/main/resources/db/migration/`. Hibernate roda em modo `validate` (não cria tabelas, só verifica consistência com as entidades).

3 tabelas: `auditoria`, `relatorio`, `problema_acessibilidade`. Veja `V1__init.sql` para detalhes.

## Docker

```bash
docker build -t accessaudit-backend .
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/accessaudit \
  accessaudit-backend
```

Em produção, prefira o `docker-compose.yml` no root do repositório (orquestra Postgres + backend + frontend juntos).
