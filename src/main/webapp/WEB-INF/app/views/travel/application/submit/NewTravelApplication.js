import React, { useReducer, useRef, useState } from "react";
import Card from "app/components/Card";
import PurposeStep from "./components/PurposeStep";
import UnsavedChangesModal from "./components/UnsavedChangesModal";
import WorkflowActions from "./components/WorkflowActions";
import WorkflowProgress from "./components/WorkflowProgress";
import { useUnsavedChangesGuard } from "./hooks/useUnsavedChangesGuard";
import {
  useSaveTravelDraft,
  useUploadSupportingDocuments,
} from "./hooks/usePurposeMutations";
import { validatePurpose } from "./purposeValidation";
import {
  createWorkflowState,
  hasUnsavedChanges,
  newTravelApplicationReducer,
  WORKFLOW_STEPS,
} from "./newTravelApplicationReducer";

export default function NewTravelApplication({ draft }) {
  const [state, dispatch] = useReducer(
    newTravelApplicationReducer,
    draft,
    createWorkflowState,
  );
  const [purposeErrors, setPurposeErrors] = useState({});
  const [saveMessage, setSaveMessage] = useState(null);
  const [uploadError, setUploadError] = useState(false);
  const errorSummaryRef = useRef(null);
  const saveDraft = useSaveTravelDraft();
  const uploadDocuments = useUploadSupportingDocuments();
  const guard = useUnsavedChangesGuard(hasUnsavedChanges(state));

  function validateCurrentPurpose() {
    const errors = validatePurpose(state.workingDraft);
    setPurposeErrors(errors);
    if (Object.keys(errors).length > 0) {
      requestAnimationFrame(() => errorSummaryRef.current?.focus());
      return false;
    }
    return true;
  }

  function handleNext() {
    setSaveMessage(null);
    if (state.currentStep === 0 && !validateCurrentPurpose()) return;
    dispatch({ type: "COMPLETE_CURRENT_STEP" });
  }

  async function handleSave() {
    setSaveMessage(null);
    if (state.currentStep === 0 && !validateCurrentPurpose()) return;
    try {
      const savedDraft = await saveDraft.mutateAsync(state.workingDraft);
      dispatch({ type: "RESET_BASELINE", draft: savedDraft });
      setSaveMessage({
        type: "success",
        text: "Your travel application was saved as a draft.",
      });
    } catch {
      setSaveMessage({
        type: "error",
        text: "Your travel application could not be saved. Your entered information is still available.",
      });
    }
  }

  async function handleUpload(files, isTooLarge) {
    setUploadError(false);
    if (files.length === 0) return;
    if (isTooLarge) {
      setUploadError(true);
      return;
    }
    try {
      const attachments = await uploadDocuments.mutateAsync(files);
      dispatch({
        type: "APPEND_ATTACHMENTS",
        attachments,
      });
    } catch {
      setUploadError(true);
    }
  }

  const workflowActions = (
    <WorkflowActions
      step={state.currentStep}
      onBack={() => dispatch({ type: "GO_BACK" })}
      onSave={handleSave}
      isSaving={saveDraft.isPending}
      onPrimary={handleNext}
    />
  );

  return (
    <div className="mx-auto max-w-5xl space-y-5">
      <WorkflowProgress
        currentStep={state.currentStep}
        furthestCompletedStep={state.furthestCompletedStep}
        onSelect={(step) => dispatch({ type: "GO_TO_STEP", step })}
      />

      {state.currentStep === 0 ? (
        <PurposeStep
          draft={state.workingDraft}
          errors={purposeErrors}
          errorSummaryRef={errorSummaryRef}
          uploadError={uploadError}
          isUploading={uploadDocuments.isPending}
          onDraftChange={(nextDraft) =>
            dispatch({ type: "UPDATE_DRAFT", draft: nextDraft })
          }
          onUpload={handleUpload}
          actions={workflowActions}
        />
      ) : (
        <Card>
          <Card.Header className="justify-start bg-teal-50">
            <Card.Title>{WORKFLOW_STEPS[state.currentStep]}</Card.Title>
          </Card.Header>
          <Card.Content className="p-5">
            <h2 className="text-lg font-semibold">
              {WORKFLOW_STEPS[state.currentStep]}
            </h2>
            <p className="mt-2 text-gray-600">
              This step will be completed in its assigned implementation slice.
            </p>
          </Card.Content>
          <Card.Footer className="mt-0 justify-end bg-gray-50 px-5 py-4">
            {workflowActions}
          </Card.Footer>
        </Card>
      )}

      {saveMessage && (
        <p
          role={saveMessage.type === "error" ? "alert" : "status"}
          className={
            saveMessage.type === "error"
              ? "font-medium text-red-700"
              : "font-medium text-green-700"
          }
        >
          {saveMessage.text}
        </p>
      )}

      <UnsavedChangesModal guard={guard} />
    </div>
  );
}
