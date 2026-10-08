import React, { useEffect, useReducer, useRef, useState } from "react";
import BusyRegion from "app/components/BusyRegion";
import ErrorAlert from "app/components/ErrorAlert";
import { useNotifySuccess } from "app/components/NotificationProvider";
import PurposeStep from "../submit/components/PurposeStep";
import ExpensesStep from "../submit/components/ExpensesStep";
import RouteStep from "../submit/components/RouteStep";
import CountyPromptModal from "../submit/components/CountyPromptModal";
import ReviewStep from "../submit/components/ReviewStep";
import OutsideConusModal from "../submit/components/OutsideConusModal";
import UnsavedChangesModal from "../submit/components/UnsavedChangesModal";
import LongTripModal from "../submit/components/LongTripModal";
import WorkflowActions from "../submit/components/WorkflowActions";
import WorkflowProgress from "../submit/components/WorkflowProgress";
import CancelEditsModal from "./components/CancelEditsModal";
import { ApplicationSubmissionDialog } from "../submit/components/SubmissionModals";
import { useUnsavedChangesGuard } from "../submit/hooks/useUnsavedChangesGuard";
import { useUploadSupportingDocuments } from "../submit/hooks/usePurposeMutations";
import { validatePurpose } from "../submit/purposeValidation";
import {
  isOutsideConus,
  validateOutboundDestinations,
} from "../submit/routeValidation";
import {
  addReturnLeg,
  addOutboundLeg,
  removeLastReturnLeg,
  removeLastOutboundLeg,
  updateReturnLeg,
  updateOutboundLeg,
} from "../submit/routeModel";
import { useCalculateTravelExpenses } from "../submit/hooks/useExpenseMutations";
import {
  createWorkflowState,
  hasUnsavedChanges,
  workflowReducer,
} from "./workflowReducer";
import { useApplicationSubmission } from "./hooks/useApplicationSubmission";
import { useExpenseEditing } from "./hooks/useExpenseEditing";
import { useRouteAdvancement } from "./hooks/useRouteAdvancement";
import { STANDARD_STEPS } from "./workflowSteps";

import OverridesStep from "../edit/OverridesStep";
import { useExpenseOverrides } from "../edit/useExpenseOverrides";

const DEFAULT_PRESENTATION = Object.freeze({
  finalActionLabel: "Submit application",
  confirmationTitle: "Submit travel application?",
  confirmationBody:
    "Once submitted, this application will be sent for review and can no longer be edited.",
  confirmationActionLabel: "Submit application",
  pendingText: "Submitting your travel application…",
});

/**
 * Shared travel application editor. Operation-specific adapters supply draft
 * persistence, final commit behavior, presentation copy, and completion UI.
 *
 * @param {object} props
 * @param {object} props.initialDraft
 * @param {Array<{id: string, label: string, allowsDraftSave: boolean}>} [props.steps]
 * @param {object|null} [props.application]
 * @param {{execute: function(object): Promise<object>, isPending: boolean}|null} [props.saveDraft]
 * @param {{execute: function(object): Promise<unknown>, isPending: boolean, isDisabled: boolean, renderError: function(*): React.ReactNode}} props.commit
 * @param {{finalActionLabel: string, confirmationTitle: string, confirmationBody: string, confirmationActionLabel: string, pendingText: string}} [props.presentation]
 * @param {function|null} [props.onCancel]
 * @param {function(*): React.ReactNode} props.renderCompletion
 */
