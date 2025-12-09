import React from "react";
import Card from "app/components/Card";
import LoadingIndicator from "app/components/LoadingIndicator";
import NoMatchesFound from "app/components/NoMatchesFound";
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

export default function TravelApplicationResults({ apps, isLoading }) {
  if (isLoading) {
    return (
      <div className="mt-6">
        <LoadingIndicator />
      </div>
    );
  }

  const rows = Array.isArray(apps) ? apps : [];

  if (!rows.length) {
    return <NoMatchesFound className="mt-6" />;
  }

  return (
    <Card className="mt-6">
      <div className="p-4">
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
            {rows.map((app) => (
              <TableRow key={app.id}>
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
      </div>
    </Card>
  );
}
