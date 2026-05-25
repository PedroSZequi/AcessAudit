/**
 * Tipos espelhando o contrato da API (TG3.6 + adendo trechoHtml).
 * Mantenha em sincronia com os DTOs em backend/src/main/java/com/accessaudit/backend/dto/.
 */

export type TipoEntrada = "URL" | "HTML";

export type Severidade = "BAIXA" | "MEDIA" | "ALTA";

export type RegraCodigo =
  | "IMG_SEM_ALT"
  | "INPUT_SEM_LABEL"
  | "LINK_TEXTO_GENERICO"
  | "LANG_AUSENTE"
  | "TITLE_AUSENTE";

export interface AuditoriaRequest {
  tipoEntrada: TipoEntrada;
  url?: string;
  html?: string;
}

export interface ProblemaResponse {
  codigoRegra: RegraCodigo;
  severidade: Severidade;
  descricao: string;
  recomendacao: string;
  trechoHtml: string | null;
}

export interface RelatorioResponse {
  id: string;
  geradoEm: string; // ISO-8601
  totalProblemas: number;
  problemas: ProblemaResponse[];
}

export interface AuditoriaResumoResponse {
  id: string;
  tipoEntrada: TipoEntrada;
  url: string | null;
  dataExecucao: string;
}

export interface AuditoriaDetalheResponse {
  id: string;
  tipoEntrada: TipoEntrada;
  url: string | null;
  html: string | null;
  dataExecucao: string;
  relatorio: RelatorioResponse;
}

export interface RegraResponse {
  codigo: RegraCodigo;
  nome: string;
  descricao: string;
  severidadePadrao: Severidade;
}

/** Envelope padrão de erro (TG3.6). */
export interface ErroResponse {
  status: number;
  erro: string;
  mensagem: string;
  caminho: string;
}
