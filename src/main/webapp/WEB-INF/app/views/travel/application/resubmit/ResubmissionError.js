import React from "react";
import Button from "app/components/Button";
import ErrorAlert from "app/components/ErrorAlert";
import { classifyResubmissionError } from "./useResubmitTravelApplication";

export default function ResubmissionError({
  error,
  recovery,
  onCheckStatus,
  isCheckingStatus = false,
  onReturn,
}) {
  const type =
    recovery?.type && recovery.type !== "idle"
      ? recovery.type
      : classifyResubmissionError(error);

  switch (type) {
    case "correction":
      return (
        <ErrorAlert title="The application could not be resubmitted">
          <p>Review your changes and try again.</p>
        </ErrorAlert>
      );
    case "access":
      return (
        <RecoveryAlert
          title="Access is no longer available"
          onReturn={onReturn}
        >
          You no longer have access to resubmit this travel application.
        </RecoveryAlert>
      );
    case "conflict":
      return (
        <RecoveryAlert
          title="The application cannot be resubmitted"
          onReturn={onReturn}
        >
          This application is no longer available for resubmission.
        </RecoveryAlert>
      );
    case "retryable-status":
      return (
        <ErrorAlert title="The application is still disapproved">
          <p>
            Its status has been checked. You can try to resubmit your changes
            again.
          </p>
        </ErrorAlert>
      );
    case "status-changed":
      return (
        <RecoveryAlert
          title="The application status changed"
          onReturn={onReturn}
        >
          The application is no longer disapproved. This status check cannot
          confirm whether your edits were saved.
        </RecoveryAlert>
      );
    case "status-check-failed":
      return (
        <StatusCheckAlert
          title="The application status could not be checked"
          onCheckStatus={onCheckStatus}
          isCheckingStatus={isCheckingStatus}
          onReturn={onReturn}
        >
          Try checking its status again before resubmitting.
        </StatusCheckAlert>
      );
    case "unknown-outcome":
    default:
      return (
        <StatusCheckAlert
          title="The resubmission outcome is unknown"
          onCheckStatus={onCheckStatus}
          isCheckingStatus={isCheckingStatus}
          onReturn={onReturn}
        >
          We could not confirm whether your application was resubmitted. Check
          its current status before trying again.
        </StatusCheckAlert>
      );
  }
}

function RecoveryAlert({ title, children, onReturn }) {
  return (
    <ErrorAlert title={title}>
      <p>{children}</p>
      <RecoveryActions onReturn={onReturn} />
    </ErrorAlert>
  );
}

function StatusCheckAlert({
  title,
  children,
  onCheckStatus,
  isCheckingStatus,
  onReturn,
}) {
  return (
    <ErrorAlert title={title}>
      <p>{children}</p>
      <RecoveryActions
        onCheckStatus={onCheckStatus}
        isCheckingStatus={isCheckingStatus}
        onReturn={onReturn}
      />
    </ErrorAlert>
  );
}

function RecoveryActions({
  onCheckStatus,
  isCheckingStatus = false,
  onReturn,
}) {
  return (
    <div className="mt-3 flex flex-wrap gap-3">
      {onCheckStatus && (
        <Button onPress={onCheckStatus} isPending={isCheckingStatus}>
          Check application status
        </Button>
      )}
      {onReturn && (
        <Button variant="secondary" onPress={onReturn}>
          Return to Travel History
        </Button>
      )}
    </div>
  );
}
