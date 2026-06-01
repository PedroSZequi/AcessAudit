import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { auditoriaApi } from "../api/auditoria";
import { ApiError } from "../api/client";
import type { TipoEntrada } from "../types/api";
import { LoadingScreen } from "./LoadingScreen";

/**
 * Form de auditoria (Tela 1 do wireframe TG2.3).
 *  - Tabs URL / HTML
 *  - Validação client-side
 *  - Durante o request, renderiza {@link LoadingScreen} (Tela 2)
 *  - On success: navega para /audit/:id (Tela 3 — F4)
 */
export function AuditForm() {
  const [aba, setAba] = useState<TipoEntrada>("URL");
  const [url, setUrl] = useState("");
  const [html, setHtml] = useState("");
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState<string | null>(null);
  const navigate = useNavigate();

  const valorPreenchido = aba === "URL" ? url.trim().length > 0 : html.trim().length > 0;
  const podeSubmeter = valorPreenchido && !loading;

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!podeSubmeter) return;

    setLoading(true);
    setErro(null);

    try {
      const auditoria = await auditoriaApi.criar({
        tipoEntrada: aba,
        url: aba === "URL" ? url.trim() : undefined,
        html: aba === "HTML" ? html : undefined,
      });
      navigate(`/audit/${auditoria.id}`);
    } catch (e) {
      if (e instanceof ApiError) {
        setErro(
          e.status === 501
            ? "Auditoria ainda não está disponível (backend em construção)."
            : e.message
        );
      } else {
        setErro("Não foi possível executar a auditoria. Tente novamente.");
      }
      setLoading(false);
    }
  }

  if (loading) {
    return <LoadingScreen origem={aba === "URL" ? url.trim() : "HTML colado"} />;
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="mx-auto mt-10 w-full max-w-2xl rounded-xl border border-zinc-800 bg-zinc-900/40 p-2 shadow-2xl"
    >
      {/* Tabs URL / HTML */}
      <div
        role="tablist"
        aria-label="Tipo de entrada"
        className="grid grid-cols-2 gap-1 rounded-lg bg-zinc-900/60 p-1"
      >
        {(["URL", "HTML"] as const).map((opt) => (
          <button
            key={opt}
            type="button"
            role="tab"
            aria-selected={aba === opt}
            onClick={() => {
              setAba(opt);
              setErro(null);
            }}
            className={[
              "rounded-md px-3 py-2 text-sm font-medium transition",
              "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-zinc-500",
              aba === opt
                ? "bg-zinc-50 text-zinc-950"
                : "text-zinc-400 hover:text-zinc-200",
            ].join(" ")}
          >
            {opt}
          </button>
        ))}
      </div>

      {/* Input por aba */}
      <div className="mt-2 flex items-stretch gap-2 p-2">
        {aba === "URL" ? (
          <input
            type="url"
            inputMode="url"
            required
            placeholder="https://exemplo.com.br"
            value={url}
            onChange={(e) => setUrl(e.target.value)}
            aria-label="URL da página a auditar"
            className="flex-1 rounded-md border border-zinc-800 bg-zinc-950 px-3 py-2 text-sm text-zinc-100 placeholder:text-zinc-600 focus:border-zinc-500 focus:outline-none"
          />
        ) : (
          <textarea
            required
            placeholder="<html>...</html>"
            value={html}
            onChange={(e) => setHtml(e.target.value)}
            aria-label="Código HTML a auditar"
            rows={6}
            className="flex-1 resize-y rounded-md border border-zinc-800 bg-zinc-950 px-3 py-2 font-mono text-xs text-zinc-100 placeholder:text-zinc-600 focus:border-zinc-500 focus:outline-none"
          />
        )}

        {aba === "URL" && (
          <button
            type="submit"
            disabled={!podeSubmeter}
            className="rounded-md bg-zinc-50 px-4 py-2 text-sm font-semibold text-zinc-950 transition hover:bg-zinc-200 disabled:cursor-not-allowed disabled:bg-zinc-700 disabled:text-zinc-400"
          >
            Auditar
          </button>
        )}
      </div>

      {aba === "HTML" && (
        <div className="px-2 pb-2">
          <button
            type="submit"
            disabled={!podeSubmeter}
            className="w-full rounded-md bg-zinc-50 px-4 py-2 text-sm font-semibold text-zinc-950 transition hover:bg-zinc-200 disabled:cursor-not-allowed disabled:bg-zinc-700 disabled:text-zinc-400"
          >
            Auditar
          </button>
        </div>
      )}

      <p className="px-2 pb-2 text-[11px] text-zinc-500">
        RF02 / RF03 — Fornecer URL ou HTML direto para auditoria
      </p>

      {erro && (
        <div
          role="alert"
          className="mx-2 mb-2 rounded-md border border-red-900/60 bg-red-950/40 px-3 py-2 text-xs text-red-300"
        >
          {erro}
        </div>
      )}
    </form>
  );
}
