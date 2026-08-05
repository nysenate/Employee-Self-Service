import React from "react";
import Card from "app/components/Card";
import LoadingIndicator from "app/components/LoadingIndicator";
import Modal from "app/components/Modal";
import Button from "app/components/Button";
import TravelAppForm from "app/views/travel/shared/components/TravelAppForm";
import { useTravelApp } from "app/views/travel/shared/hooks/useTravelApp";
import TravelAppSummaryTable from "app/views/travel/shared/components/TravelAppSummaryTable";
import Pagination from "app/components/Pagination";
import { RotateCcw, SearchX } from "lucide-react";
import TravelResultsHeader from "app/views/travel/shared/components/TravelResultsHeader";
import TravelResultsContent from "app/views/travel/shared/components/TravelResultsContent";
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
    return <EmptyResults onResetFilters={onResetFilters} />;
  }

  return (
    <>
      <Card className="mt-6">
        <div className="p-4">
          <TravelResultsHeader
            count={rows.length}
            status={status}
            offset={offset}
            total={total}
            itemLabel={APPLICATION_ITEM_LABEL}
            onResetFilters={onResetFilters}
          />
          <TravelResultsContent status={status}>
            <TravelAppSummaryTable apps={rows} onSelectApp={selectApp} />
          </TravelResultsContent>
          <Pagination
            limit={limit}
            offset={offset}
            total={total}
            onPageChange={onPageChange}
          />
        </div>
      </Card>

      <TravelAppFormModal app={selectedApp} onOpenChange={handleDialogChange} />
    </>
  );
}

function EmptyResults({ onResetFilters }) {
  return (
    <Card className="mt-6">
      <div className="flex flex-col items-center px-4 py-10 text-center">
        <SearchX aria-hidden="true" className="mb-3 h-10 w-10 text-gray-400" />
        <h2 className="text-xl font-semibold">
          No travel applications match these filters
        </h2>
        <p className="mt-1 text-gray-600">
          Try adjusting the filters above
          {onResetFilters ? " or reset them to the defaults." : "."}
        </p>
        {onResetFilters && (
          <Button
            variant="secondary"
            className="mt-4"
            contentClassName="gap-2"
            onPress={onResetFilters}
          >
            <RotateCcw aria-hidden="true" className="h-4 w-4" />
            Reset filters
          </Button>
        )}
      </div>
    </Card>
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
