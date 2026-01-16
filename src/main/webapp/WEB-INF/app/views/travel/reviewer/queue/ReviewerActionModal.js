import React, { useEffect, useState } from "react";
import { useTravelReview } from "app/views/travel/shared/hooks/useTravelReview";
import { Dialog, DialogContent, DialogFooter } from "app/components/ui/dialog";
import TravelAppReviewForm from "app/views/travel/shared/components/TravelAppReviewForm";
import Button from "app/components/Button";
import LoadingIndicator from "app/components/LoadingIndicator";
import ApproveConfirmDialog from "./ApproveConfirmDialog";
import DisapproveConfirmDialog from "./DisapproveConfirmDialog";

export default function ReviewerActionModal({ reviewSummary, setIsOpen }) {
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
      <Dialog open={Boolean(reviewSummary)} onOpenChange={setIsOpen}>
        <DialogContent>
          <LoadingIndicator />
        </DialogContent>
      </Dialog>
    );
  }

  const pdfHref = `${window.location.origin}/api/v1/travel/applications/${review?.travelApplication.id}.pdf`;
  return (
    <Dialog open={Boolean(reviewSummary)} onOpenChange={setIsOpen}>
      <DialogContent discription="desc">
        <TravelAppReviewForm appReview={review} />
        <DialogFooter className="items-center gap-6 px-3 py-1.5 sm:justify-center">
          <div className="grid w-full grid-cols-[1fr_auto]">
            <div className="flex items-center justify-center gap-3">
              <Button
                color="success"
                onClick={() => setApproveDialogOpen(true)}
              >
                Approve Application
              </Button>
              <Button
                color="error"
                onClick={() => setDisapproveDialogOpen(true)}
              >
                Disapprove Application
              </Button>
              <Button color="secondary">Edit Application</Button>
            </div>
            <div className="flex items-center gap-3">
              <a href={pdfHref} target="_blank" rel="noopener noreferrer">
                Print
              </a>
              <Button
                color="secondary"
                className="w-20"
                onClick={() => setIsOpen(false)}
              >
                Close
              </Button>
            </div>
          </div>
        </DialogFooter>
        <ApproveConfirmDialog
          open={approveDialogOpen}
          onOpenChange={setApproveDialogOpen}
          review={review}
          onApproved={() => setIsOpen(false)}
        />
        <DisapproveConfirmDialog
          open={disapproveDialogOpen}
          onOpenChange={setDisapproveDialogOpen}
          review={review}
          onDisapproved={() => setIsOpen(false)}
        />
      </DialogContent>
    </Dialog>
  );
}
