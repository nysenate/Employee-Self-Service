import React, { useMemo, useState } from "react";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "app/components/ui/alert-dialog";
import { Label } from "app/components/ui/label";
import { Textarea } from "app/components/ui/textarea";
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
    if (!review?.appReviewId || !review?.nextReviewerRole || !trimmedNote) {
      return;
    }

    disapproveReview.mutate(
      {
        appReviewId: review.appReviewId,
        role: review.nextReviewerRole,
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

  return (
    <AlertDialog
      open={open}
      onOpenChange={(nextOpen) => {
        onOpenChange(nextOpen);
        if (!nextOpen && !isPending) {
          setNote("");
        }
      }}
    >
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Confirm disapproval</AlertDialogTitle>
          <AlertDialogDescription>
            Confirm the application details and add notes before disapproving.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <ReviewSummary review={review} />
        <div className="grid gap-2">
          <Label htmlFor="disapprove-application-note">
            Disapproval notes<span className="text-red-600">*</span>
          </Label>
          <Textarea
            id="disapprove-application-note"
            value={note}
            onChange={(event) => setNote(event.target.value)}
            placeholder="Add notes (required)"
            disabled={isPending}
          />
        </div>
        <AlertDialogFooter>
          <AlertDialogAction
            variant="destructive"
            onClick={handleDisapprove}
            disabled={!canDisapprove}
          >
            Disapprove
          </AlertDialogAction>
          <AlertDialogCancel disabled={isPending}>Cancel</AlertDialogCancel>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
