import { useCallback, useEffect, useState } from "react";
import type { AuditoriaDetalheResponse, TipoEntrada } from "../types/api";

/**
 * Resumo persistido no localStorage para acesso rápido às últimas
 * auditorias visualizadas pelo próprio navegador. Independente do
 * histórico vindo do backend (que mostra todas as auditorias do sistema).
 */
export interface HistoricoItem {
  id: string;
  tipoEntrada: TipoEntrada;
  url: string | null;
  dataExecucao: string;
  totalProblemas: number;
}

const KEY = "accessaudit:historico";
const MAX_ITEMS = 10;

function carregar(): HistoricoItem[] {
  try {
    const raw = localStorage.getItem(KEY);
    if (!raw) return [];
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

function persistir(items: HistoricoItem[]) {
  try {
    localStorage.setItem(KEY, JSON.stringify(items));
  } catch {
    // quota / modo privado — silenciosamente ignora
  }
}

export function useHistorico() {
  const [itens, setItens] = useState<HistoricoItem[]>(carregar);

  // Mantém sincronizado se outra aba alterar o storage
  useEffect(() => {
    function onStorage(e: StorageEvent) {
      if (e.key === KEY) setItens(carregar());
    }
    window.addEventListener("storage", onStorage);
    return () => window.removeEventListener("storage", onStorage);
  }, []);

  const adicionar = useCallback((auditoria: AuditoriaDetalheResponse) => {
    const item: HistoricoItem = {
      id: auditoria.id,
      tipoEntrada: auditoria.tipoEntrada,
      url: auditoria.url,
      dataExecucao: auditoria.dataExecucao,
      totalProblemas: auditoria.relatorio.totalProblemas,
    };
    setItens((curr) => {
      const semDuplicado = curr.filter((x) => x.id !== item.id);
      const proximo = [item, ...semDuplicado].slice(0, MAX_ITEMS);
      persistir(proximo);
      return proximo;
    });
  }, []);

  const limpar = useCallback(() => {
    persistir([]);
    setItens([]);
  }, []);

  return { itens, adicionar, limpar };
}
