import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { auditoriaApi } from "../api/auditoria";
import type { AuditoriaResumoResponse } from "../types/api";

/**
 * Página de histórico — lista todas as auditorias do backend.
 * Consome GET /api/auditorias.
 */
export function HistoricoPage() {
  const [auditorias, setAuditorias] = useState<AuditoriaResumoResponse[] | null>(null);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    const ctrl = new AbortController();
    auditoriaApi
      .listar(ctrl.signal)
      .then(setAuditorias)
      .catch((e: unknown) => {
        if ((e as { name?: string })?.name === "AbortError") return;
        setErro("Não foi possível carregar o histórico.");
      });
    return () => ctrl.abort();
  }, []);

  return (
    <main className="mx-auto w-full max-w-5xl flex-1 px-6 py-8">
      <nav aria-label="breadcrumb" className="text-xs text-zinc-500">
        <Link to="/" className="hover:text-zinc-300">
          Home
        </Link>
        <span className="px-2">/</span>
        <span className="text-zinc-300">Histórico</span>
      </nav>

      <h1 className="mt-2 text-2xl font-semibold tracking-tight">
        Histórico de auditorias
      </h1>
      <p className="mt-1 text-sm text-zinc-400">
        Todas as auditorias executadas pelo sistema, da mais recente para a mais antiga.
      </p>

      {erro && (
        <div
          role="alert"
          className="mt-8 rounded-lg border border-red-900/60 bg-red-950/40 px-4 py-3 text-sm text-red-300"
        >
          {erro}
        </div>
      )}

      {auditorias === null && !erro && <Esqueleto />}

      {auditorias && auditorias.length === 0 && (
        <div className="mt-8 rounded-lg border border-zinc-800 bg-zinc-900/30 px-4 py-8 text-center">
          <p className="text-sm text-zinc-400">Nenhuma auditoria executada ainda.</p>
          <Link
            to="/"
            className="mt-4 inline-block rounded-md bg-zinc-50 px-4 py-2 text-sm font-medium text-zinc-950 transition hover:bg-zinc-200"
          >
            Executar primeira auditoria
          </Link>
        </div>
      )}

      {auditorias && auditorias.length > 0 && (
        <ul className="mt-6 space-y-2">
          {auditorias.map((a) => (
            <li key={a.id}>
              <Link
                to={`/audit/${a.id}`}
                className="flex items-center justify-between gap-3 rounded-lg border border-zinc-800 bg-zinc-900/30 px-3 py-2 transition hover:border-zinc-700 hover:bg-zinc-900/60 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-zinc-500"
              >
                <div className="min-w-0 flex-1">
                  <p className="truncate font-mono text-sm text-zinc-200">
                    {a.url ?? "Auditoria por HTML colado"}
                  </p>
                  <p className="mt-0.5 text-[11px] text-zinc-500">
                    <span className="rounded border border-zinc-800 bg-zinc-900 px-1.5 py-0.5">
                      {a.tipoEntrada}
                    </span>
                    <span className="px-2 text-zinc-700">·</span>
                    <time dateTime={a.dataExecucao}>{formatarData(a.dataExecucao)}</time>
                  </p>
                </div>
                <span className="text-zinc-600 transition group-hover:text-zinc-400" aria-hidden>
                  ›
                </span>
              </Link>
            </li>
          ))}
        </ul>
      )}
    </main>
  );
}

function Esqueleto() {
  return (
    <div className="mt-6 space-y-2" aria-busy="true">
      {[0, 1, 2].map((i) => (
        <div key={i} className="h-14 animate-pulse rounded-lg bg-zinc-900/40" />
      ))}
    </div>
  );
}

function formatarData(iso: string): string {
  try {
    return new Date(iso).toLocaleString("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch {
    return iso;
  }
}
