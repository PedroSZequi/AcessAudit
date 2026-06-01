import { Link, NavLink } from "react-router-dom";

/**
 * Header padrão (todas as telas dos wireframes TG2.3 têm o mesmo).
 */
export function Header() {
  return (
    <header className="border-b border-zinc-800/80">
      <div className="mx-auto flex h-14 max-w-6xl items-center justify-between px-6">
        <div className="flex items-center gap-6">
          <Link to="/" className="flex items-center gap-2 transition hover:opacity-80">
            <span
              aria-hidden="true"
              className="grid h-6 w-6 place-items-center rounded bg-zinc-50 text-xs font-bold text-zinc-950"
            >
              A
            </span>
            <span className="text-sm font-semibold tracking-tight">AccessAudit</span>
          </Link>
          <nav aria-label="Principal" className="flex items-center gap-4 text-sm">
            <NavLink
              to="/"
              end
              className={({ isActive }) =>
                isActive ? "text-zinc-100" : "text-zinc-500 hover:text-zinc-300"
              }
            >
              Auditar
            </NavLink>
            <NavLink
              to="/historico"
              className={({ isActive }) =>
                isActive ? "text-zinc-100" : "text-zinc-500 hover:text-zinc-300"
              }
            >
              Histórico
            </NavLink>
          </nav>
        </div>
        <span className="text-xs text-zinc-500">protótipo · wireframe</span>
      </div>
    </header>
  );
}
