import { BrowserRouter, Routes, Route } from "react-router-dom";
import { Toaster } from "react-hot-toast";

import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import Holdings from "./pages/Holdings";
import Trades from "./pages/Trades";
import Pricing from "./pages/Pricing";
import RiskAlerts from "./pages/RiskAlerts";
import PnLReports from "./pages/PnLReports";
import ForgotPassword from "./pages/ForgotPassword";
import ResetPassword from "./pages/ResetPassword"; // ✅ ADD THIS
import ExchangeConnections from "./exchanges/ExchangeConnections"; // ✅ ADD THIS

import { AuthProvider } from "./contexts/AuthContext";

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Toaster position="top-right" />
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/reset-password" element={<ResetPassword />} />{" "}
          {/* ✅ */}
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/exchanges" element={<ExchangeConnections />} />
          <Route path="/holdings" element={<Holdings />} />
          <Route path="/trades" element={<Trades />} />
          <Route path="/pricing" element={<Pricing />} />
          <Route path="/risk-alerts" element={<RiskAlerts />} />
          <Route path="/pnl-reports" element={<PnLReports />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
