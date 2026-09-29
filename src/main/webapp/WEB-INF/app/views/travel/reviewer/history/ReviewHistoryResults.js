import { useLocation } from "react-router-dom";
import React from "react";
import LoadingStatus from "app/components/LoadingStatus";
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
  const location = useLocation();
  const [selectedReview, setSelectedReview] = React.useState(
    location.state?.reviewSummary ?? null,
  );
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

  return (
    <>
      {isLoading ||
      (status === TRAVEL_RESULTS_STATUS.transitioning && !appReviews.length) ? (
        <div className="mt-6">
          <LoadingStatus
            message="Loading review history…"
            layout="centered"
            size="lg"
            className="min-h-48 p-6"
          />
        </div>
      ) : appReviews.length === 0 ? (
        <TravelEmptyResults
          itemLabel="travel reviews"
          onResetFilters={onResetFilters}
        />
      ) : (
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
      )}

      {selectedReview && (
        <TravelAppReviewModal
          reviewSummary={selectedReview}
          onOpenChange={handleDialogChange}
        />
      )}
    </>
  );
}
