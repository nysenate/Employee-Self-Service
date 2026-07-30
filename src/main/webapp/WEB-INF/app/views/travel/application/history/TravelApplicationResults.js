import React from "react";
import Card from "app/components/Card";
import LoadingIndicator from "app/components/LoadingIndicator";
import Modal from "app/components/Modal";
import Button from "app/components/Button";
import TravelAppForm from "app/views/travel/shared/components/TravelAppForm";
import { useTravelApp } from "app/views/travel/shared/hooks/useTravelApp";
import TravelAppSummaryTable from "app/views/travel/shared/components/TravelAppSummaryTable";
import Pagination from "app/components/Pagination";
import { LoaderCircle, RotateCcw, SearchX } from "lucide-react";
import { cn } from "app/utils/cn";

export default function TravelApplicationResults({
  apps,
  isLoading,
  isUpdating,
  isPlaceholderData,
  hasActiveFilters,
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

  if (isLoading || (isPlaceholderData && !rows.length)) {
    return (
      <div className="mt-6">
        <LoadingIndicator />
      </div>
    );
  }

  if (!rows.length) {
    return (
      <EmptyResults
        canReset={hasActiveFilters}
        onResetFilters={onResetFilters}
      />
    );
  }

  return (
    <>
      <Card className="mt-6">
        <div className="p-4">
          <ResultsHeader
            count={rows.length}
            isUpdating={isUpdating}
            isPlaceholderData={isPlaceholderData}
            offset={offset}
            total={total}
            canReset={hasActiveFilters}
            onResetFilters={onResetFilters}
          />
          <div
            aria-busy={isUpdating}
            className={cn("transition-opacity", isUpdating && "opacity-60")}
          >
            <TravelAppSummaryTable apps={rows} onSelectApp={selectApp} />
          </div>
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

function ResultsHeader({
  count,
  isUpdating,
  isPlaceholderData,
  offset,
  total,
  canReset,
  onResetFilters,
}) {
  const lastResult = Math.min(offset + count - 1, total);

  return (
    <div className="mb-3 flex min-h-9 flex-wrap items-center justify-between gap-2 border-b border-gray-200 pb-3">
      <div aria-live="polite" className="flex items-center gap-3">
        {isPlaceholderData ? (
          <span className="inline-flex items-center gap-1.5 font-semibold">
            <LoaderCircle aria-hidden="true" className="h-4 w-4 animate-spin" />
            Updating results
          </span>
        ) : (
          <>
            <span className="font-semibold">
              Showing {offset}–{lastResult} of {total}{" "}
              {total === 1 ? "application" : "applications"}
            </span>
            {isUpdating && (
              <span className="inline-flex items-center gap-1.5 text-sm text-gray-500">
                <LoaderCircle
                  aria-hidden="true"
                  className="h-4 w-4 animate-spin"
                />
                Refreshing
              </span>
            )}
          </>
        )}
      </div>
      {canReset && (
        <Button
          variant="quiet"
          contentClassName="gap-2"
          onPress={onResetFilters}
        >
          <RotateCcw aria-hidden="true" className="h-4 w-4" />
          Reset filters
        </Button>
      )}
    </div>
  );
}

function EmptyResults({ canReset, onResetFilters }) {
  return (
    <Card className="mt-6">
      <div className="flex flex-col items-center px-4 py-10 text-center">
        <SearchX aria-hidden="true" className="mb-3 h-10 w-10 text-gray-400" />
        <h2 className="text-xl font-semibold">
          No travel applications match these filters
        </h2>
        <p className="mt-1 text-gray-600">
          Try adjusting the filters above
          {canReset ? " or reset them to the defaults." : "."}
        </p>
        {canReset && (
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
