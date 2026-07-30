import { isoToShortDate } from "app/utils/dateUtils";
import { toCurrency } from "app/utils/textUtils";
import TravelAppStatusBadge from "app/views/travel/shared/components/TravelAppStatusBadge";
import React from "react";
import Button from "app/components/Button";
import { ChevronRight } from "lucide-react";

export default function TravelAppSummaryTable({ apps, onSelectApp }) {
  if (!apps || apps.length === 0) {
    return null;
  }

  return (
    <div className="overflow-x-auto">
      <table className="table min-w-[48rem] table-fixed">
        <colgroup>
          <col className="w-28" />
          <col className="w-44" />
          <col />
          <col className="w-32" />
          <col className="w-40" />
          <col className="w-10" />
        </colgroup>
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell">Travel Date</th>
            <th className="table__head__cell">Traveler</th>
            <th className="table__head__cell">Destination</th>
            <th className="table__head__cell cell--number">Allotted Funds</th>
            <th className="table__head__cell">Status</th>
            <th className="table__head__cell">
              <span className="sr-only">Actions</span>
            </th>
          </tr>
        </thead>
        <tbody className="table__body table__body--highlight">
          {apps.map((app) => (
            <tr
              key={app.id}
              className="table__row"
              onClick={() => onSelectApp(app)}
            >
              <td className="table__cell">{isoToShortDate(app.startDate)}</td>
              <td className="table__cell">{app.travelerName ?? ""}</td>
              <td className="table__cell">{app.destinationSummary ?? ""}</td>
              <td className="table__cell cell--number">
                {toCurrency(app.totalAllowance ?? "")}
              </td>
              <td className="table__cell">
                <TravelAppStatusBadge status={app.status} />
              </td>
              <td
                className="table__cell px-1"
                onClick={(event) => event.stopPropagation()}
              >
                <Button
                  variant="quiet"
                  aria-label={getViewDetailsLabel(app)}
                  className="h-8 w-8 p-0"
                  onPress={() => onSelectApp(app)}
                >
                  <ChevronRight aria-hidden="true" className="h-4 w-4" />
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function getViewDetailsLabel(app) {
  const traveler = app.travelerName?.trim();
  const travelDate = isoToShortDate(app.startDate);

  if (traveler && travelDate) {
    return `View ${traveler}'s travel application for ${travelDate}`;
  }
  if (traveler) {
    return `View ${traveler}'s travel application`;
  }
  if (travelDate) {
    return `View travel application for ${travelDate}`;
  }

  return "View travel application details";
}
