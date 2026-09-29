import React, { useContext, useEffect, useRef } from "react";
import { ThemeContext } from "app/ThemeContext";
import LoadingStatus from "app/components/LoadingStatus";

const spinnerColors = {
  myinfo: "text-green-700",
  time: "text-teal-700",
  supply: "text-purple-700",
  travel: "text-orange-700",
};

/** Blocks only the affected content, with the announcement outside the busy region. */
export default function BusyRegion({ message, children }) {
  const theme = useContext(ThemeContext);
  const isBusy = Boolean(message);
  const contentRef = useRef(null);
  const wasBusy = useRef(false);
  useEffect(() => {
    // Inert content can lose browser focus. Restore a useful reading position
    // only if the user has not moved elsewhere (for example, into a dialog).
    if (
      wasBusy.current &&
      !isBusy &&
      document.activeElement === document.body
    ) {
      const heading = contentRef.current?.querySelector("h1, h2");
      if (heading) {
        heading.setAttribute("tabindex", "-1");
        heading.focus();
      }
    }
    wasBusy.current = isBusy;
  }, [isBusy]);
  return (
    <div className="relative">
      <fieldset
        ref={contentRef}
        disabled={isBusy}
        inert={isBusy ? "" : undefined}
        aria-busy={isBusy}
        className="m-0 min-w-0 border-0 p-0"
      >
        {children}
      </fieldset>
      <div
        role={isBusy ? "status" : undefined}
        aria-live="polite"
        aria-atomic="true"
      >
        {isBusy && (
          <div className="absolute inset-0 z-10 cursor-wait bg-white/85">
            {/* Center within short cards; keep a viewport-sized area sticky in tall cards. */}
            <div className="sticky top-16 flex h-full max-h-[calc(100dvh-8rem)] items-center justify-center p-4">
              <div className="flex w-fit max-w-full items-center gap-3 rounded-lg border border-gray-200 bg-white px-6 py-5 shadow-md">
                <LoadingStatus
                  message={message}
                  description="This may take a moment."
                  size="lg"
                  announce={false}
                  indicatorClassName={spinnerColors[theme] ?? spinnerColors.time}
                />
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
