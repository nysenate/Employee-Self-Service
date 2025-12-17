import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "app/components/ui/table";
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
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Travel Date</TableHead>
          <TableHead>Traveler</TableHead>
          <TableHead>Destination</TableHead>
          <TableHead numeric>Allotted Funds</TableHead>
          <TableHead>Status</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {apps.map((app) => (
          <TableRow
            key={app.id}
            role="button"
            tabIndex={0}
            className="cursor-pointer"
            onClick={() => handleRowClick(app)}
            onKeyDown={(event) => handleRowKeyDown(event, app)}
          >
            <TableCell>{isoToShortDate(app.startDate)}</TableCell>
            <TableCell>{app.travelerName ?? ""}</TableCell>
            <TableCell>{app.destinationSummary ?? ""}</TableCell>
            <TableCell numeric>
              {toCurrency(app.totalAllowance ?? "")}
            </TableCell>
            <TableCell>
              <TravelAppStatusBadge status={app.status} />
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}
