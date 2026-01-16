import React from "react";
import { isoToShortDate } from "app/utils/dateUtils";

export function ReviewSummary({ review }) {
  const summary = getReviewSummary(review);
  return (
    <div className="text-muted-foreground grid gap-2 text-sm">
      <div className="grid grid-cols-[120px_1fr] gap-x-3">
        <div className="text-foreground font-semibold">Traveler</div>
        <div>{summary.travelerName}</div>
        <div className="text-foreground font-semibold">Destination</div>
        <div>{summary.destinationLabel}</div>
        <div className="text-foreground font-semibold">Dates of travel</div>
        <div>{summary.travelDates}</div>
      </div>
    </div>
  );
}

function getReviewSummary(review) {
  const travelApp = review?.travelApplication ?? {};
  const amendment = travelApp?.activeAmendment ?? {};
  const traveler = travelApp?.traveler ?? {};
  const destinationLabel = amendment?.destinationSummary || "N/A";
  const travelDates = `${isoToShortDate(
    amendment?.startDate,
  )} to ${isoToShortDate(amendment?.endDate)}`;

  return {
    travelerName: traveler.fullName || "N/A",
    destinationLabel,
    travelDates,
  };
}
