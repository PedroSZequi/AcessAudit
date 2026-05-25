import type { RegraResponse } from "../types/api";
import { api } from "./client";

export const regrasApi = {
  /** GET /api/regras — lista as regras de acessibilidade suportadas. */
  listar(signal?: AbortSignal) {
    return api.get<RegraResponse[]>("/api/regras", signal);
  },
};
