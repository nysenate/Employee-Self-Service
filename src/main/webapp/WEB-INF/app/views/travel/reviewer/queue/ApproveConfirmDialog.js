import React, { useState } from "react";
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
          <AlertDialogTitle>Confirm approval</AlertDialogTitle>
          <AlertDialogDescription>
            Confirm the application details and add optional notes before
            approving.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <ReviewSummary review={review} />
        <div className="grid gap-2">
          <Label htmlFor="approve-application-note">Approval notes</Label>
          <Textarea
            id="approve-application-note"
            value={note}
            onChange={(event) => setNote(event.target.value)}
            placeholder="Add notes (optional)"
            disabled={isPending}
          />
        </div>
        <AlertDialogFooter>
          <AlertDialogAction onClick={handleApprove} disabled={isPending}>
            Approve
          </AlertDialogAction>
          <AlertDialogCancel disabled={isPending}>Cancel</AlertDialogCancel>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
