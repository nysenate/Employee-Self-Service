import React from "react";
import LoadingIndicator from "app/components/LoadingIndicator";
import Modal from "app/components/Modal";
import TravelAppForm from "app/views/travel/shared/components/TravelAppForm";
import { useTravelApp } from "app/views/travel/shared/hooks/useTravelApp";
import TravelAppSummaryTable from "app/views/travel/shared/components/TravelAppSummaryTable";
import TravelEmptyResults from "app/views/travel/shared/components/TravelEmptyResults";
import TravelResultsCard from "app/views/travel/shared/components/TravelResultsCard";
import { TRAVEL_RESULTS_STATUS } from "app/views/travel/shared/travelResultsStatus";

const APPLICATION_ITEM_LABEL = {
  singular: "application",
  plural: "applications",
};

export default function TravelApplicationResults({
  apps,
  isLoading,
  status,
  limit,
  offset,
  total,
  onResetFilters,
  onPageChange,
}) {
  const [selectedApp, setSelectedApp] = React.useState(null);
  const rows = Array.isArray(apps) ? apps : [];

  const selectApp = (app) => {
    setSelectedApp(app);
  };

  const handleDialogChange = (open) => {
    if (!open) {
      setSelectedApp(null);
    }
  };

  if (
    isLoading ||
    (status === TRAVEL_RESULTS_STATUS.transitioning && !rows.length)
  ) {
    return (
      <div className="mt-6">
        <LoadingIndicator />
      </div>
    );
  }

  if (!rows.length) {
    return (
      <TravelEmptyResults
        itemLabel="travel applications"
        onResetFilters={onResetFilters}
      />
    );
  }

  return (
    <>
      <TravelResultsCard
        count={rows.length}
        status={status}
        limit={limit}
        offset={offset}
        total={total}
        itemLabel={APPLICATION_ITEM_LABEL}
        onResetFilters={onResetFilters}
        onPageChange={onPageChange}
      >
        <TravelAppSummaryTable apps={rows} onSelectApp={selectApp} />
      </TravelResultsCard>

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
