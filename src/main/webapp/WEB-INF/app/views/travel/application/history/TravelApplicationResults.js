import React from "react";
import LoadingStatus from "app/components/LoadingStatus";
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
  onSelectApp,
}) {
  const rows = Array.isArray(apps) ? apps : [];

  if (
    isLoading ||
    (status === TRAVEL_RESULTS_STATUS.transitioning && !rows.length)
  ) {
    return (
      <div className="mt-6">
        <LoadingStatus
          message="Loading travel applications…"
          layout="centered"
          size="lg"
          className="min-h-48 p-6"
        />
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
      <TravelAppSummaryTable
        apps={rows}
        onSelectApp={(app) => onSelectApp(app.id)}
      />
    </TravelResultsCard>
  );
}
