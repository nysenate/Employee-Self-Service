import React from "react";
import Spinner from "app/components/Spinner";
import { cn } from "app/utils/cn";

/**
 * Shared loading presentation. The containing page/card owns spacing and blocking.
 * Use layout="centered" when content is unavailable. Keep existing content mounted
 * during refreshes; show inline progress only when it helps explain the wait.
 * For filter transitions, put progress in the existing card header with a stable
 * height and fade the old results. Do not insert a temporary row above content.
 * A quick save normally needs only its pending button and confirmed result;
 * its follow-up refetch does not need a second loading message.
 * Set announce={false} inside an existing live region (for example BusyRegion).
 * Supply a descriptive message; use Spinner alone only when a control owns its label.
 */
export default function LoadingStatus({
  message,
  description,
  layout = "inline",
  size = "sm",
  announce = true,
  className,
  indicatorClassName,
}) {
  const centered = layout === "centered";
  const Element = centered ? "div" : "span";
  return (
    <Element
      role={announce ? "status" : undefined}
      aria-atomic={announce ? "true" : undefined}
      className={cn(
        centered
          ? "flex flex-col items-center justify-center gap-3 text-center"
          : "inline-flex items-center gap-3",
        className,
      )}
    >
      <Spinner size={size} className={indicatorClassName} />
      <span>
        <span className="font-medium">{message}</span>
        {description && (
          <span className="mt-1 block text-sm text-gray-600">
            {description}
          </span>
        )}
      </span>
    </Element>
  );
}
