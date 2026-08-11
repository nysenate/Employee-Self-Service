import { isoToShortDate } from "app/utils/dateUtils";
import { toCurrency } from "app/utils/textUtils";
import TravelAppStatusBadge from "app/views/travel/shared/components/TravelAppStatusBadge";
import React from "react";

export default function TravelAppSummaryTable({
  apps,
  onSelectApp,
  actionLabel = "View",
}) {
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
        </colgroup>
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell">Travel Date</th>
            <th className="table__head__cell">Traveler</th>
            <th className="table__head__cell">Destination</th>
            <th className="table__head__cell cell--number">Allotted Funds</th>
            <th className="table__head__cell">Status</th>
          </tr>
        </thead>
        <tbody className="table__body table__body--highlight">
          {apps.map((app) => (
            <tr
              key={app.id}
              className="table__row focus-visible:outline-2 focus-visible:-outline-offset-2 focus-visible:outline-teal-600"
              role="button"
              tabIndex={0}
              aria-label={getActionLabel(app, actionLabel)}
              onClick={() => onSelectApp(app)}
              onKeyDown={(event) =>
                handleRowKeyDown(event, () => onSelectApp(app))
              }
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
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function handleRowKeyDown(event, onSelect) {
  if (event.key !== "Enter" && event.key !== " ") return;

  event.preventDefault();
  onSelect();
}

function getActionLabel(app, actionLabel) {
  const traveler = app.travelerName?.trim();
  const travelDate = isoToShortDate(app.startDate);

  if (traveler && travelDate) {
    return `${actionLabel} ${traveler}'s travel application for ${travelDate}`;
  }
  if (traveler) {
    return `${actionLabel} ${traveler}'s travel application`;
  }
  if (travelDate) {
    return `${actionLabel} travel application for ${travelDate}`;
  }

  return `${actionLabel} travel application details`;
}
