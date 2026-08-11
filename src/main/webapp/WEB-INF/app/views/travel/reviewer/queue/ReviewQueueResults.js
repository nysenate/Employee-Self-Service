import React from "react";
import Badge from "app/components/Badge";
import Card from "app/components/Card";
import TravelAppSummaryTable from "app/views/travel/shared/components/TravelAppSummaryTable";
import TravelEmptyResults from "app/views/travel/shared/components/TravelEmptyResults";
import { travelRoleDisplayName } from "app/views/travel/shared/travelRoles";
import ReviewerActionModal from "./ReviewerActionModal";

export default function ReviewQueueResults({ queue, roleName }) {
  const [selectedReview, setSelectedReview] = React.useState(null);
  const [successMessage, setSuccessMessage] = React.useState(null);

  React.useEffect(() => {
    if (!successMessage) return undefined;

    const timeoutId = window.setTimeout(() => setSuccessMessage(null), 6000);
    return () => window.clearTimeout(timeoutId);
  }, [successMessage]);

  const appIdToReview = new Map();
  queue?.forEach((review) => appIdToReview.set(review.application.id, review));

  const apps = queue?.map((review) => review.application) ?? [];

  const selectApp = (app) => {
    setSuccessMessage(null);
    setSelectedReview(appIdToReview.get(app.id));
  };

  const handleIsOpenChange = (open) => {
    if (!open) {
      setSelectedReview(null);
    }
  };

  const handleReviewCompleted = ({ action, review }) => {
    const travelerName = review?.travelApplication?.traveler?.fullName;
    const reviewingAs = travelRoleDisplayName(review?.pendingReviewerRole);
    const subject = travelerName ? ` for ${travelerName}` : "";
    const role = reviewingAs ? ` as ${reviewingAs}` : "";
    setSuccessMessage(`Application${subject} ${action}${role}.`);
  };

  return (
    <>
      {successMessage && (
        <div
          role="status"
          className="mt-6 border border-green-300 bg-green-100 px-4 py-3 font-medium text-green-900"
        >
          {successMessage}
        </div>
      )}

      {apps.length === 0 ? (
        <TravelEmptyResults
          title={
            roleName
              ? `No applications awaiting ${roleName} review`
              : "No applications awaiting your review"
          }
          description={
            roleName
              ? `You're all caught up in the ${roleName} queue.`
              : "You're all caught up."
          }
        />
      ) : (
        <Card className="mt-6">
          <Card.Header className="flex-col gap-1">
            <div className="flex items-center gap-2">
              <Card.Title>
                {roleName
                  ? `${roleName} Review Queue`
                  : "Applications to Review"}
              </Card.Title>
              <Badge value={apps.length} />
            </div>
            {roleName && (
              <div className="text-sm text-gray-600">
                {apps.length}{" "}
                {apps.length === 1 ? "application" : "applications"} awaiting
                your review
              </div>
            )}
          </Card.Header>
          <Card.Content>
            <TravelAppSummaryTable
              apps={apps}
              onSelectApp={selectApp}
              actionLabel="Review"
            />
          </Card.Content>
        </Card>
      )}

      {selectedReview && (
        <ReviewerActionModal
          reviewSummary={selectedReview}
          setIsOpen={handleIsOpenChange}
          onReviewCompleted={handleReviewCompleted}
        />
      )}
    </>
  );
}
