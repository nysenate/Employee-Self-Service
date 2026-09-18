import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Button from "app/components/Button";
import ErrorAlert from "app/components/ErrorAlert";
import Hero from "app/components/Hero";
import LoadingIndicator from "app/components/LoadingIndicator";
import TravelApplicationWorkflow from "app/views/travel/application/workflow/TravelApplicationWorkflow";
import { normalizeHistoryReturnTo } from "app/views/travel/application/workflow/applicationRoutes";
import { SubmissionSuccessModal } from "app/views/travel/application/submit/components/SubmissionModals";
import { useApplicationEditSession } from "app/views/travel/application/workflow/hooks/useApplicationEditSession";
import ResubmissionError from "./ResubmissionError";
import { useResubmitTravelApplication } from "./useResubmitTravelApplication";

const RESUBMISSION_PRESENTATION = Object.freeze({
  finalActionLabel: "Save and Resubmit",
  confirmationTitle: "Resubmit travel application?",
  confirmationBody:
    "Your changes will be saved and this application will be sent through review again.",
  confirmationActionLabel: "Save and Resubmit",
  pendingText: "Resubmitting your travel application…",
});

export default function ResubmitTravelApplication({ appId }) {
  const location = useLocation();
  const navigate = useNavigate();
  const returnTo = normalizeHistoryReturnTo(location.state?.returnTo);
  const session = useApplicationEditSession({ appId, operation: "resubmit" });
  const resubmission = useResubmitTravelApplication(appId);
  const returnToHistory = () => navigate(returnTo, { replace: true });

  let content;
  if (appId === null) {
    content = (
      <UnavailableState onReturn={returnToHistory}>
        The application address is invalid.
      </UnavailableState>
    );
  } else if (session.isPending) {
    content = <LoadingState />;
  } else if (session.isError) {
    content = (
      <InitializationError
        error={session.error}
        retry={session.refetch}
        onReturn={returnToHistory}
      />
    );
  } else if (!session.data?.application || !session.data?.draft) {
    content = <UnavailableState onReturn={returnToHistory} />;
  } else if (!session.data.application.status?.isDisapproved) {
    content = <NotDisapprovedState onReturn={returnToHistory} />;
  } else {
    const { application, draft } = session.data;
    content = (
      <TravelApplicationWorkflow
        key={appId}
        initialDraft={draft}
        application={application}
        saveDraft={null}
        commit={{
          execute: resubmission.mutateAsync,
          isPending: resubmission.isPending,
          isDisabled: resubmission.isCommitBlocked,
          renderError: (error) => (
            <ResubmissionError
              error={error}
              recovery={resubmission.recovery}
              onCheckStatus={resubmission.checkApplicationStatus}
              isCheckingStatus={resubmission.isStatusCheckPending}
              onReturn={returnToHistory}
            />
          ),
        }}
        presentation={RESUBMISSION_PRESENTATION}
        onCancel={returnToHistory}
        renderCompletion={() => (
          <SubmissionSuccessModal
            isOpen
            title="Application resubmitted"
            body="Your travel application was resubmitted successfully. What would you like to do next?"
            onReturn={() => navigate("/travel", { replace: true })}
            onLogout={() => navigate("/logout", { replace: true })}
          />
        )}
      />
    );
  }

  return (
    <div className="space-y-5">
      <Hero>Edit and Resubmit Travel Application</Hero>
      {content}
    </div>
  );
}

function LoadingState() {
  return (
    <div
      className="flex min-h-48 flex-col items-center justify-center gap-3"
      role="status"
    >
      <LoadingIndicator />
      <span>Loading your travel application…</span>
    </div>
  );
}

function InitializationError({ error, retry, onReturn }) {
  const status = error?.response?.status;
  const missingDepartment = error?.data?.errorCode === "MISSING_DEPARTMENT";

  if (missingDepartment) {
    return (
      <ErrorAlert title="Department information is missing">
        <p>
          ESS could not determine the traveler’s department. Contact your
          personnel office before resubmitting this application.
        </p>
        <ReturnButton onReturn={onReturn} />
      </ErrorAlert>
    );
  }

  if (status === 401 || status === 403) {
    return (
      <ErrorAlert title="Access is unavailable">
        <p>You do not have access to edit and resubmit this application.</p>
        <ReturnButton onReturn={onReturn} />
      </ErrorAlert>
    );
  }

  if (status === 404) {
    return <UnavailableState onReturn={onReturn} />;
  }

  return (
    <ErrorAlert title="We couldn’t load this travel application">
      <p>
        Your information has not been changed. Please try again or return to
        Travel History.
      </p>
      <div className="mt-4 flex flex-wrap gap-3">
        <Button onPress={retry}>Try again</Button>
        <Button variant="secondary" onPress={onReturn}>
          Return to Travel History
        </Button>
      </div>
    </ErrorAlert>
  );
}

function UnavailableState({ onReturn, children }) {
  return (
    <ErrorAlert title="Travel application unavailable">
      <p>
        {children ?? "The requested travel application could not be found."}
      </p>
      <ReturnButton onReturn={onReturn} />
    </ErrorAlert>
  );
}

function NotDisapprovedState({ onReturn }) {
  return (
    <ErrorAlert title="This application cannot be resubmitted">
      <p>
        Only a disapproved travel application can be edited and resubmitted.
      </p>
      <ReturnButton onReturn={onReturn} />
    </ErrorAlert>
  );
}

function ReturnButton({ onReturn }) {
  return (
    <Button className="mt-4" variant="secondary" onPress={onReturn}>
      Return to Travel History
    </Button>
  );
}
