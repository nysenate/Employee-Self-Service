import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

export function useUnsavedChangesGuard(isDirty, isLocked = false) {
  const location = useLocation();
  const navigate = useNavigate();
  const [pendingDestination, setPendingDestination] = useState(null);

  useEffect(() => {
    if (!isDirty && !isLocked) return undefined;

    const warnBeforeUnload = (event) => {
      event.preventDefault();
      event.returnValue = "";
    };
    const guardAppLink = (event) => {
      if (shouldIgnoreClick(event)) return;

      const anchor = event.target.closest?.("a[href]");
      const destination = navigableDestination(anchor, location);
      if (!destination) return;

      event.preventDefault();
      if (isLocked) return;
      setPendingDestination(destination);
    };

    window.addEventListener("beforeunload", warnBeforeUnload);
    document.addEventListener("click", guardAppLink, true);
    return () => {
      window.removeEventListener("beforeunload", warnBeforeUnload);
      document.removeEventListener("click", guardAppLink, true);
    };
  }, [isDirty, isLocked, location]);

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

function shouldIgnoreClick(event) {
  return (
    event.defaultPrevented ||
    event.button !== 0 ||
    event.metaKey ||
    event.ctrlKey ||
    event.shiftKey ||
    event.altKey
  );
}

function navigableDestination(anchor, location) {
  if (!anchor || anchor.target || anchor.hasAttribute("download")) return null;
  const destination = new URL(anchor.href, window.location.href);
  const currentPath = `${location.pathname}${location.search}`;
  if (
    destination.origin !== window.location.origin ||
    `${destination.pathname}${destination.search}` === currentPath
  ) {
    return null;
  }
  return `${destination.pathname}${destination.search}${destination.hash}`;
}
