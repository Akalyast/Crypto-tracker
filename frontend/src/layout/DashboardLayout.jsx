import Sidebar from "../components/Sidebar";
import NotificationBell from "../components/NotificationBell";

export default function DashboardLayout({ children }) {
  return (
    <div className="flex min-h-screen bg-gradient-to-br from-black via-[#0a0a2e] to-black text-white">

      {/* SIDEBAR */}
      <Sidebar />

      {/* MAIN SECTION */}
      <div className="flex flex-col flex-1">

        {/* TOP HEADER */}
        <header className="h-16 flex items-center justify-between px-8 border-b border-white/10 bg-black/40 backdrop-blur-xl relative z-50">
          <h1 className="text-lg font-semibold tracking-wide">
            Crypto Portfolio Tracker
          </h1>

          <div className="flex items-center gap-6">
            <NotificationBell />
          </div>
        </header>

        {/* PAGE CONTENT (ONLY THIS SCROLLS) */}
        <main className="flex-1 p-10 overflow-auto">
          {children}
        </main>

      </div>
    </div>
  );
}
