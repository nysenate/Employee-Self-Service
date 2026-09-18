import React from "react";
import { useNavigate } from "react-router-dom";
import TravelApplicationWorkflow from "../workflow/TravelApplicationWorkflow";
import { SubmissionSuccessModal } from "./components/SubmissionModals";
import { useSaveTravelDraft } from "./hooks/usePurposeMutations";
import { useSubmitTravelApplication } from "./hooks/useSubmitTravelApplication";

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
        isDisabled: false,
        renderError: () => (
          <p role="alert" className="font-medium">
            Your travel application could not be submitted. Your application
            and any saved draft remain available; you can edit it or try again.
          </p>
        ),
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
