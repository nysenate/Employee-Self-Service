import React from "react";
import { cn } from "app/utils/cn";
import { TRAVEL_RESULTS_STATUS } from "app/views/travel/shared/travelResultsStatus";

export default function TravelResultsContent({ children, status }) {
  const isBusy = status !== TRAVEL_RESULTS_STATUS.ready;
  const shouldFade = status === TRAVEL_RESULTS_STATUS.transitioning;

  return (
    <div
      aria-busy={isBusy}
      className={cn("transition-opacity", shouldFade && "opacity-60")}
    >
      {children}
    </div>
  );
}
