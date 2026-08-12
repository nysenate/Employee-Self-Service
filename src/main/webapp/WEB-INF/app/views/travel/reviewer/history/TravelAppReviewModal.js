import React from "react";
import Button from "app/components/Button";
import LoadingIndicator from "app/components/LoadingIndicator";
import Modal from "app/components/Modal";
import TravelAppReviewForm from "app/views/travel/shared/components/TravelAppReviewForm";
import { useTravelReview } from "app/views/travel/shared/hooks/useTravelReview";

export default function TravelAppReviewModal({ reviewSummary, onOpenChange }) {
  const { data, isPending } = useTravelReview(reviewSummary?.appReviewId);
  const review = data?.result;

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
        <div className="flex items-center gap-6 px-3 py-1.5">
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
      </Modal.Controls>
    </Modal>
  );
}