export default function TravelApplicationWorkflow({
  initialDraft,
  steps = STANDARD_STEPS,
  application = null,
  saveDraft = null,
  commit,
  presentation = DEFAULT_PRESENTATION,
  onCancel = null,
  renderCompletion,
}) {
  const draft = initialDraft;
  const notifySuccess = useNotifySuccess();
  const [state, dispatch] = useReducer(workflowReducer, draft, (initial) =>
    createWorkflowState(initial, steps),
  );
  const expenseOverrides = useExpenseOverrides(draft);
  const [purposeErrors, setPurposeErrors] = useState({});
  const [showConusWarning, setShowConusWarning] = useState(false);
  const [saveError, setSaveError] = useState(null);
  const [pendingAction, setPendingAction] = useState(null);
  const [uploadError, setUploadError] = useState(false);
  const [showCancelEdits, setShowCancelEdits] = useState(false);
  const errorSummaryRef = useRef(null);
  const saveErrorRef = useRef(null);
  const routeErrorRef = useRef(null);
  const calculateExpenses = useCalculateTravelExpenses();
  const uploadDocuments = useUploadSupportingDocuments();
  const submission = useApplicationSubmission({
    draft: state.workingDraft,
    commit,
  });
  const guard = useUnsavedChangesGuard(
    submission.status !== "success" &&
      (hasUnsavedChanges(state) ||
        expenseOverrides.isDirty(state.workingDraft)),
    submission.status === "submitting",
  );

  const expenseEditing = useExpenseEditing({
    initialDraft,
    state,
    dispatch,
    calculate: calculateExpenses.mutateAsync,
    reportValidationResult,
    onComplete: completeStepAction,
  });

  const routeAdvancement = useRouteAdvancement({
    state,
    dispatch,
    reportValidationResult,
    onCalculated: (calculatedDraft) => {
      dispatch({ type: "APPLY_CALCULATED_DRAFT", draft: calculatedDraft });
      resetExpensesAfterRouteCalculation(calculatedDraft);
    },
    onComplete: completeStepAction,
  });

  function validateCurrentPurpose() {
    const errors = validatePurpose(state.workingDraft);
    setPurposeErrors(errors);
    return reportValidationResult(errors);
  }

  useEffect(() => {
    if (saveError) saveErrorRef.current?.focus();
  }, [saveError]);
  useEffect(() => {
    if (routeAdvancement.calculationError) routeErrorRef.current?.focus();
  }, [routeAdvancement.calculationError]);

  function reportValidationResult(errors) {
    if (Object.keys(errors).length === 0) return true;
    requestAnimationFrame(() => errorSummaryRef.current?.focus());
    return false;
  }

  async function handleNext() {
    setSaveError(null);
    setPendingAction("next");
    switch (state.currentStepId) {
      case "purpose":
        if (!validateCurrentPurpose()) return;
        dispatch({ type: "COMPLETE_CURRENT_STEP" });
        return;
      case "outbound":
        await routeAdvancement.advanceOutbound();
        return;
      case "return":
        await routeAdvancement.advanceReturn("next");
        return;
      case "expenses":
        await expenseEditing.complete("next");
        return;
      case "overrides":
        await completeOverrides();
        return;
      case "review":
        submission.requestConfirmation();
        return;
      default:
        throw new Error(`Unknown workflow step: ${state.currentStepId}`);
    }
  }

  async function handleSave() {
    if (!saveDraft) return;
    setSaveError(null);
    setPendingAction("save");
    switch (state.currentStepId) {
      case "purpose":
        if (!validateCurrentPurpose()) return;
        break;
      case "return":
        await routeAdvancement.advanceReturn("save");
        return;
      case "expenses":
        await expenseEditing.complete("save");
        return;
      case "review":
        break;
      case "outbound":
        return;
      default:
        throw new Error(`Unknown workflow step: ${state.currentStepId}`);
    }
    await persistDraft(state.workingDraft);
  }

  function completeStepAction(action, calculatedDraft) {
    if (action === "save") return persistDraft(calculatedDraft);
    dispatch({ type: "COMPLETE_CURRENT_STEP" });
  }

  async function persistDraft(draftToSave) {
    if (!saveDraft) return;
    try {
      const savedDraft = await saveDraft.execute(draftToSave);
      dispatch({ type: "RESET_BASELINE", draft: savedDraft });
      notifySuccess("Draft saved");
      return savedDraft;
    } catch {
      setSaveError(
        "Your travel application could not be saved. Your entered information is still available.",
      );
    }
  }

  async function completeOverrides() {
    const calculated = await expenseOverrides.complete({
      state,
      calculate: calculateExpenses.mutateAsync,
      reportValidationResult,
    });
    if (!calculated) return;
    dispatch({ type: "APPLY_CALCULATED_EXPENSES", draft: calculated });
    dispatch({ type: "COMPLETE_CURRENT_STEP" });
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

  function discardEdits() {
    if (!onCancel || submission.isLocked || commit.isPending) return;
    setShowCancelEdits(false);
    onCancel();
  }

  const busyMessage = saveDraft?.isPending
    ? "Saving your draft…"
    : routeAdvancement.isPending
      ? routeAdvancement.pendingMessage
      : calculateExpenses.isPending || expenseEditing.isPending
        ? "Updating expense totals…"
        : null;
  const isWorking = Boolean(busyMessage);
  const isNavigationDisabled =
    isWorking || expenseEditing.isLodgingPending || uploadDocuments.isPending;

  const workflowActions = (
    <WorkflowActions
      steps={state.steps}
      stepId={state.currentStepId}
      onBack={() => dispatch({ type: "GO_BACK" })}
      onSave={saveDraft ? handleSave : null}
      onCancel={onCancel ? () => setShowCancelEdits(true) : null}
      finalActionLabel={presentation.finalActionLabel}
      isSaving={isWorking && pendingAction === "save"}
      onPrimary={handleNext}
      isPrimaryPending={
        (isWorking && pendingAction !== "save") ||
        submission.status === "submitting"
      }
      isPrimaryDisabled={state.currentStepId === "review" && commit.isDisabled}
      isCancelDisabled={submission.isLocked || commit.isPending}
      isDisabled={submission.isLocked || isNavigationDisabled}
    />
  );

  return (
    <div className="mx-auto max-w-5xl space-y-5">
      <WorkflowProgress
        steps={state.steps}
        currentStepId={state.currentStepId}
        completedStepIds={state.completedStepIds}
        onSelect={(stepId) => dispatch({ type: "GO_TO_STEP", stepId })}
        isDisabled={submission.isLocked || isNavigationDisabled}
      />

      {saveError && (
        <ErrorAlert
          ref={saveErrorRef}
          tabIndex={-1}
          title="Draft was not saved"
        >
          {saveError}
        </ErrorAlert>
      )}
      {submission.status === "idle" &&
        submission.error &&
        commit.renderError(submission.error)}
      <BusyRegion message={busyMessage}>{renderCurrentStep()}</BusyRegion>

      <CountyPromptModal
        pending={routeAdvancement.pendingCounty}
        onSubmit={routeAdvancement.submitCounty}
        onCancel={routeAdvancement.cancelCounty}
      />

      <ApplicationSubmissionDialog
        isOpen={["confirming", "submitting", "failed"].includes(
          submission.status,
        )}
        isPending={submission.status === "submitting"}
        pendingText={presentation.pendingText}
        error={
          submission.status === "failed"
            ? commit.renderError(submission.error)
            : null
        }
        onCancel={submission.cancelConfirmation}
        onConfirm={submission.confirm}
        title={presentation.confirmationTitle}
        body={presentation.confirmationBody}
        actionLabel={presentation.confirmationActionLabel}
      />
      {onCancel && (
        <CancelEditsModal
          isOpen={showCancelEdits}
          onKeepEditing={() => setShowCancelEdits(false)}
          onDiscard={discardEdits}
          isDiscardDisabled={submission.isLocked || commit.isPending}
        />
      )}
      {submission.status === "success" && renderCompletion(submission.result)}

      <UnsavedChangesModal guard={guard} />
    </div>
  );

  function renderCurrentStep() {
    switch (state.currentStepId) {
      case "purpose":
        return (
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
        );
      case "outbound":
        return (
          <>
            <RouteStep
              title="Outbound"
              description="Enter the route from your origin location through every outbound destination."
              legs={state.dirtyRoute.outboundLegs}
              errors={{
                ...routeAdvancement.errors,
                ...validateOutboundDestinations(state.dirtyRoute),
              }}
              errorSummaryRef={errorSummaryRef}
              segmentIdPrefix="outbound"
              addSegmentLabel="Add outbound segment"
              onAddSegment={() =>
                routeAdvancement.updateRoute(addOutboundLeg(state.dirtyRoute))
              }
              onRemoveLastSegment={() =>
                routeAdvancement.updateRoute(
                  removeLastOutboundLeg(state.dirtyRoute),
                )
              }
              onUpdateSegment={(index, changes) =>
                routeAdvancement.updateRoute(
                  updateOutboundLeg(state.dirtyRoute, index, changes),
                )
              }
              onDestinationSelect={(address) => {
                if (isOutsideConus(address)) setShowConusWarning(true);
              }}
              firstLegQualifier={{
                label: "Departing before 7:00 AM",
                checked: Boolean(
                  state.dirtyRoute.firstLegQualifiesForBreakfast,
                ),
                onChange: (checked) =>
                  routeAdvancement.updateRoute({
                    ...state.dirtyRoute,
                    firstLegQualifiesForBreakfast: checked,
                  }),
              }}
              actions={workflowActions}
              isDisabled={routeAdvancement.isPending}
            />
            <OutsideConusModal
              isOpen={showConusWarning}
              onClose={() => setShowConusWarning(false)}
            />
          </>
        );
      case "return":
        return (
          <>
            <RouteStep
              title="Return"
              description="Enter the route from your final destination back to your original departure point."
              legs={state.dirtyRoute.returnLegs}
              travelStartDate={state.dirtyRoute.outboundLegs[0]?.travelDate}
              precedingTravelDate={
                state.dirtyRoute.outboundLegs.at(-1)?.travelDate
              }
              errors={routeAdvancement.errors}
              errorSummaryRef={errorSummaryRef}
              segmentIdPrefix="return"
              addSegmentLabel="Add return segment"
              onAddSegment={() =>
                routeAdvancement.updateRoute(addReturnLeg(state.dirtyRoute))
              }
              onRemoveLastSegment={() =>
                routeAdvancement.updateRoute(
                  removeLastReturnLeg(state.dirtyRoute),
                )
              }
              onUpdateSegment={(index, changes) =>
                routeAdvancement.updateRoute(
                  updateReturnLeg(state.dirtyRoute, index, changes),
                )
              }
              onDestinationSelect={() => {}}
              lastLegQualifier={{
                label: "Arriving after 7:00 PM",
                checked: Boolean(state.dirtyRoute.lastLegQualifiesForDinner),
                onChange: (checked) =>
                  routeAdvancement.updateRoute({
                    ...state.dirtyRoute,
                    lastLegQualifiesForDinner: checked,
                  }),
              }}
              actions={workflowActions}
              isDisabled={routeAdvancement.isPending}
            />
            {routeAdvancement.calculationError && (
              <ErrorAlert
                ref={routeErrorRef}
                tabIndex={-1}
                title="Route could not be calculated"
                className="mt-4"
              >
                {routeAdvancement.calculationError}
              </ErrorAlert>
            )}
            <LongTripModal
              isOpen={routeAdvancement.showLongTripWarning}
              onReview={routeAdvancement.reviewLongTrip}
              onConfirm={routeAdvancement.confirmLongTrip}
            />
          </>
        );
      case "expenses":
        return (
          <ExpensesStep
            draft={state.workingDraft}
            expenses={expenseEditing.expenses}
            errors={expenseEditing.errors}
            errorSummaryRef={errorSummaryRef}
            calculationError={expenseEditing.calculationError}
            lodgingErrors={expenseEditing.lodgingErrors}
            isDisabled={calculateExpenses.isPending || expenseEditing.isPending}
            pendingLodgingRows={expenseEditing.pendingLodgingRows}
            onExpensesChange={expenseEditing.update}
            onDraftChange={(next) =>
              dispatch({ type: "UPDATE_DRAFT", draft: next })
            }
            onLodgingSelect={expenseEditing.selectLodging}
            actions={workflowActions}
          />
        );
      case "overrides":
        return (
          <OverridesStep
            draft={expenseOverrides.preview(state.workingDraft)}
            errors={expenseOverrides.errors}
            errorSummaryRef={errorSummaryRef}
            calculationError={expenseOverrides.calculationError}
            isDisabled={
              calculateExpenses.isPending ||
              expenseEditing.isPending ||
              submission.isLocked
            }
            onChange={(next) => {
              expenseOverrides.update(next);
              dispatch({ type: "INVALIDATE_CURRENT_STEP" });
            }}
            actions={workflowActions}
          />
        );
      case "review":
        return (
          <ReviewStep
            draft={state.workingDraft}
            application={application}
            actions={workflowActions}
          />
        );
      default:
        throw new Error(`Unknown workflow step: ${state.currentStepId}`);
    }
  }

  function resetExpensesAfterRouteCalculation(calculatedDraft) {
    expenseOverrides.resetFromDraft(calculatedDraft);
    expenseEditing.resetFromDraft(calculatedDraft);
  }
}
