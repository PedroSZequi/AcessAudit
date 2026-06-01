import type { FiltroSeveridade } from "./CardsResumo";

interface Props {
  filtro: FiltroSeveridade;
  onChange: (filtro: FiltroSeveridade) => void;
}

const OPCOES: { value: FiltroSeveridade; label: string }[] = [
  { value: "TODOS", label: "Todos" },
  { value: "ALTA", label: "Alta" },
  { value: "MEDIA", label: "Média" },
  { value: "BAIXA", label: "Baixa" },
];

/**
 * Chips de filtro por severidade (Tela 3 do wireframe TG2.3).
 */
export function FiltroChips({ filtro, onChange }: Props) {
  return (
    <div role="group" aria-label="Filtrar por severidade" className="flex flex-wrap gap-2">
      {OPCOES.map((opt) => {
        const ativo = filtro === opt.value;
        return (
          <button
            key={opt.value}
            type="button"
            aria-pressed={ativo}
            onClick={() => onChange(opt.value)}
            className={[
              "rounded-full border px-3 py-1 text-xs font-medium transition",
              "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-zinc-500",
              ativo
                ? "border-zinc-200 bg-zinc-100 text-zinc-950"
                : "border-zinc-800 bg-zinc-900/40 text-zinc-400 hover:text-zinc-200",
            ].join(" ")}
          >
            {opt.label}
          </button>
        );
      })}
    </div>
  );
}
