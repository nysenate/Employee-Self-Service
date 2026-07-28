import React, { useState } from "react";
import Modal from "app/components/Modal";
import Button from "app/components/Button";
import { ReviewSummary } from "./reviewSummary";
import { useApproveReview } from "./useApproveReview";

export default function ApproveConfirmDialog({
  open,
  onOpenChange,
  review,
  onApproved,
}) {
  const approveReview = useApproveReview();
  const isPending = approveReview.isPending;
  const [note, setNote] = useState("");

  const handleApprove = () => {
    approveReview.mutate(
      {
        appReviewId: review.appReviewId,
        role: review.nextReviewerRole,
        notes: note?.trim() || null,
      },
      {
        onSuccess: () => {
          onOpenChange(false);
          setNote("");
          onApproved?.();
        },
      },
    );
  };

  const handleCloseRequest = (nextOpen) => {
    if (!nextOpen && !isPending) {
      onOpenChange(false);
      setNote("");
    }
  };

  return (
    <Modal
      isOpen={open}
      onOpenChange={handleCloseRequest}
    >
      <Modal.Title>Confirm approval</Modal.Title>
      <Modal.Body>
        <p className="mb-3">
          Confirm the application details and add optional notes before
          approving.
        </p>
        <ReviewSummary review={review} />
        <div className="grid gap-2">
          <label
            className="text-sm font-semibold"
            htmlFor="approve-application-note"
          >
            Approval notes
          </label>
          <textarea
            id="approve-application-note"
            className="input w-full"
            rows={4}
            value={note}
            onChange={(event) => setNote(event.target.value)}
            placeholder="Add notes (optional)"
            disabled={isPending}
          />
        </div>
      </Modal.Body>
      <Modal.Buttons>
        <Button onPress={handleApprove} isDisabled={isPending}>
          Approve
        </Button>
        <Button
          variant="secondary"
          onPress={() => handleCloseRequest(false)}
          isDisabled={isPending}
        >
          Cancel
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
