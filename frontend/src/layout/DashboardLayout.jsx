import Sidebar from "../components/Sidebar";
import NotificationBell from "../components/NotificationBell";
import UserProfile from "../components/UserProfile";

export default function DashboardLayout({ children }) {
  return (
    <div className="flex h-screen overflow-hidden bg-gradient-to-br from-black via-[#0a0a2e] to-black text-white">
      {/* ================= SIDEBAR (STATIC) ================= */}
      <div className="flex-shrink-0 h-screen">
        <Sidebar />
      </div>

      {/* ================= RIGHT SECTION ================= */}
      <div className="flex flex-col flex-1 overflow-hidden">
        {/* ---------- FIXED HEADER ---------- */}
        <header className="h-16 flex items-center justify-between px-8 border-b border-white/10 bg-black/40 backdrop-blur-xl flex-shrink-0">
          <h1 className="text-lg font-semibold tracking-wide">
            Crypto Portfolio Tracker
          </h1>

          <div className="flex items-center gap-6">
            <NotificationBell />
            <UserProfile />
          </div>
        </header>

        {/* ---------- SCROLLABLE CONTENT ---------- */}
        <main className="flex-1 overflow-y-auto p-10">{children}</main>
      </div>
    </div>
  );
}
