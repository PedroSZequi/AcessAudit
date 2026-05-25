import { Link, useParams } from "react-router-dom";

/**
 * Placeholder das telas 2-4 do wireframe (Loading, Relatório, Modal).
 * Implementação real entra nas fases F3-F5.
 */
export function AuditPage() {
  const { id } = useParams();

  return (
    <main className="flex flex-1 items-center justify-center px-6 py-12">
      <div className="max-w-xl text-center">
        <nav aria-label="breadcrumb" className="mb-8 text-xs text-zinc-500">
          <Link to="/" className="hover:text-zinc-300">
            Home
          </Link>
          <span className="px-2">/</span>
          <span className="text-zinc-300">Relatório</span>
        </nav>
        <h1 className="text-2xl font-semibold">Relatório em construção</h1>
        <p className="mt-3 text-sm text-zinc-400">
          ID da auditoria: <code className="rounded bg-zinc-900 px-1.5 py-0.5 text-xs">{id}</code>
        </p>
        <p className="mt-2 text-xs text-zinc-600">
          Esta tela é finalizada nas fases F3 (loading) e F4 (relatório).
        </p>
        <Link
          to="/"
          className="mt-8 inline-block rounded-md border border-zinc-700 px-4 py-2 text-sm transition hover:bg-zinc-900"
        >
          ← Voltar
        </Link>
      </div>
    </main>
  );
}
