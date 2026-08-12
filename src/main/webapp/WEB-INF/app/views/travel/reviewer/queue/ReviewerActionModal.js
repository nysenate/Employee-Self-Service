import React, { useEffect, useState } from "react";
import { useTravelReview } from "app/views/travel/shared/hooks/useTravelReview";
import Modal from "app/components/Modal";
import TravelAppReviewForm from "app/views/travel/shared/components/TravelAppReviewForm";
import Button from "app/components/Button";
import LoadingIndicator from "app/components/LoadingIndicator";
import ApproveConfirmDialog from "./ApproveConfirmDialog";
import DisapproveConfirmDialog from "./DisapproveConfirmDialog";

export default function ReviewerActionModal({
  reviewSummary,
  setIsOpen,
  onReviewCompleted,
}) {
  const { data, isPending } = useTravelReview(reviewSummary?.appReviewId);
  const review = data?.result;
  const [approveDialogOpen, setApproveDialogOpen] = useState(false);
  const [disapproveDialogOpen, setDisapproveDialogOpen] = useState(false);

  useEffect(() => {
    if (!reviewSummary) {
      setApproveDialogOpen(false);
      setDisapproveDialogOpen(false);
    }
  }, [reviewSummary]);

  if (isPending || !review) {
    return (
      <Modal
        isOpen={Boolean(reviewSummary)}
        onOpenChange={setIsOpen}
        ariaLabel="Travel review details"
      >
        <Modal.Body>
          <LoadingIndicator />
        </Modal.Body>
      </Modal>
    );
  }

  const pdfHref = `${window.location.origin}/api/v1/travel/applications/${review?.travelApplication.id}.pdf`;
  return (
    <Modal
      isOpen={Boolean(reviewSummary)}
      onOpenChange={setIsOpen}
      ariaLabel="Travel review details"
    >
      <Modal.Body>
        <TravelAppReviewForm appReview={review} />
      </Modal.Body>
      <Modal.Controls>
        <div className="grid w-full grid-cols-[1fr_auto] items-center gap-6 px-3 py-1.5 sm:justify-center">
          <div className="flex items-center justify-center gap-3">
            <Button
              variant="primary"
              onPress={() => setApproveDialogOpen(true)}
            >
              Approve Application
            </Button>
            <Button
              variant="destructive"
              onPress={() => setDisapproveDialogOpen(true)}
            >
              Disapprove Application
            </Button>
            <Button variant="secondary">Edit Application</Button>
          </div>
          <div className="flex items-center gap-3">
            <a href={pdfHref} target="_blank" rel="noopener noreferrer">
              Print
            </a>
            <Button
              variant="secondary"
              className="w-20"
              onPress={() => setIsOpen(false)}
            >
              Close
            </Button>
          </div>
        </div>
      </Modal.Controls>
      <ApproveConfirmDialog
        open={approveDialogOpen}
        onOpenChange={setApproveDialogOpen}
        review={review}
        onApproved={() => {
          setIsOpen(false);
          onReviewCompleted?.({ action: "approved", review });
        }}
      />
      <DisapproveConfirmDialog
        open={disapproveDialogOpen}
        onOpenChange={setDisapproveDialogOpen}
        review={review}
        onDisapproved={() => {
          setIsOpen(false);
          onReviewCompleted?.({ action: "disapproved", review });
        }}
      />
    </Modal>
  );
}
