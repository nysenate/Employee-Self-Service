import React from "react";
import Card from "app/components/Card";
import LoadingIndicator from "app/components/LoadingIndicator";
import NoMatchesFound from "app/components/NoMatchesFound";
import { Dialog, DialogContent } from "app/components/ui/dialog";
import TravelAppForm from "app/views/travel/shared/components/TravelAppForm";
import { useTravelApp } from "app/views/travel/shared/hooks/useTravelApp";
import TravelAppSummaryTable from "app/views/travel/shared/components/TravelAppSummaryTable";

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
          <TravelAppSummaryTable
            apps={rows}
            handleRowClick={handleRowClick}
            handleRowKeyDown={handleRowKeyDown}
          />
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
