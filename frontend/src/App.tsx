import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Header } from "./components/Header";
import { AuditPage } from "./pages/AuditPage";
import { HomePage } from "./pages/HomePage";

function App() {
  return (
    <BrowserRouter>
      <div className="flex min-h-screen flex-col bg-zinc-950 text-zinc-50">
        <Header />
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/audit/:id" element={<AuditPage />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
