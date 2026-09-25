import React from "react";
import Button from "app/components/Button";
import LoadingIndicator from "app/components/LoadingIndicator";
import Modal from "app/components/Modal";
import { useAdminEditAction } from "app/views/travel/application/edit/useAdminEditAction";
import TravelAppReviewForm from "app/views/travel/shared/components/TravelAppReviewForm";
import { useTravelReview } from "app/views/travel/shared/hooks/useTravelReview";

export default function TravelAppReviewModal({ reviewSummary, onOpenChange }) {
  const { data, isPending } = useTravelReview(reviewSummary?.appReviewId);
  const review = data?.result;
  const { canEdit, onEdit } = useAdminEditAction(reviewSummary, review);

  if (isPending || !review) {
    return (
      <Modal
        isOpen={Boolean(reviewSummary)}
        onOpenChange={onOpenChange}
        ariaLabel="Travel review details"
      >
        <Modal.Body>
          <LoadingIndicator />
        </Modal.Body>
      </Modal>
    );
  }

  const pdfHref = `${window.location.origin}/api/v1/travel/applications/${review.travelApplication.id}.pdf`;

  return (
    <Modal
      isOpen={Boolean(reviewSummary)}
      onOpenChange={onOpenChange}
      ariaLabel="Travel review details"
    >
      <Modal.Body>
        <TravelAppReviewForm appReview={review} />
      </Modal.Body>
      <Modal.Controls>
        <div className="grid w-full grid-cols-[1fr_auto] items-center gap-6 px-3 py-1.5 sm:justify-center">
          <div className="flex flex-wrap items-center justify-center gap-3">
            {canEdit && (
              <Button variant="secondary" onPress={onEdit}>
                Edit Application
              </Button>
            )}
          </div>
          <div className="flex items-center justify-end gap-3">
            <a href={pdfHref} target="_blank" rel="noopener noreferrer">
              Print
            </a>
            <Button
              variant="secondary"
              className="w-20"
              onPress={() => onOpenChange(false)}
            >
              Close
            </Button>
          </div>
        </div>
      </Modal.Controls>
    </Modal>
  );
}
