import { isoToShortDate } from "app/utils/dateUtils";
import { toCurrency } from "app/utils/textUtils";
import TravelAppStatusBadge from "app/views/travel/shared/components/TravelAppStatusBadge";
import React from "react";

export default function TravelAppSummaryTable({
  apps,
  handleRowClick,
  handleRowKeyDown,
}) {
  if (!apps || apps.length === 0) {
    return null;
  }

  return (
    <table className="table">
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
            role="button"
            tabIndex={0}
            className="table__row"
            onClick={() => handleRowClick(app)}
            onKeyDown={(event) => handleRowKeyDown(event, app)}
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
  );
}
