import type { ProblemaResponse } from "../types/api";
import { CORES_SEVERIDADE, SeveridadeBadge } from "./SeveridadeBadge";

interface Props {
  problema: ProblemaResponse;
  /** Disparado ao clicar — abre o modal de detalhe (F5). */
  onClick?: () => void;
}

/**
 * Linha de problema na lista do relatório (Tela 3 do wireframe TG2.3).
 * Clicável — abre o modal de detalhe (implementado em F5).
 */
export function ProblemaItem({ problema, onClick }: Props) {
  const cor = CORES_SEVERIDADE[problema.severidade];

  return (
    <button
      type="button"
      onClick={onClick}
      className="group flex w-full items-center justify-between gap-3 rounded-lg border border-zinc-800 bg-zinc-900/30 px-3 py-2 text-left transition hover:border-zinc-700 hover:bg-zinc-900/60 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-zinc-500"
    >
      <div className="flex min-w-0 flex-1 items-center gap-3">
        <span
          className={`shrink-0 rounded border px-2 py-0.5 font-mono text-[10px] uppercase tracking-tight ${cor.chip}`}
        >
          {problema.codigoRegra}
        </span>
        <span className="truncate text-sm text-zinc-200">{problema.descricao}</span>
      </div>
      <div className="flex shrink-0 items-center gap-2">
        <SeveridadeBadge severidade={problema.severidade} />
        <span className="text-zinc-600 transition group-hover:text-zinc-400" aria-hidden="true">
          ›
        </span>
      </div>
    </button>
  );
}
