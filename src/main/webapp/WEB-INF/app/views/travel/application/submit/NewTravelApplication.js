import React from "react";
import ErrorAlert from "app/components/ErrorAlert";
import { useNavigate } from "react-router-dom";
import TravelApplicationWorkflow from "../workflow/TravelApplicationWorkflow";
import { SubmissionSuccessModal } from "./components/SubmissionModals";
import { useSaveTravelDraft } from "./hooks/usePurposeMutations";
import { useSubmitTravelApplication } from "./hooks/useSubmitTravelApplication";
import { classifyApplicationSubmissionError } from "../workflow/submissionErrors";

const NEW_APPLICATION_PRESENTATION = Object.freeze({
  finalActionLabel: "Submit application",
  confirmationTitle: "Submit travel application?",
  confirmationBody:
    "Once submitted, this application will be sent for review and can no longer be edited.",
  confirmationActionLabel: "Submit application",
  pendingText: "Submitting your travel application…",
});

export default function NewTravelApplication({ draft }) {
  const navigate = useNavigate();
  const saveDraftMutation = useSaveTravelDraft();
  const submitApplication = useSubmitTravelApplication();

  return (
    <TravelApplicationWorkflow
      initialDraft={draft}
      application={null}
      saveDraft={{
        execute: saveDraftMutation.mutateAsync,
        isPending: saveDraftMutation.isPending,
      }}
      commit={{
        execute: submitApplication.mutateAsync,
        isPending: submitApplication.isPending,
        isDisabled: submitApplication.isCommitBlocked,
        renderError: (error) => <SubmissionError error={error} />,
      }}
      presentation={NEW_APPLICATION_PRESENTATION}
      onCancel={null}
      renderCompletion={() => (
        <SubmissionSuccessModal
          isOpen
          onReturn={() => navigate("/travel")}
          onLogout={() => navigate("/logout")}
        />
      )}
    />
  );
}

function SubmissionError({ error }) {
  const type = classifyApplicationSubmissionError(error);
  if (type === "correction") {
    return (
      <ErrorAlert title="Application was not submitted">
        Review your entered information and try again.
      </ErrorAlert>
    );
  }
  if (type === "access") {
    return (
      <ErrorAlert title="Access is no longer available">
        You no longer have access to submit this travel application. Your
        entered information remains on this page.
      </ErrorAlert>
    );
  }
  return (
    <ErrorAlert title="The submission outcome is unknown">
      <p>
        We could not confirm whether your application was submitted. Submitting
        again could create a duplicate application. Your entered information
        remains on this page.
      </p>
      <p className="mt-3">
        Check Travel History for this application. If you cannot confirm the
        outcome, contact the STS helpline before submitting it again. An
        application missing from history does not confirm that submission
        failed.
      </p>
      <a
        className="mt-3 inline-block underline"
        href="/travel/applications"
        target="_blank"
        rel="noopener noreferrer"
      >
        Open Travel History in a new tab
      </a>
    </ErrorAlert>
  );
}
