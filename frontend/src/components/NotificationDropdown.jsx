import { useNavigate } from "react-router-dom";

export default function NotificationDropdown({
  notifications,
  onMarkAsRead,
  onMarkAllAsRead,
}) {
  const navigate = useNavigate();

  const handleClick = (n) => {
    if (!n.read) onMarkAsRead(n.id);

    if (n.type === "INFO") navigate("/trades");
    else if (n.type === "WARNING") navigate("/risk-alerts");
    else if (n.type === "SUCCESS") navigate("/pnl");
  };

  return (
    <div
      className="
        absolute
        right-0
        mt-4
        w-[380px]
        bg-[#0b0b24]
        border border-white/10
        rounded-2xl
        shadow-2xl
        z-[9999]
      "
    >
      {/* HEADER */}
      <div className="px-5 py-4 border-b border-white/10 flex justify-between items-center">
        <h3 className="text-sm font-semibold tracking-wide">Notifications</h3>

        <button
          onClick={onMarkAllAsRead}
          className="text-xs text-blue-400 hover:text-blue-300 transition"
        >
          Mark all as read
        </button>
      </div>

      {/* LIST */}
      <div className="max-h-96 overflow-y-auto custom-scrollbar">
        {notifications.length === 0 && (
          <div className="px-6 py-10 text-center text-sm text-gray-400">
            🎉 You’re all caught up!
          </div>
        )}

        {notifications.map((n) => (
          <div
            key={n.id}
            onClick={() => handleClick(n)}
            className={`
              px-5 py-4
              border-b border-white/5
              cursor-pointer
              transition-all
              duration-200
              ${
                !n.read
                  ? "bg-white/5 hover:bg-white/10"
                  : "opacity-60 hover:opacity-90"
              }
            `}
          >
            <div className="flex justify-between items-center mb-1">
              <span className="font-medium text-sm">{n.title}</span>

              <span
                className={`text-[10px] px-2 py-0.5 rounded-full ${
                  n.type === "WARNING"
                    ? "bg-red-500/20 text-red-400"
                    : n.type === "SUCCESS"
                      ? "bg-green-500/20 text-green-400"
                      : "bg-blue-500/20 text-blue-400"
                }`}
              >
                {n.type}
              </span>
            </div>

            <p className="text-xs text-gray-400 leading-relaxed">{n.message}</p>

            {!n.read && (
              <span className="inline-block mt-2 text-[10px] text-blue-400">
                ● Unread
              </span>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
