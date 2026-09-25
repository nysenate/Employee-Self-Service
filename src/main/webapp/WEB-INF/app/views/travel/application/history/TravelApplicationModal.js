import React, { useState } from "react";
import Button from "app/components/Button";
import ErrorAlert from "app/components/ErrorAlert";
import LoadingIndicator from "app/components/LoadingIndicator";
import Modal from "app/components/Modal";
import TravelAppForm from "app/views/travel/shared/components/TravelAppForm";
import { useTravelApp } from "app/views/travel/shared/hooks/useTravelApp";
import { parseApplicationId } from "app/views/travel/application/workflow/applicationRoutes";
import CancelApplicationDialog from "./CancelApplicationDialog";
import { useCancelTravelApplication } from "./useCancelTravelApplication";

export default function TravelApplicationModal({ appId, onClose, onResubmit }) {
  const appQuery = useTravelApp(appId, { throwOnError: false });
  const cancellation = useCancelTravelApplication(appId);
  const [confirmCancel, setConfirmCancel] = useState(false);
  const application = appQuery.isSuccess ? appQuery.data?.result : null;
  const hasLoadError = appQuery.isError || (appQuery.isSuccess && !application);
  const canResubmit =
    Boolean(onResubmit) &&
    !appQuery.isFetching &&
    parseApplicationId(application?.id) === appId &&
    application?.status?.isDisapproved === true;
  const canCancel =
    !appQuery.isFetching &&
    parseApplicationId(application?.id) === appId &&
    (application?.status?.isPending === true ||
      application?.status?.isDisapproved === true);
  const pdfHref = `${window.location.origin}/api/v1/travel/applications/${appId}.pdf`;

  return (
    <Modal
      isOpen={Boolean(appId)}
      isDismissable={!cancellation.isPending}
      onOpenChange={(open) => !open && !cancellation.isPending && onClose()}
      ariaLabel="Travel application details"
    >
      <Modal.Body>
        {appQuery.isPending && <LoadingIndicator />}
        {hasLoadError && (
          <ErrorAlert title="We couldn’t load this travel application">
            <p>The application details are currently unavailable.</p>
            <Button className="mt-3" onPress={() => appQuery.refetch()}>
              Retry
            </Button>
          </ErrorAlert>
        )}
        {application && (
          <TravelAppForm app={application} showStatus className="p-5" />
        )}
      </Modal.Body>
      <Modal.Controls>
        <div className="grid w-full grid-cols-[1fr_auto] items-center gap-6 px-3 py-1.5 sm:justify-center">
          <div className="flex flex-wrap items-center justify-center gap-3">
            {canResubmit && (
              <Button
                variant="theme"
                isDisabled={cancellation.isPending || cancellation.isError}
                onPress={() => onResubmit(appId)}
              >
                Edit and Resubmit
              </Button>
            )}
            {canCancel && (
              <Button
                variant="destructive"
                onPress={() => setConfirmCancel(true)}
              >
                Cancel Application
              </Button>
            )}
          </div>
          <div className="flex items-center justify-end gap-3">
            {application && (
              <a href={pdfHref} target="_blank" rel="noopener noreferrer">
                Print
              </a>
            )}
            <Button
              variant="secondary"
              className="w-20"
              isDisabled={cancellation.isPending}
              onPress={onClose}
            >
              Close
            </Button>
          </div>
        </div>
      </Modal.Controls>
      {confirmCancel && (
        <CancelApplicationDialog
          canCancel={canCancel}
          mutation={cancellation}
          onClose={() => setConfirmCancel(false)}
          onConfirm={() => {
            if (canCancel && !cancellation.isPending && !cancellation.isError) {
              cancellation.mutate(undefined, { onSuccess: onClose });
            }
          }}
        />
      )}
    </Modal>
  );
}
