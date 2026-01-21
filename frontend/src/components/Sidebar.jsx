import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import {
  MdDashboard,
  MdAccountBalanceWallet,
  MdTrendingUp,
  MdPriceCheck,
  MdWarning,
  MdAssessment,
  MdMenu,
  MdClose,
  MdLogout,
  MdLink,
} from "react-icons/md";

export default function Sidebar() {
  const location = useLocation();
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(true);

  const toggleSidebar = () => setIsOpen(!isOpen);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("email");
    localStorage.removeItem("name");
    navigate("/"); // redirect to login
  };

  const menuItems = [
    { name: "Dashboard", path: "/dashboard", icon: <MdDashboard size={22} /> },
    { name: "Exchanges", path: "/exchanges", icon: <MdLink size={22} /> },

    {
      name: "Holdings",
      path: "/holdings",
      icon: <MdAccountBalanceWallet size={22} />,
    },
    { name: "Trades", path: "/trades", icon: <MdTrendingUp size={22} /> },
    { name: "Pricing", path: "/pricing", icon: <MdPriceCheck size={22} /> },
    {
      name: "Risk Alerts",
      path: "/risk-alerts",
      icon: <MdWarning size={22} />,
    },
    {
      name: "P&L Reports",
      path: "/pnl-reports",
      icon: <MdAssessment size={22} />,
    },
  ];

  return (
    <div
      className={`
        min-h-screen bg-black/30 backdrop-blur-2xl shadow-2xl border-r border-white/10
        transition-all duration-300 ease-in-out flex flex-col
        ${isOpen ? "w-64 px-6" : "w-20 px-3"}
      `}
    >
      {/* HEADER */}
      <div className="flex items-center justify-between py-6">
        {isOpen && (
          <h1 className="text-2xl font-bold text-white tracking-wide">
            <span className="text-pink-500">Crypto</span>Tracker
          </h1>
        )}

        <button
          onClick={toggleSidebar}
          className="text-white bg-white/10 p-2 rounded-xl hover:bg-white/20 transition"
        >
          {isOpen ? <MdClose size={22} /> : <MdMenu size={22} />}
        </button>
      </div>

      {/* MENU */}
      <nav className="flex flex-col gap-3 mt-4">
        {menuItems.map((item) => {
          const active = location.pathname === item.path;

          return (
            <Link
              key={item.path}
              to={item.path}
              className={`
                flex items-center gap-4 p-3 rounded-xl transition-all duration-300
                ${
                  active
                    ? "bg-pink-600/45 text-white shadow-lg shadow-pink-500/40 scale-[1.03]"
                    : "text-gray-300 hover:bg-white/10 hover:scale-[1.02] hover:shadow-md"
                }
              `}
            >
              {item.icon}
              {isOpen && <span className="font-medium">{item.name}</span>}
            </Link>
          );
        })}
      </nav>

      {/* FOOTER */}
      <div className="mt-auto pb-6 flex flex-col gap-3">
        {/* LOGOUT BUTTON */}
        <button
          onClick={handleLogout}
          className={`
            flex items-center gap-4 p-3 rounded-xl text-red-400 hover:text-white 
            hover:bg-red-600/40 transition-all duration-300 
            ${isOpen ? "justify-start" : "justify-center"}
          `}
        >
          <MdLogout size={22} />
          {isOpen && <span className="font-medium">Logout</span>}
        </button>

        {/* COPYRIGHT */}
        {isOpen && (
          <p className="text-gray-400 text-sm text-center">
            © 2025 CryptoTracker
          </p>
        )}
      </div>
    </div>
  );
}
