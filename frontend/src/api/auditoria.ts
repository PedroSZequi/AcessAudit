import type {
  AuditoriaDetalheResponse,
  AuditoriaRequest,
  AuditoriaResumoResponse,
  RelatorioResponse,
} from "../types/api";
import { api } from "./client";

export const auditoriaApi = {
  /** POST /api/auditorias — executa uma auditoria. */
  criar(req: AuditoriaRequest, signal?: AbortSignal) {
    return api.post<RelatorioResponse>("/api/auditorias", req, signal);
  },

  /** GET /api/auditorias — lista resumida. */
  listar(signal?: AbortSignal) {
    return api.get<AuditoriaResumoResponse[]>("/api/auditorias", signal);
  },

  /** GET /api/auditorias/{id} — detalhe da auditoria + relatório. */
  consultar(id: string, signal?: AbortSignal) {
    return api.get<AuditoriaDetalheResponse>(`/api/auditorias/${id}`, signal);
  },

  /** GET /api/auditorias/{id}/relatorio — relatório isolado. */
  consultarRelatorio(id: string, signal?: AbortSignal) {
    return api.get<RelatorioResponse>(`/api/auditorias/${id}/relatorio`, signal);
  },
};
