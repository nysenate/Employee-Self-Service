import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useRef,
  useState,
} from "react";
import { Check, X } from "lucide-react";
import Button from "app/components/Button";

const NotificationContext = createContext(null);
const DISPLAY_DURATION = 4000;

/** One shared host keeps confirmations visible when their originating page unmounts. */
export default function NotificationProvider({ children }) {
  const [notifications, setNotifications] = useState([]);
  const sequence = useRef(0);
  const notifySuccess = useCallback((message) => {
    const notification = { id: ++sequence.current, message };
    setNotifications((current) => {
      // Repeating the same action refreshes its confirmation, without stacking duplicates.
      const existing = current.findIndex((item) => item.message === message);
      if (existing < 0) return [...current, notification];
      return current.map((item, index) =>
        index === existing ? notification : item,
      );
    });
  }, []);
  const dismiss = useCallback((id) => {
    setNotifications((current) => current.filter((item) => item.id !== id));
  }, []);

  return (
    <NotificationContext.Provider value={notifySuccess}>
      {children}
      <SuccessNotification
        notification={notifications[0]}
        onDismiss={dismiss}
      />
    </NotificationContext.Provider>
  );
}

export function useNotifySuccess() {
  const notify = useContext(NotificationContext);
  if (!notify)
    throw new Error("useNotifySuccess requires NotificationProvider.");
  return notify;
}

function SuccessNotification({ notification, onDismiss }) {
  const [hovered, setHovered] = useState(false);
  const [focused, setFocused] = useState(false);
  const [away, setAway] = useState(document.hidden);
  const remaining = useRef(DISPLAY_DURATION);
  const id = notification?.id;

  useEffect(() => {
    const pause = () => setAway(true);
    const resume = () => setAway(document.hidden);
    window.addEventListener("blur", pause);
    window.addEventListener("focus", resume);
    document.addEventListener("visibilitychange", resume);
    return () => {
      window.removeEventListener("blur", pause);
      window.removeEventListener("focus", resume);
      document.removeEventListener("visibilitychange", resume);
    };
  }, []);

  useEffect(() => {
    remaining.current = DISPLAY_DURATION;
    if (!id) {
      setHovered(false);
      setFocused(false);
    }
  }, [id]);

  useEffect(() => {
    if (!id || hovered || focused || away) return undefined;
    const started = Date.now();
    const timeout = window.setTimeout(() => onDismiss(id), remaining.current);
    return () => {
      window.clearTimeout(timeout);
      remaining.current = Math.max(
        0,
        remaining.current - (Date.now() - started),
      );
    };
  }, [id, hovered, focused, away, onDismiss]);

  return (
    <div
      className="pointer-events-none fixed inset-x-4 top-24 z-40 sm:right-6 sm:left-auto sm:w-96"
      onPointerEnter={() => setHovered(true)}
      onPointerLeave={() => setHovered(false)}
      onFocusCapture={() => setFocused(true)}
      onBlurCapture={(event) => {
        if (!event.currentTarget.contains(event.relatedTarget))
          setFocused(false);
      }}
    >
      <div
        className={
          notification
            ? "pointer-events-auto flex items-start gap-3 rounded-lg border border-green-200 bg-green-50 p-4 text-gray-900 shadow-lg"
            : ""
        }
      >
        {notification && (
          <span className="flex size-7 shrink-0 items-center justify-center rounded-full bg-green-700 text-white">
            <Check aria-hidden="true" className="size-4" />
          </span>
        )}
        <div
          role={notification ? "status" : undefined}
          aria-live="polite"
          aria-atomic="true"
          className="min-w-0 flex-1 pt-0.5 font-medium"
        >
          {notification?.message}
        </div>
        {notification && (
          <Button
            variant="quiet"
            aria-label="Dismiss notification"
            className="-mt-1 -mr-1 p-1.5"
            onPress={() => onDismiss(id)}
          >
            <X aria-hidden="true" className="size-4" />
          </Button>
        )}
      </div>
    </div>
  );
}
