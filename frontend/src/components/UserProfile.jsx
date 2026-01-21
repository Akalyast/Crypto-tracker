import { useState, useRef, useEffect } from "react";

export default function UserProfile() {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  /* ================= USER DATA ================= */
  const name = localStorage.getItem("userName") || "User";
  const email = localStorage.getItem("userEmail") || "user@email.com";

  const initials = name
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase();

  /* ================= CURRENCY ================= */
  const [currency, setCurrency] = useState(
    localStorage.getItem("currency") || "INR",
  );

  /* APPLY CURRENCY GLOBALLY */
  useEffect(() => {
    localStorage.setItem("currency", currency);

    // 🔔 Notify whole app
    window.dispatchEvent(
      new CustomEvent("currencyChanged", { detail: currency }),
    );
  }, [currency]);

  /* CLOSE DROPDOWN ON OUTSIDE CLICK */
  useEffect(() => {
    const handler = (e) => {
      if (ref.current && !ref.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  return (
    <div ref={ref} className="relative">
      {/* ================= PROFILE AVATAR ================= */}
      <button
        onClick={() => setOpen(!open)}
        className="
          w-10 h-10 rounded-full
          bg-gradient-to-br from-pink-500 to-purple-600
          flex items-center justify-center
          font-bold text-white
          hover:scale-110
          transition-all duration-300
          shadow-lg
        "
        title="Profile"
      >
        {initials}
      </button>

      {/* ================= DROPDOWN ================= */}
      {open && (
        <div
          className="
            absolute right-0 mt-4 w-80
            rounded-2xl
            bg-slate-900/95 backdrop-blur-xl
            border border-white/10
            shadow-2xl
            z-50
            animate-fade-in
          "
        >
          {/* USER INFO */}
          <div className="p-5 border-b border-white/10">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-full bg-gradient-to-br from-pink-500 to-purple-600 flex items-center justify-center font-bold text-white text-lg">
                {initials}
              </div>
              <div>
                <p className="font-semibold text-white">{name}</p>
                <p className="text-sm text-gray-400">{email}</p>
              </div>
            </div>
          </div>

          {/* SETTINGS */}
          <div className="p-5 space-y-5 text-sm">
            {/* CURRENCY */}
            <div className="flex justify-between items-center">
              <span className="text-gray-400">Preferred Currency</span>
              <select
                value={currency}
                onChange={(e) => setCurrency(e.target.value)}
                className="
                  bg-slate-800 text-white
                  px-3 py-2
                  rounded-lg
                  border border-white/10
                  focus:outline-none
                  focus:ring-2 focus:ring-pink-500
                  transition
                "
              >
                <option value="INR">₹ INR</option>
                <option value="USD">$ USD</option>
                <option value="EUR">€ EUR</option>
              </select>
            </div>

            {/* INFO NOTE */}
            <div className="text-xs text-gray-500 text-center pt-2 border-t border-white/10">
              Profile settings are synced automatically
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
