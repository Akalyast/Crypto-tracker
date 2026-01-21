import { useEffect, useState, useRef } from "react";
import api from "../api/axios";
import NotificationDropdown from "./NotificationDropdown";

export default function NotificationBell() {
  const [open, setOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const bellRef = useRef(null);

  /* ---------------- FETCH NOTIFICATIONS (UNCHANGED) ---------------- */
  const fetchNotifications = () => {
    api
      .get("/notifications")
      .then((res) => setNotifications(res.data || []))
      .catch(() => setNotifications([]));
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  /* ---------------- CLOSE ON OUTSIDE CLICK ---------------- */
  useEffect(() => {
    const handler = (e) => {
      if (bellRef.current && !bellRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  /* ---------------- MARK SINGLE AS READ ---------------- */
  const markAsRead = async (id) => {
    try {
      await api.post(`/notifications/${id}/read`);
      fetchNotifications();
    } catch (err) {
      console.error("Mark as read failed", err);
    }
  };

  /* ---------------- MARK ALL AS READ ---------------- */
  const markAllAsRead = async () => {
    try {
      await api.post("/notifications/read-all");
      fetchNotifications();
    } catch (err) {
      console.error("Mark all as read failed", err);
    }
  };

  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <div ref={bellRef} className="relative z-[9999]">
      {/* 🔔 Bell */}
      <button
        onClick={() => setOpen(!open)}
        className="
          relative
          p-3
          rounded-full
          bg-white/5
          hover:bg-white/10
          transition-all
          duration-300
          hover:scale-105
        "
      >
        <span className="text-xl">🔔</span>

        {unreadCount > 0 && (
          <span
            className="
              absolute
              -top-1
              -right-1
              min-w-[18px]
              h-[18px]
              px-1
              rounded-full
              bg-red-500
              text-[11px]
              font-bold
              flex
              items-center
              justify-center
              animate-pulse
              shadow-lg
            "
          >
            {unreadCount}
          </span>
        )}
      </button>

      {open && (
        <NotificationDropdown
          notifications={notifications}
          onMarkAsRead={markAsRead}
          onMarkAllAsRead={markAllAsRead}
        />
      )}
    </div>
  );
}
