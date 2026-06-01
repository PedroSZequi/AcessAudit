import type { ProblemaResponse, Severidade } from "../types/api";
import { CORES_SEVERIDADE } from "./SeveridadeBadge";

/**
 * 4 cards de totais (Total + por severidade) — Tela 3 do wireframe TG2.3.
 */
export function CardsResumo({ problemas }: { problemas: ProblemaResponse[] }) {
  const alta = problemas.filter((p) => p.severidade === "ALTA").length;
  const media = problemas.filter((p) => p.severidade === "MEDIA").length;
  const baixa = problemas.filter((p) => p.severidade === "BAIXA").length;

  return (
    <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
      <Card label="TOTAL" valor={problemas.length} cor="text-zinc-50" />
      <Card label="ALTA" valor={alta} cor={CORES_SEVERIDADE.ALTA.texto} />
      <Card label="MÉDIA" valor={media} cor={CORES_SEVERIDADE.MEDIA.texto} />
      <Card label="BAIXA" valor={baixa} cor={CORES_SEVERIDADE.BAIXA.texto} />
    </div>
  );
}

function Card({ label, valor, cor }: { label: string; valor: number; cor: string }) {
  return (
    <div className="rounded-lg border border-zinc-800 bg-zinc-900/40 px-4 py-3">
      <div className="text-[10px] font-medium uppercase tracking-wide text-zinc-500">
        {label}
      </div>
      <div className={`mt-1 text-3xl font-semibold ${cor}`}>{valor}</div>
    </div>
  );
}

/** Tipo auxiliar exportado caso outras telas queiram filtrar pelos mesmos valores. */
export type FiltroSeveridade = Severidade | "TODOS";
