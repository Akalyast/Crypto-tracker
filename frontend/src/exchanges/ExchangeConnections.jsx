import { useEffect, useState } from "react";
import DashboardLayout from "../layout/DashboardLayout";
import api from "../api/axios";

export default function ExchangeConnections() {
  const [exchanges, setExchanges] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [form, setForm] = useState({
    exchange: "BINANCE",
    label: "",
    apiKey: "",
    apiSecret: "",
  });

  /* ================= FETCH CONNECTED EXCHANGES ================= */
  const fetchExchanges = async () => {
    try {
      const res = await api.get("/api/exchanges");
      setExchanges(res.data || []);
    } catch (err) {
      console.error("Failed to load exchanges", err);
      setExchanges([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchExchanges();
  }, []);

  /* ================= CONNECT EXCHANGE ================= */
  const handleConnect = async () => {
    try {
      await api.post("/api/exchanges", form);
      setIsModalOpen(false);
      setForm({
        exchange: "BINANCE",
        label: "",
        apiKey: "",
        apiSecret: "",
      });
      fetchExchanges();
    } catch (err) {
      console.error("Exchange connection failed", err);
      alert("Failed to connect exchange");
    }
  };

  /* ================= SYNC ================= */
  const handleSync = async (id) => {
    try {
      await api.post(`/api/exchanges/${id}/sync`);
      fetchExchanges();
    } catch (err) {
      console.error("Sync failed", err);
      alert("Sync failed");
    }
  };

  /* ================= DISCONNECT ================= */
  const handleDisconnect = async (id) => {
    if (!window.confirm("Disconnect this exchange?")) return;

    try {
      await api.delete(`/api/exchanges/${id}`);
      fetchExchanges();
    } catch (err) {
      console.error("Disconnect failed", err);
      alert("Failed to disconnect exchange");
    }
  };

  return (
    <DashboardLayout>
      <div className="p-10 text-white min-h-screen cyberpunk-bg">
        {/* HEADER */}
        <div className="mb-10">
          <h1 className="text-4xl font-bold tracking-wide">
            Exchange Connections
          </h1>
          <p className="text-gray-400 mt-2 max-w-2xl">
            Securely connect your exchanges to auto-sync balances and trades.
            API keys are encrypted at rest.
          </p>

          <span className="inline-block mt-4 px-4 py-1 rounded-full bg-green-500/10 text-green-400 text-sm border border-green-500/20">
            🔒 AES-256 Encrypted • Read-Only Access
          </span>
        </div>

        {/* CONTENT */}
        {loading ? (
          <p className="text-gray-400">Loading exchanges...</p>
        ) : exchanges.length === 0 ? (
          <p className="text-gray-400 mb-10">No exchanges connected yet.</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-10">
            {exchanges.map((ex) => (
              <div
                key={ex.id}
                className="glass-card p-6 rounded-2xl border border-white/10 shadow-xl"
              >
                <div className="flex justify-between items-center mb-4">
                  <div className="flex items-center gap-3">
                    <img
                      src="https://cryptologos.cc/logos/binance-coin-bnb-logo.png"
                      alt="Binance"
                      className="w-10 h-10"
                    />
                    <div>
                      <h2 className="text-xl font-semibold">{ex.exchange}</h2>
                      <p className="text-sm text-gray-400">{ex.label}</p>
                    </div>
                  </div>

                  <span className="px-3 py-1 rounded-full text-xs font-semibold bg-green-500/10 text-green-400">
                    CONNECTED
                  </span>
                </div>

                <div className="text-sm text-gray-400 mb-4">
                  Last Sync:{" "}
                  <span className="text-white">
                    {ex.lastSyncedAt
                      ? new Date(ex.lastSyncedAt).toLocaleString()
                      : "Never"}
                  </span>
                </div>

                <div className="flex gap-4">
                  <button
                    onClick={() => handleSync(ex.id)}
                    className="px-4 py-2 rounded-lg bg-blue-600 hover:bg-blue-700 transition"
                  >
                    Sync Now
                  </button>

                  <button
                    onClick={() => handleDisconnect(ex.id)}
                    className="px-4 py-2 rounded-lg bg-red-500/10 text-red-400 hover:bg-red-500/20 transition"
                  >
                    Disconnect
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* ADD EXCHANGE */}
        <button
          onClick={() => setIsModalOpen(true)}
          className="px-8 py-4 rounded-2xl neon-button text-lg font-semibold"
        >
          + Connect New Exchange
        </button>

        {/* MODAL */}
        {isModalOpen && (
          <div className="modal-overlay">
            <div className="modal-box max-w-lg">
              <h2 className="text-2xl font-bold mb-2 neon-text">
                Connect Exchange
              </h2>
              <p className="text-gray-400 mb-6">
                Add exchange API keys (read-only recommended)
              </p>

              <div className="flex flex-col gap-4">
                <select
                  className="cyber-input"
                  value={form.exchange}
                  onChange={(e) =>
                    setForm({ ...form, exchange: e.target.value })
                  }
                >
                  <option value="BINANCE">Binance</option>
                </select>

                <input
                  className="cyber-input"
                  placeholder="Label (My Binance Main)"
                  value={form.label}
                  onChange={(e) => setForm({ ...form, label: e.target.value })}
                />

                <input
                  className="cyber-input"
                  placeholder="API Key"
                  type="password"
                  value={form.apiKey}
                  onChange={(e) => setForm({ ...form, apiKey: e.target.value })}
                />

                <input
                  className="cyber-input"
                  placeholder="API Secret"
                  type="password"
                  value={form.apiSecret}
                  onChange={(e) =>
                    setForm({ ...form, apiSecret: e.target.value })
                  }
                />
              </div>

              <div className="text-xs text-gray-400 mt-4">
                🔐 Keys are encrypted using AES and never exposed. Enable{" "}
                <b>READ-ONLY</b> permissions only.
              </div>

              <div className="flex justify-end gap-4 mt-8">
                <button
                  onClick={() => setIsModalOpen(false)}
                  className="cancel-btn"
                >
                  Cancel
                </button>

                <button onClick={handleConnect} className="save-btn">
                  Test & Connect
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </DashboardLayout>
  );
}
