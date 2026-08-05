import React, { useMemo, useState } from "react";
import Modal from "app/components/Modal";
import Button from "app/components/Button";
import { ReviewSummary } from "./reviewSummary";
import { useDisapproveReview } from "./useDisapproveReview";

export default function DisapproveConfirmDialog({
  open,
  onOpenChange,
  review,
  onDisapproved,
}) {
  const disapproveReview = useDisapproveReview();
  const isPending = disapproveReview.isPending;
  const [note, setNote] = useState("");
  const trimmedNote = useMemo(() => note.trim(), [note]);
  const canDisapprove = trimmedNote.length > 0 && !isPending;

  const handleDisapprove = () => {
    if (!review?.appReviewId || !review?.pendingReviewerRole || !trimmedNote) {
      return;
    }

    disapproveReview.mutate(
      {
        appReviewId: review.appReviewId,
        role: review.pendingReviewerRole,
        notes: trimmedNote,
      },
      {
        onSuccess: () => {
          onOpenChange(false);
          setNote("");
          onDisapproved?.();
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
      <Modal.Title>Confirm disapproval</Modal.Title>
      <Modal.Body>
        <p className="mb-3">
          Confirm the application details and add notes before disapproving.
        </p>
        <ReviewSummary review={review} />
        <div className="grid gap-2">
          <label
            className="text-sm font-semibold"
            htmlFor="disapprove-application-note"
          >
            Disapproval notes<span className="text-red-600">*</span>
          </label>
          <textarea
            id="disapprove-application-note"
            className="input w-full"
            rows={4}
            value={note}
            onChange={(event) => setNote(event.target.value)}
            placeholder="Add notes (required)"
            disabled={isPending}
          />
        </div>
      </Modal.Body>
      <Modal.Buttons>
        <Button
          variant="destructive"
          onPress={handleDisapprove}
          isDisabled={!canDisapprove}
        >
          Disapprove
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
