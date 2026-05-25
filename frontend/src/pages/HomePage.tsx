import { AuditForm } from "../components/AuditForm";

/**
 * Tela 1 do wireframe TG2.3 — Home / Input.
 */
export function HomePage() {
  return (
    <main className="flex flex-1 items-center justify-center px-6 py-12">
      <div className="w-full max-w-3xl text-center">
        <span className="inline-flex items-center gap-2 rounded-full border border-zinc-800 bg-zinc-900/60 px-3 py-1 text-xs text-zinc-400">
          <span aria-hidden="true" className="h-1.5 w-1.5 rounded-full bg-emerald-400" />
          WCAG 2.1 · Auditoria automatizada
        </span>
        <h1 className="mt-6 text-4xl font-semibold tracking-tight sm:text-5xl">
          Audite a acessibilidade
          <br /> da sua página web
        </h1>
        <p className="mt-4 text-sm text-zinc-400">
          Informe uma URL ou cole o código HTML diretamente.
          <br />
          O AccessAudit identifica problemas e gera recomendações.
        </p>

        <AuditForm />
      </div>
    </main>
  );
}
