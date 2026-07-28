import React from "react";
import Card from "app/components/Card";
import NoMatchesFound from "app/components/NoMatchesFound";
import TravelAppSummaryTable from "app/views/travel/shared/components/TravelAppSummaryTable";
import ReviewerActionModal from "./ReviewerActionModal";

export default function ReviewQueueResults({ queue }) {
  if (queue?.length === 0) {
    return <NoMatchesFound className="mt-6" />;
  }

  const [selectedReview, setSelectedReview] = React.useState(null);

  const appIdToReview = new Map();
  queue.forEach((review) => appIdToReview.set(review.application.id, review));

  const apps = queue.map((review) => review.application);

  const handleRowClick = (app) => {
    setSelectedReview(appIdToReview.get(app.id));
  };

  const handleRowKeyDown = (event, app) => {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      setSelectedReview(appIdToReview.get(app.id));
    }
  };

  const handleIsOpenChange = (open) => {
    if (!open) {
      setSelectedReview(null);
    }
  };

  return (
    <>
      <Card className="mt-6">
        <Card.Header>
          <Card.Title>Applications to Review</Card.Title>
        </Card.Header>
        <Card.Content>
          <TravelAppSummaryTable
            apps={apps}
            handleRowClick={handleRowClick}
            handleRowKeyDown={handleRowKeyDown}
          />
        </Card.Content>
      </Card>

      {selectedReview && (
        <ReviewerActionModal
          reviewSummary={selectedReview}
          setIsOpen={handleIsOpenChange}
        />
      )}
    </>
  );
}
