import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

export function useUnsavedChangesGuard(isDirty) {
  const location = useLocation();
  const navigate = useNavigate();
  const [pendingDestination, setPendingDestination] = useState(null);

  useEffect(() => {
    if (!isDirty) return undefined;

    const warnBeforeUnload = (event) => {
      event.preventDefault();
      event.returnValue = "";
    };
    const guardAppLink = (event) => {
      if (
        event.defaultPrevented ||
        event.button !== 0 ||
        event.metaKey ||
        event.ctrlKey ||
        event.shiftKey ||
        event.altKey
      ) {
        return;
      }

      const anchor = event.target.closest?.("a[href]");
      if (!anchor || anchor.target || anchor.hasAttribute("download")) return;

      const destination = new URL(anchor.href, window.location.href);
      if (
        destination.origin !== window.location.origin ||
        `${destination.pathname}${destination.search}` ===
          `${location.pathname}${location.search}`
      ) {
        return;
      }

      event.preventDefault();
      setPendingDestination(
        `${destination.pathname}${destination.search}${destination.hash}`,
      );
    };

    window.addEventListener("beforeunload", warnBeforeUnload);
    document.addEventListener("click", guardAppLink, true);
    return () => {
      window.removeEventListener("beforeunload", warnBeforeUnload);
      document.removeEventListener("click", guardAppLink, true);
    };
  }, [isDirty, location]);

  return {
    isOpen: pendingDestination !== null,
    stay: () => setPendingDestination(null),
    leave: () => {
      const destination = pendingDestination;
      setPendingDestination(null);
      if (destination) navigate(destination);
    },
  };
}
