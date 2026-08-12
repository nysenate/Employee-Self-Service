import React from "react";
import LoadingIndicator from "app/components/LoadingIndicator";
import TravelAppSummaryTable from "app/views/travel/shared/components/TravelAppSummaryTable";
import TravelEmptyResults from "app/views/travel/shared/components/TravelEmptyResults";
import TravelResultsCard from "app/views/travel/shared/components/TravelResultsCard";
import { TRAVEL_RESULTS_STATUS } from "app/views/travel/shared/travelResultsStatus";
import TravelAppReviewModal from "app/views/travel/reviewer/history/TravelAppReviewModal";

const REVIEW_ITEM_LABEL = {
  singular: "review",
  plural: "reviews",
};

export default function ReviewHistoryResults({
  data,
  isLoading,
  status,
  limit,
  offset,
  onResetFilters,
  onPageChange,
}) {
  const [selectedReview, setSelectedReview] = React.useState(null);
  const appReviews = data?.result ?? [];
  const total = data?.total ?? 0;

  const appIdToReview = new Map();
  appReviews.forEach((review) =>
    appIdToReview.set(review.application.id, review),
  );

  const apps = appReviews.map((review) => review.application);

  const selectApp = (app) => {
    setSelectedReview(appIdToReview.get(app.id));
  };

  const handleDialogChange = (open) => {
    if (!open) {
      setSelectedReview(null);
    }
  };

  if (
    isLoading ||
    (status === TRAVEL_RESULTS_STATUS.transitioning && !appReviews.length)
  ) {
    return (
      <div className="mt-6">
        <LoadingIndicator />
      </div>
    );
  }

  if (appReviews.length === 0) {
    return (
      <TravelEmptyResults
        itemLabel="travel reviews"
        onResetFilters={onResetFilters}
      />
    );
  }

  return (
    <>
      <TravelResultsCard
        count={appReviews.length}
        status={status}
        limit={limit}
        offset={offset}
        total={total}
        itemLabel={REVIEW_ITEM_LABEL}
        onResetFilters={onResetFilters}
        onPageChange={onPageChange}
      >
        <TravelAppSummaryTable apps={apps} onSelectApp={selectApp} />
      </TravelResultsCard>

      {selectedReview && (
        <TravelAppReviewModal
          reviewSummary={selectedReview}
          onOpenChange={handleDialogChange}
        />
      )}
    </>
  );
}
