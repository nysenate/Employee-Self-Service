import { useLocation } from "react-router-dom";
import React from "react";
import Badge from "app/components/Badge";
import Card from "app/components/Card";
import TravelAppSummaryTable from "app/views/travel/shared/components/TravelAppSummaryTable";
import TravelEmptyResults from "app/views/travel/shared/components/TravelEmptyResults";
import { travelRoleDisplayName } from "app/views/travel/shared/travelRoles";
import ReviewerActionModal from "./ReviewerActionModal";
import { useNotifySuccess } from "app/components/NotificationProvider";

export default function ReviewQueueResults({ queue, roleName }) {
  const location = useLocation();
  const notifySuccess = useNotifySuccess();
  const [selectedReview, setSelectedReview] = React.useState(
    location.state?.reviewSummary ?? null,
  );

  const appIdToReview = new Map();
  queue?.forEach((review) => appIdToReview.set(review.application.id, review));

  const apps = queue?.map((review) => review.application) ?? [];

  const selectApp = (app) => {
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
    notifySuccess(`Application${subject} ${action}${role}.`);
  };

  return (
    <>
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
