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
import { Dialog, DialogContent } from "app/components/ui/dialog";
import { isoToShortDate } from "app/utils/dateUtils";
import { toCurrency } from "app/utils/textUtils";
import TravelAppStatusBadge from "app/views/travel/shared/components/TravelAppStatusBadge";
import TravelAppForm from "app/views/travel/shared/components/TravelAppForm";
import { useTravelApp } from "app/views/travel/shared/hooks/useTravelApp";
import { Spinner } from "app/components/ui/spinner";

export default function TravelApplicationResults({ apps, isLoading }) {
  const [selectedApp, setSelectedApp] = React.useState(null);

  const handleRowClick = (app) => {
    setSelectedApp(app);
  };

  const handleRowKeyDown = (event, app) => {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      setSelectedApp(app);
    }
  };

  const handleDialogChange = (open) => {
    if (!open) {
      setSelectedApp(null);
    }
  };

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
    <>
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
        </div>
      </Card>

      <TravelAppFormModal app={selectedApp} onOpenChange={handleDialogChange} />
    </>
  );
}

function TravelAppFormModal({ app, onOpenChange }) {
  const { data, isPending } = useTravelApp(app?.id);

  return (
    <Dialog open={Boolean(app)} onOpenChange={onOpenChange}>
      <DialogContent>
        {isPending ? (
          <LoadingIndicator />
        ) : (
          <TravelAppForm app={data.result} showStatus className="p-5" />
        )}
      </DialogContent>
    </Dialog>
  );
}
