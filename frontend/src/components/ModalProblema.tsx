import { useEffect, useRef } from "react";
import type { ProblemaResponse } from "../types/api";
import { CORES_SEVERIDADE, SeveridadeBadge } from "./SeveridadeBadge";

/**
 * Tela 4 do wireframe TG2.3 — modal de detalhe do problema.
 *
 * Acessibilidade:
 *  - role="dialog" + aria-modal="true"
 *  - aria-labelledby / aria-describedby
 *  - foco move para o modal ao abrir e volta ao botão de origem ao fechar
 *  - ESC fecha
 *  - foco trap (Tab cicla apenas pelos elementos do modal)
 *  - clique no backdrop fecha (clique dentro do card, não)
 */

interface Props {
  problema: ProblemaResponse;
  onClose: () => void;
}

export function ModalProblema({ problema, onClose }: Props) {
  const dialogRef = useRef<HTMLDivElement | null>(null);
  const closeButtonRef = useRef<HTMLButtonElement | null>(null);

  useEffect(() => {
    const elementoOriginal = document.activeElement as HTMLElement | null;
    // Foco inicial no botão de fechar para uso por teclado
    closeButtonRef.current?.focus();

    // Bloqueia scroll do body enquanto o modal está aberto
    const overflowAnterior = document.body.style.overflow;
    document.body.style.overflow = "hidden";

    return () => {
      document.body.style.overflow = overflowAnterior;
      elementoOriginal?.focus?.();
    };
  }, []);

  function onKeyDown(e: React.KeyboardEvent<HTMLDivElement>) {
    if (e.key === "Escape") {
      e.preventDefault();
      onClose();
      return;
    }
    if (e.key !== "Tab") return;

    const root = dialogRef.current;
    if (!root) return;

    const focusables = root.querySelectorAll<HTMLElement>(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    );
    if (focusables.length === 0) return;

    const first = focusables[0];
    const last = focusables[focusables.length - 1];
    const ativo = document.activeElement as HTMLElement | null;

    if (e.shiftKey && ativo === first) {
      e.preventDefault();
      last.focus();
    } else if (!e.shiftKey && ativo === last) {
      e.preventDefault();
      first.focus();
    }
  }

  const cor = CORES_SEVERIDADE[problema.severidade];

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm"
      onClick={onClose}
      onKeyDown={onKeyDown}
    >
      <div
        ref={dialogRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-titulo"
        aria-describedby="modal-descricao"
        onClick={(e) => e.stopPropagation()}
        className="w-full max-w-lg rounded-xl border border-zinc-800 bg-zinc-950 shadow-2xl"
      >
        <header className="flex items-start justify-between gap-3 border-b border-zinc-800 px-5 py-4">
          <h2 id="modal-titulo" className="text-base font-semibold leading-tight">
            {problema.descricao}
          </h2>
          <button
            ref={closeButtonRef}
            type="button"
            aria-label="Fechar"
            onClick={onClose}
            className="rounded-md p-1 text-zinc-500 transition hover:bg-zinc-900 hover:text-zinc-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-zinc-500"
          >
            <span aria-hidden="true" className="text-lg leading-none">
              ×
            </span>
          </button>
        </header>

        <div className="space-y-4 px-5 py-4">
          <section>
            <Label>Regra / Código</Label>
            <p className={`mt-1 font-mono text-sm ${cor.texto}`}>
              {problema.codigoRegra}
            </p>
          </section>

          <section>
            <Label>Severidade</Label>
            <div className="mt-1">
              <SeveridadeBadge severidade={problema.severidade} />
            </div>
          </section>

          <section id="modal-descricao">
            <Label>Descrição</Label>
            <p className="mt-1 text-sm text-zinc-300">{problema.descricao}</p>
          </section>

          {problema.trechoHtml && (
            <section>
              <Label>Trecho identificado</Label>
              <pre className="mt-1 overflow-x-auto rounded-md border border-zinc-800 bg-zinc-900/50 p-3 font-mono text-xs text-zinc-200">
                <code>{problema.trechoHtml}</code>
              </pre>
            </section>
          )}

          <section>
            <Label>Recomendação de correção</Label>
            <p className="mt-1 text-sm text-zinc-300">{problema.recomendacao}</p>
          </section>
        </div>

        <footer className="flex justify-end gap-2 border-t border-zinc-800 px-5 py-3">
          <button
            type="button"
            onClick={onClose}
            className="rounded-md border border-zinc-700 px-4 py-1.5 text-sm transition hover:bg-zinc-900 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-zinc-500"
          >
            Fechar
          </button>
        </footer>
      </div>
    </div>
  );
}

function Label({ children }: { children: React.ReactNode }) {
  return (
    <p className="text-[10px] font-medium uppercase tracking-wide text-zinc-500">
      {children}
    </p>
  );
}
