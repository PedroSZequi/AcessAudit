import type { Severidade } from "../types/api";

/** Classes Tailwind por severidade — usadas em badges, cards e chips de regra. */
export const CORES_SEVERIDADE: Record<
  Severidade,
  { texto: string; bg: string; border: string; chip: string }
> = {
  ALTA: {
    texto: "text-red-400",
    bg: "bg-red-950/40",
    border: "border-red-900/60",
    chip: "bg-red-950/40 text-red-300 border-red-900/60",
  },
  MEDIA: {
    texto: "text-amber-400",
    bg: "bg-amber-950/40",
    border: "border-amber-900/60",
    chip: "bg-amber-950/40 text-amber-300 border-amber-900/60",
  },
  BAIXA: {
    texto: "text-emerald-400",
    bg: "bg-emerald-950/40",
    border: "border-emerald-900/60",
    chip: "bg-emerald-950/40 text-emerald-300 border-emerald-900/60",
  },
};

export function SeveridadeBadge({ severidade }: { severidade: Severidade }) {
  const c = CORES_SEVERIDADE[severidade];
  return (
    <span
      className={`inline-flex items-center rounded border px-2 py-0.5 text-[10px] font-medium uppercase tracking-wide ${c.chip}`}
    >
      {severidade}
    </span>
  );
}
