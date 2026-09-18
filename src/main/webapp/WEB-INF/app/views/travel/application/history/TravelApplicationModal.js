import React from "react";
import Button from "app/components/Button";
import ErrorAlert from "app/components/ErrorAlert";
import LoadingIndicator from "app/components/LoadingIndicator";
import Modal from "app/components/Modal";
import TravelAppForm from "app/views/travel/shared/components/TravelAppForm";
import { useTravelApp } from "app/views/travel/shared/hooks/useTravelApp";
import { parseApplicationId } from "app/views/travel/application/workflow/applicationRoutes";

export default function TravelApplicationModal({
  appId,
  onClose,
  onResubmit,
}) {
  const appQuery = useTravelApp(appId, { throwOnError: false });
  const application = appQuery.isSuccess ? appQuery.data?.result : null;
  const hasLoadError = appQuery.isError || (appQuery.isSuccess && !application);
  const canResubmit =
    Boolean(onResubmit) &&
    !appQuery.isFetching &&
    parseApplicationId(application?.id) === appId &&
    application?.status?.isDisapproved === true;
  const pdfHref = `${window.location.origin}/api/v1/travel/applications/${appId}.pdf`;

  return (
    <Modal
      isOpen={Boolean(appId)}
      onOpenChange={(open) => !open && onClose()}
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
        <div className="flex items-center gap-6 px-3 py-1.5">
          {canResubmit && (
            <Button variant="theme" onPress={() => onResubmit(appId)}>Edit and Resubmit</Button>
          )}
          {application && (
            <a href={pdfHref} target="_blank" rel="noopener noreferrer">
              Print
            </a>
          )}
          <Button variant="secondary" className="w-20" onPress={onClose}>
            Close
          </Button>
        </div>
      </Modal.Controls>
    </Modal>
  );
}
