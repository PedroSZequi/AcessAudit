import { useEffect, useState } from "react";

/**
 * Tela 2 do wireframe TG2.3 — feedback durante a auditoria.
 * As etapas avançam em intervalos fixos enquanto o POST está em voo.
 * Não há streaming real — é animação visual.
 */

interface Props {
  /** Identificador visível: URL informada, ou "HTML colado". */
  origem: string;
}

const ETAPAS = [
  "Validando URL",
  "Obtendo conteúdo HTML",
  "Executando regras WCAG",
  "Gerando relatório",
] as const;

export function LoadingScreen({ origem }: Props) {
  const [etapaAtiva, setEtapaAtiva] = useState(0);

  useEffect(() => {
    // Avança a etapa ativa a cada 700ms; a última fica em loop até a navegação.
    const intervalos = [400, 900, 1700, 2500];
    const timers = intervalos.map((ms, i) =>
      window.setTimeout(() => setEtapaAtiva(i + 1), ms)
    );
    return () => timers.forEach(clearTimeout);
  }, []);

  return (
    <main
      className="flex flex-1 items-center justify-center px-6 py-12"
      role="status"
      aria-live="polite"
      aria-busy="true"
    >
      <div className="w-full max-w-md text-center">
        {/* Ícone de "ondas" pulsando */}
        <div className="mx-auto flex h-14 w-14 items-center justify-center">
          <span className="relative flex h-3 w-3">
            <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-zinc-50 opacity-30" />
            <span className="relative inline-flex h-3 w-3 rounded-full bg-zinc-50" />
          </span>
        </div>

        <h1 className="mt-4 text-lg font-medium">
          Analisando <span className="text-zinc-400">acessibilidade...</span>
        </h1>

        <div className="mt-3 inline-block rounded-md border border-zinc-800 bg-zinc-900/60 px-3 py-1 font-mono text-xs text-zinc-400">
          {origem}
        </div>

        <ul className="mx-auto mt-8 max-w-xs space-y-2 text-left text-sm">
          {ETAPAS.map((etapa, i) => {
            const concluida = i < etapaAtiva;
            const ativa = i === etapaAtiva;
            return (
              <li
                key={etapa}
                className={[
                  "flex items-center gap-2 transition",
                  concluida
                    ? "text-zinc-200"
                    : ativa
                    ? "text-zinc-100"
                    : "text-zinc-600",
                ].join(" ")}
              >
                <span
                  aria-hidden="true"
                  className="inline-block w-4 text-center"
                >
                  {concluida ? "✓" : ativa ? "▢" : "·"}
                </span>
                <span className={ativa ? "font-medium" : ""}>{etapa}</span>
                {ativa && (
                  <span className="ml-1 inline-flex gap-0.5" aria-hidden="true">
                    <span className="h-1 w-1 animate-pulse rounded-full bg-zinc-400 [animation-delay:0ms]" />
                    <span className="h-1 w-1 animate-pulse rounded-full bg-zinc-400 [animation-delay:150ms]" />
                    <span className="h-1 w-1 animate-pulse rounded-full bg-zinc-400 [animation-delay:300ms]" />
                  </span>
                )}
              </li>
            );
          })}
        </ul>
      </div>
    </main>
  );
}
