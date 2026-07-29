import React from "react";
import Card from "app/components/Card";
import LoadingIndicator from "app/components/LoadingIndicator";
import NoMatchesFound from "app/components/NoMatchesFound";
import Modal from "app/components/Modal";
import TravelAppForm from "app/views/travel/shared/components/TravelAppForm";
import { useTravelApp } from "app/views/travel/shared/hooks/useTravelApp";
import TravelAppSummaryTable from "app/views/travel/shared/components/TravelAppSummaryTable";
import Pagination from "app/components/Pagination";

export default function TravelApplicationResults({
  apps,
  isLoading,
  limit,
  offset,
  total,
  onPageChange,
}) {
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
          {total > limit && (
            <Pagination
              limit={limit}
              offset={offset}
              total={total}
              onPageChange={onPageChange}
            />
          )}
        </div>
      </Card>

      <TravelAppFormModal app={selectedApp} onOpenChange={handleDialogChange} />
    </>
  );
}

function TravelAppFormModal({ app, onOpenChange }) {
  const { data, isPending } = useTravelApp(app?.id);

  return (
    <Modal
      isOpen={Boolean(app)}
      onOpenChange={onOpenChange}
      ariaLabel="Travel application details"
    >
      <Modal.Body>
        {isPending ? (
          <LoadingIndicator />
        ) : (
          <TravelAppForm app={data.result} showStatus className="p-5" />
        )}
      </Modal.Body>
    </Modal>
  );
}
