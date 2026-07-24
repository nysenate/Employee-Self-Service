import React from "react";
import { cn } from "app/utils/cn";

/**
 * A captioned box of hours, as used by the accrual and allowance bars.
 * Ported from the legacy .captioned-hour-square styles (assets/css/less/time.less).
 */
export function HourSquare({ caption, captionClassName, className, children }) {
  return (
    <div className={cn("my-1 px-1", className)}>
      <div
        className={cn(
          "px-1 py-1 leading-5 font-semibold text-white",
          captionClassName || "bg-[#5c7474]",
        )}
      >
        {caption}
      </div>
      <div className="border border-t-0 border-gray-200 bg-white leading-[30px] font-semibold text-[#0e4e5a]">
        {children}
      </div>
    </div>
  );
}

/** One of the sub columns within a wide hour square. */
export function HourSquareColumn({ caption, children }) {
  return (
    <div className="min-w-28 grow border-r border-gray-200 last:border-r-0">
      {caption && (
        <div className="px-1 py-1 leading-5 font-semibold text-[#0e4e5a]">
          {caption}
        </div>
      )}
      {children}
    </div>
  );
}

/**
 * An hour difference, shown in green when there is a surplus and red when there is a deficit.
 * Ported from the legacy "hoursDiffHighlighter" filter.
 */
export function HoursDiff({ hours }) {
  const color = hours > 0 ? "#09BB05" : hours < 0 ? "#BB0505" : "#0e4e5a";
  return (
    <span style={{ color }}>
      {hours > 0 && "+"}
      {hours}
    </span>
  );
}
