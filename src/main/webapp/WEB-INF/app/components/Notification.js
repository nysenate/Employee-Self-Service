import React from "react";
import { cn } from "app/utils/cn";

/**
 * A banner calling out information, a warning, or an error.
 * Ported from the legacy "essNotification" AngularJS directive
 * (assets/js/src/common/ess-notifications.js).
 *
 * @param level One of "info", "warn" or "error". Defaults to "info".
 * @param title Heading text for the banner.
 * @param message A single line of body text. Use children for anything richer.
 */
export default function Notification({
  level = "info",
  title,
  message,
  className,
  children,
}) {
  return (
    <div
      className={cn(
        "border-b border-gray-200 p-[0.3em] text-center font-semibold",
        /*
         * Preflight strips the margins the legacy banners got from the browser, which is what
         * spaces the body text away from the heading. Restored for whatever is passed in.
         */
        "[&_p]:my-[1em] [&_ul]:my-[1em]",
        levelStyles[level] || levelStyles.info,
        className,
      )}
    >
      {/*
       * The legacy heading is a plain h2 under "h1, h2 { font-weight: normal }": large, not
       * bold, and set well apart from the body by its own margins.
       */}
      {title && (
        <h2 className="my-[0.83em] text-[1.5em] font-normal">{title}</h2>
      )}
      {message && <p>{message}</p>}
      {children}
    </div>
  );
}

const levelStyles = {
  info: "bg-white text-[#0e4e5a]",
  warn: "bg-orange-600 text-white",
  // The legacy error banners are red orange, which has no equivalent in the theme palette.
  error: "bg-[#e64727] text-white",
};
