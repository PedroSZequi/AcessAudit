/**
 * Wrapper tipado sobre fetch. Centraliza:
 *  - prefixo VITE_API_BASE_URL
 *  - serialização JSON
 *  - tratamento de erro com o envelope {@link ErroResponse}
 */
import type { ErroResponse } from "../types/api";

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export class ApiError extends Error {
  status: number;
  envelope?: ErroResponse;

  constructor(status: number, message: string, envelope?: ErroResponse) {
    super(message);
    this.status = status;
    this.envelope = envelope;
  }
}

interface RequestOptions {
  method?: "GET" | "POST" | "PUT" | "DELETE" | "PATCH";
  body?: unknown;
  signal?: AbortSignal;
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = "GET", body, signal } = options;

  const response = await fetch(`${BASE_URL}${path}`, {
    method,
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    body: body !== undefined ? JSON.stringify(body) : undefined,
    signal,
  });

  // 204 No Content
  if (response.status === 204) {
    return undefined as T;
  }

  // 501 (stubs em desenvolvimento) — propaga sem body
  if (response.status === 501) {
    throw new ApiError(501, "Endpoint ainda não implementado no backend.");
  }

  if (!response.ok) {
    let envelope: ErroResponse | undefined;
    try {
      envelope = await response.json();
    } catch {
      // resposta não-JSON
    }
    throw new ApiError(
      response.status,
      envelope?.mensagem ?? `Erro HTTP ${response.status}`,
      envelope
    );
  }

  return (await response.json()) as T;
}

export const api = {
  get: <T>(path: string, signal?: AbortSignal) => request<T>(path, { method: "GET", signal }),
  post: <T>(path: string, body: unknown, signal?: AbortSignal) =>
    request<T>(path, { method: "POST", body, signal }),
};
