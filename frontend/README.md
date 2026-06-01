# AccessAudit — Frontend

Interface web que consome a API REST do backend para auditar acessibilidade WCAG 2.1 de páginas. Permite informar uma URL ou colar um HTML, exibe um relatório com filtros por severidade e um modal de detalhe para cada problema identificado.

## Stack

- **React 19** + **TypeScript**
- **Vite** (dev server e build)
- **Tailwind CSS v4** (via `@tailwindcss/vite`) — tema dark
- **React Router 7**
- Cliente HTTP nativo (`fetch`) com wrapper tipado

## Telas

| Rota | Tela | Origem |
|---|---|---|
| `/` | Home com tabs URL/HTML e botão Auditar | Tela 1 do wireframe |
| (durante submit) | Loading com 4 etapas animadas | Tela 2 do wireframe |
| `/audit/:id` | Relatório com cards de totais, filtro por chips e lista de problemas | Tela 3 do wireframe |
| (modal sobre /audit) | Detalhe do problema (código, severidade, trecho HTML, recomendação) | Tela 4 do wireframe |
| `/historico` | Lista todas as auditorias executadas no sistema | adicional |

## Estrutura

```
frontend/
├── src/
│   ├── api/           Cliente HTTP tipado + funções por recurso
│   │   ├── client.ts        wrapper fetch (BASE_URL, ApiError, envelope)
│   │   ├── auditoria.ts     POST/GET de auditorias
│   │   └── regras.ts        GET /api/regras
│   ├── components/    Componentes reutilizáveis
│   │   ├── AuditForm.tsx
│   │   ├── CardsResumo.tsx
│   │   ├── FiltroChips.tsx
│   │   ├── Header.tsx
│   │   ├── LoadingScreen.tsx
│   │   ├── ModalProblema.tsx
│   │   ├── ProblemaItem.tsx
│   │   └── SeveridadeBadge.tsx
│   ├── hooks/         Hooks customizados
│   │   └── useHistorico.ts   persistência no localStorage
│   ├── pages/         Páginas roteadas
│   │   ├── HomePage.tsx
│   │   ├── AuditPage.tsx
│   │   └── HistoricoPage.tsx
│   ├── types/         Espelho do contrato da API
│   │   └── api.ts
│   ├── App.tsx        Router + layout
│   ├── main.tsx
│   └── index.css      Tailwind + dark theme base
├── Dockerfile         Multi-stage (node → nginx)
├── nginx.conf         Reverse proxy /api/* → backend
└── package.json
```

## Como rodar localmente

### Pré-requisitos
- Node.js 20+ (24 também funciona)
- Backend rodando em `http://localhost:8080`

```bash
npm install
npm run dev
```

Aplicação disponível em `http://localhost:5173`.

### Variáveis de ambiente

Um único `.env` (opcional, com fallback no código):

```bash
VITE_API_BASE_URL=http://localhost:8080
```

- **Em desenvolvimento** (Vite dev server na 5173): o cliente HTTP chama `http://localhost:8080/api/...`. CORS está configurado no backend.
- **Em produção** (build com `VITE_API_BASE_URL=""`): as chamadas viram **same-origin** (`/api/...`), e o Nginx faz reverse proxy para o backend. Sem CORS.

## Scripts

| Comando | Faz |
|---|---|
| `npm run dev` | Inicia o Vite dev server (hot reload) |
| `npm run build` | Type-check (tsc) + build de produção em `dist/` |
| `npm run preview` | Serve o `dist/` localmente para testar o build |
| `npm run lint` | Roda o ESLint sobre todo o código |

Para checagem isolada de tipos sem build:

```bash
npx tsc --noEmit
```

## Contrato com o backend

Os tipos em [`src/types/api.ts`](src/types/api.ts) espelham os DTOs do backend Java em `backend/src/main/java/com/accessaudit/backend/dto/`. Quando o contrato mudar, atualize ambos.

Toda comunicação passa pelo wrapper em [`src/api/client.ts`](src/api/client.ts), que:
- Centraliza `VITE_API_BASE_URL`
- Trata o envelope `{ status, erro, mensagem, caminho }` retornado pelo backend
- Expõe `ApiError` com `status` e `message` para o componente reagir

Exemplo:

```tsx
try {
  const auditoria = await auditoriaApi.criar({
    tipoEntrada: "URL",
    url: "https://example.com",
  });
  navigate(`/audit/${auditoria.id}`);
} catch (e) {
  if (e instanceof ApiError) {
    setErro(e.status === 422 ? "URL inválida ou inacessível." : e.message);
  }
}
```

## Acessibilidade

A própria interface segue boas práticas (já que ela audita acessibilidade dos outros):
- Tema dark com contraste WCAG AA
- `role`, `aria-label`, `aria-pressed`, `aria-busy`, `aria-modal` nos componentes interativos
- Foco trap no modal de detalhe (Tab cicla pelos focáveis do modal)
- ESC fecha modal
- Navegação por teclado em todos os controles

## Docker

```bash
docker build -t accessaudit-frontend .
docker run --rm -p 80:80 accessaudit-frontend
```

Em produção, prefira o `docker-compose.yml` no root do repositório (sobe Postgres + backend + frontend juntos com Nginx fazendo proxy).
