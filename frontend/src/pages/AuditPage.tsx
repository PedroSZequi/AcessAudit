import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { auditoriaApi } from "../api/auditoria";
import { ApiError } from "../api/client";
import { CardsResumo, type FiltroSeveridade } from "../components/CardsResumo";
import { FiltroChips } from "../components/FiltroChips";
import { ModalProblema } from "../components/ModalProblema";
import { ProblemaItem } from "../components/ProblemaItem";
import { useHistorico } from "../hooks/useHistorico";
import type { AuditoriaDetalheResponse, ProblemaResponse } from "../types/api";

/**
 * Tela 3 do wireframe TG2.3 — Relatório de Auditoria.
 * + Modal de detalhe (Tela 4)
 * + Persiste auditoria visualizada no histórico local
 */
export function AuditPage() {
  const { id } = useParams();
  const [auditoria, setAuditoria] = useState<AuditoriaDetalheResponse | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<{ status: number; mensagem: string } | null>(null);
  const [filtro, setFiltro] = useState<FiltroSeveridade>("TODOS");
  const [problemaSelecionado, setProblemaSelecionado] = useState<ProblemaResponse | null>(null);
  const { adicionar } = useHistorico();

  useEffect(() => {
    if (!id) return;
    const ctrl = new AbortController();
    setCarregando(true);
    setErro(null);

    auditoriaApi
      .consultar(id, ctrl.signal)
      .then((dados) => {
        setAuditoria(dados);
        adicionar(dados); // registra no histórico local
      })
      .catch((e: unknown) => {
        if ((e as { name?: string })?.name === "AbortError") return;
        if (e instanceof ApiError) {
          setErro({ status: e.status, mensagem: e.message });
        } else {
          setErro({ status: 0, mensagem: "Erro ao carregar a auditoria." });
        }
      })
      .finally(() => setCarregando(false));

    return () => ctrl.abort();
  }, [id, adicionar]);

  const problemasFiltrados = useMemo<ProblemaResponse[]>(() => {
    if (!auditoria) return [];
    if (filtro === "TODOS") return auditoria.relatorio.problemas;
    return auditoria.relatorio.problemas.filter((p) => p.severidade === filtro);
  }, [auditoria, filtro]);

  return (
    <main className="mx-auto w-full max-w-5xl flex-1 px-6 py-8">
      <nav aria-label="breadcrumb" className="text-xs text-zinc-500">
        <Link to="/" className="hover:text-zinc-300">
          Home
        </Link>
        <span className="px-2">/</span>
        <span className="text-zinc-300">Relatório</span>
      </nav>

      <h1 className="mt-2 text-2xl font-semibold tracking-tight">
        Relatório de Auditoria
      </h1>

      {carregando && <EstadoCarregando />}
      {erro && <EstadoErro status={erro.status} mensagem={erro.mensagem} />}

      {auditoria && (
        <>
          <p className="mt-1 text-xs text-zinc-400">
            <span className="font-mono">
              {auditoria.tipoEntrada === "URL"
                ? auditoria.url
                : "Auditoria por HTML colado"}
            </span>
            <span className="px-2 text-zinc-700">·</span>
            <time dateTime={auditoria.dataExecucao}>
              {formatarData(auditoria.dataExecucao)}
            </time>
          </p>

          <div className="mt-6">
            <CardsResumo problemas={auditoria.relatorio.problemas} />
          </div>

          <div className="mt-6">
            <FiltroChips filtro={filtro} onChange={setFiltro} />
          </div>

          <ul className="mt-4 space-y-2">
            {problemasFiltrados.length === 0 ? (
              <li className="rounded-lg border border-zinc-800 bg-zinc-900/30 px-3 py-6 text-center text-sm text-zinc-500">
                {auditoria.relatorio.totalProblemas === 0
                  ? "Nenhum problema encontrado — parabéns 🎉"
                  : "Nenhum problema na severidade selecionada."}
              </li>
            ) : (
              problemasFiltrados.map((problema, i) => (
                <li key={i}>
                  <ProblemaItem
                    problema={problema}
                    onClick={() => setProblemaSelecionado(problema)}
                  />
                </li>
              ))
            )}
          </ul>
        </>
      )}

      {problemaSelecionado && (
        <ModalProblema
          problema={problemaSelecionado}
          onClose={() => setProblemaSelecionado(null)}
        />
      )}
    </main>
  );
}

function EstadoCarregando() {
  return (
    <div className="mt-8 space-y-3" aria-busy="true">
      <div className="h-20 animate-pulse rounded-lg bg-zinc-900/50" />
      <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
        {[0, 1, 2, 3].map((i) => (
          <div key={i} className="h-20 animate-pulse rounded-lg bg-zinc-900/50" />
        ))}
      </div>
      <div className="h-10 animate-pulse rounded-lg bg-zinc-900/50" />
      <div className="h-12 animate-pulse rounded-lg bg-zinc-900/50" />
      <div className="h-12 animate-pulse rounded-lg bg-zinc-900/50" />
    </div>
  );
}

function EstadoErro({ status, mensagem }: { status: number; mensagem: string }) {
  return (
    <div
      role="alert"
      className="mt-8 rounded-lg border border-red-900/60 bg-red-950/40 px-4 py-6 text-sm text-red-300"
    >
      <p className="font-medium">
        {status === 404 ? "Auditoria não encontrada" : "Erro ao carregar"}
      </p>
      <p className="mt-1 text-red-400/80">{mensagem}</p>
      <Link
        to="/"
        className="mt-4 inline-block rounded-md border border-red-900/60 px-3 py-1 text-xs hover:bg-red-950/60"
      >
        ← Voltar
      </Link>
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
