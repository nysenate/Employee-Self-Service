import React, { useReducer, useRef, useState } from "react";
import PurposeStep from "../submit/components/PurposeStep";
import ExpensesStep from "../submit/components/ExpensesStep";
import RouteStep from "../submit/components/RouteStep";
import ReviewStep from "../submit/components/ReviewStep";
import OutsideConusModal from "../submit/components/OutsideConusModal";
import UnsavedChangesModal from "../submit/components/UnsavedChangesModal";
import LongTripModal from "../submit/components/LongTripModal";
import WorkflowActions from "../submit/components/WorkflowActions";
import WorkflowProgress from "../submit/components/WorkflowProgress";
import CancelEditsModal from "./components/CancelEditsModal";
import { SubmissionConfirmationModal } from "../submit/components/SubmissionModals";
import { useUnsavedChangesGuard } from "../submit/hooks/useUnsavedChangesGuard";
import { useUploadSupportingDocuments } from "../submit/hooks/usePurposeMutations";
import { validatePurpose } from "../submit/purposeValidation";
import {
  isLongTrip,
  normalizeCompleteRoute,
  normalizeOutboundRoute,
  isOutsideConus,
  validateOutboundDestinations,
  validateOutboundRoute,
  validateReturnRoute,
} from "../submit/routeValidation";
import {
  addReturnLeg,
  addOutboundLeg,
  findMissingRouteCounty,
  findMissingOutboundCounty,
  removeLastReturnLeg,
  removeLastOutboundLeg,
  setRouteAddressCounty,
  toRouteDto,
  setOutboundAddressCounty,
  updateReturnLeg,
  updateOutboundLeg,
} from "../submit/routeModel";
import { useCalculateTravelRoute } from "../submit/hooks/useRouteMutations";
import {
  useCalculateLodgingRate,
  useCalculateTravelExpenses,
} from "../submit/hooks/useExpenseMutations";
import {
  applyEditableExpenses,
  createEditableExpenses,
  validateExpenses,
} from "../submit/expenseModel";
import { useAddressCounty } from "app/views/travel/shared/hooks/useAddressCounty";
import {
  createWorkflowState,
  hasUnsavedChanges,
  needsRouteRecalculation,
  needsExpenseRecalculation,
  workflowReducer,
} from "./workflowReducer";
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
  const [state, dispatch] = useReducer(workflowReducer, draft, (initial) =>
    createWorkflowState(initial, steps),
  );
  const expenseOverrides = useExpenseOverrides(draft);
  const [purposeErrors, setPurposeErrors] = useState({});
  const [routeErrors, setRouteErrors] = useState({});
  const [pendingCounty, setPendingCounty] = useState(null);
  const [isAdvancingRoute, setIsAdvancingRoute] = useState(false);
  const [showConusWarning, setShowConusWarning] = useState(false);
  const [showLongTripWarning, setShowLongTripWarning] = useState(false);
  const [pendingReturnAction, setPendingReturnAction] = useState(null);
  const [routeCalculationError, setRouteCalculationError] = useState(null);
  const [saveMessage, setSaveMessage] = useState(null);
  const [uploadError, setUploadError] = useState(false);
  const [expenses, setExpenses] = useState(() => createEditableExpenses(draft));
  const [expenseErrors, setExpenseErrors] = useState({});
  const [lodgingErrors, setLodgingErrors] = useState({});
  const [pendingLodgingRows, setPendingLodgingRows] = useState({});
  const [expenseCalculationError, setExpenseCalculationError] = useState(null);
  const [submissionState, setSubmissionState] = useState("idle");
  const [submissionError, setSubmissionError] = useState(null);
  const [submissionResult, setSubmissionResult] = useState(null);
  const [showCancelEdits, setShowCancelEdits] = useState(false);
  const errorSummaryRef = useRef(null);
  const routeAdvancePendingRef = useRef(false);
  const calculateRoute = useCalculateTravelRoute();
  const calculateExpenses = useCalculateTravelExpenses();
  const calculateLodging = useCalculateLodgingRate();
  const uploadDocuments = useUploadSupportingDocuments();
  const addressCounty = useAddressCounty();
  const submissionLocked =
    submissionState === "submitting" || submissionState === "success";
  const guard = useUnsavedChangesGuard(
    submissionState !== "success" &&
      (hasUnsavedChanges(state) || expenseOverrides.isDirty(state.workingDraft)),
    submissionState === "submitting",
  );
  const routeNeedsRecalculation = needsRouteRecalculation(state);
  const expenseActionPendingRef = useRef(false);
  const submissionPendingRef = useRef(false);
  const lodgingRequestRef = useRef({});
  const isLodgingPending = Object.keys(pendingLodgingRows).length > 0;

  function validateCurrentPurpose() {
    const errors = validatePurpose(state.workingDraft);
    setPurposeErrors(errors);
    return reportValidationResult(errors);
  }

  function reportValidationResult(errors) {
    if (Object.keys(errors).length === 0) return true;
    requestAnimationFrame(() => errorSummaryRef.current?.focus());
    return false;
  }

  async function handleNext() {
    setSaveMessage(null);
    switch (state.currentStepId) {
      case "purpose":
        if (!validateCurrentPurpose()) return;
        dispatch({ type: "COMPLETE_CURRENT_STEP" });
        return;
      case "outbound": {
        const errors = validateOutboundRoute(state.dirtyRoute);
        setRouteErrors(errors);
        if (!reportValidationResult(errors)) return;
        await continueOutbound(normalizeOutboundRoute(state.dirtyRoute));
        return;
      }
      case "return":
        await prepareReturnAction("next");
        return;
      case "expenses":
        await completeExpenseAction("next");
        return;
      case "overrides":
        await completeOverrides();
        return;
      case "review":
        setSubmissionError(null);
        setSubmissionState("confirming");
        return;
      default:
        throw new Error(`Unknown workflow step: ${state.currentStepId}`);
    }
  }

  async function continueOutbound(route) {
    if (routeAdvancePendingRef.current) return;
    routeAdvancePendingRef.current = true;
    setIsAdvancingRoute(true);
    try {
      await advanceOutbound(route);
    } finally {
      routeAdvancePendingRef.current = false;
      setIsAdvancingRoute(false);
    }
  }

  async function advanceOutbound(initialRoute) {
    let route = initialRoute;
    let missingCounty = findMissingOutboundCounty(route);
    while (missingCounty) {
      const { index, direction, addressField } = missingCounty;
      const county = await lookupCounty(addressField.address);
      if (!county) {
        dispatch({ type: "UPDATE_DIRTY_ROUTE", route });
        setPendingCounty({
          index,
          direction,
          addressText: addressField.addressText,
        });
        return;
      }
      route = setOutboundAddressCounty(route, index, direction, county);
      missingCounty = findMissingOutboundCounty(route);
    }
    dispatch({ type: "UPDATE_DIRTY_ROUTE", route });
    dispatch({ type: "COMPLETE_CURRENT_STEP" });
  }

  async function lookupCounty(address) {
    try {
      return await addressCounty.mutateAsync(address);
    } catch {
      return "";
    }
  }

  async function submitCounty(county) {
    if (pendingCounty.routePart) {
      const route = setRouteAddressCounty(
        state.dirtyRoute,
        pendingCounty,
        county,
      );
      const action = pendingCounty.returnAction;
      dispatch({ type: "UPDATE_DIRTY_ROUTE", route });
      setPendingCounty(null);
      const completeRoute = await resolveReturnCounties(route, action);
      if (!completeRoute) return;
      dispatch({ type: "UPDATE_DIRTY_ROUTE", route: completeRoute });
      if (routeNeedsRecalculation && isLongTrip(completeRoute)) {
        setPendingReturnAction({ action, route: completeRoute });
        setShowLongTripWarning(true);
      } else {
        await completeReturnAction(action, completeRoute);
      }
      return;
    }
    const route = setOutboundAddressCounty(
      state.dirtyRoute,
      pendingCounty.index,
      pendingCounty.direction,
      county,
    );
    dispatch({ type: "UPDATE_DIRTY_ROUTE", route });
    setPendingCounty(null);
    await continueOutbound(route);
  }

  async function handleSave() {
    if (!saveDraft) return;
    setSaveMessage(null);
    switch (state.currentStepId) {
      case "purpose":
        if (!validateCurrentPurpose()) return;
        break;
      case "return":
        await prepareReturnAction("save");
        return;
      case "expenses":
        await completeExpenseAction("save");
        return;
      case "review":
        break;
      case "outbound":
        return;
      default:
        throw new Error(`Unknown workflow step: ${state.currentStepId}`);
    }
    try {
      const savedDraft = await saveDraft.execute(state.workingDraft);
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

  async function prepareReturnAction(action) {
    if (routeAdvancePendingRef.current) return;
    setRouteCalculationError(null);
    const errors = validateReturnRoute(state.dirtyRoute);
    setRouteErrors(errors);
    if (!reportValidationResult(errors)) return;
    const route = normalizeCompleteRoute(state.dirtyRoute);
    const completeRoute = await resolveReturnCounties(route, action);
    if (!completeRoute) return;
    dispatch({ type: "UPDATE_DIRTY_ROUTE", route: completeRoute });
    if (routeNeedsRecalculation && isLongTrip(completeRoute)) {
      setPendingReturnAction({ action, route: completeRoute });
      setShowLongTripWarning(true);
      return;
    }
    await completeReturnAction(action, completeRoute);
  }

  async function resolveReturnCounties(initialRoute, action) {
    let route = initialRoute;
    let missingCounty = findMissingRouteCounty(route);
    while (missingCounty) {
      const county = await lookupCounty(missingCounty.addressField.address);
      if (!county) {
        dispatch({ type: "UPDATE_DIRTY_ROUTE", route });
        setPendingCounty({
          ...missingCounty,
          returnAction: action,
          addressText: missingCounty.addressField.addressText,
        });
        return null;
      }
      route = setRouteAddressCounty(route, missingCounty, county);
      missingCounty = findMissingRouteCounty(route);
    }
    return route;
  }

  async function completeReturnAction(action, route) {
    if (action === "save" && !saveDraft) return;
    if (routeAdvancePendingRef.current) return;
    routeAdvancePendingRef.current = true;
    setIsAdvancingRoute(true);
    setRouteCalculationError(null);
    let phase = "calculate";
    try {
      let draftToUse = state.workingDraft;
      if (routeNeedsRecalculation) {
        const draftWithRoute = {
          ...state.workingDraft,
          amendment: {
            ...state.workingDraft.amendment,
            route: toRouteDto(route),
          },
        };
        draftToUse = await calculateRoute.mutateAsync(draftWithRoute);
        dispatch({ type: "APPLY_CALCULATED_DRAFT", draft: draftToUse });
        resetExpensesAfterRouteCalculation(draftToUse);
      }
      if (action === "save") {
        phase = "save";
        const savedDraft = await saveDraft.execute(draftToUse);
        dispatch({ type: "RESET_BASELINE", draft: savedDraft });
        setSaveMessage({
          type: "success",
          text: "Your travel application was saved as a draft.",
        });
      } else {
        dispatch({ type: "COMPLETE_CURRENT_STEP" });
      }
    } catch (error) {
      if (phase === "save") {
        setSaveMessage({
          type: "error",
          text: "Your travel application could not be saved. Your entered information is still available.",
        });
      } else {
        setRouteCalculationError(routeErrorMessage(error));
      }
    } finally {
      routeAdvancePendingRef.current = false;
      setIsAdvancingRoute(false);
    }
  }

  async function completeExpenseAction(action) {
    if (action === "save" && !saveDraft) return;
    if (expenseActionPendingRef.current) return;
    setSaveMessage(null);
    setExpenseCalculationError(null);
    const errors = validateExpenses(expenses);
    setExpenseErrors(errors);
    if (!reportValidationResult(errors) || Object.keys(lodgingErrors).length)
      return;
    expenseActionPendingRef.current = true;
    let phase = "calculate";
    try {
      let draftToUse = applyEditableExpenses(state.workingDraft, expenses);
      if (needsExpenseRecalculation({ ...state, workingDraft: draftToUse })) {
        draftToUse = await calculateExpenses.mutateAsync(draftToUse);
        dispatch({ type: "APPLY_CALCULATED_EXPENSES", draft: draftToUse });
        setExpenses(createEditableExpenses(draftToUse));
      }
      if (action === "save") {
        phase = "save";
        const savedDraft = await saveDraft.execute(draftToUse);
        dispatch({ type: "RESET_BASELINE", draft: savedDraft });
        setExpenses(createEditableExpenses(savedDraft));
        setSaveMessage({
          type: "success",
          text: "Your travel application was saved as a draft.",
        });
      } else {
        dispatch({ type: "COMPLETE_CURRENT_STEP" });
      }
    } catch {
      const text =
        phase === "save"
          ? "Your travel application could not be saved. Your entered information is still available."
          : "Your expense totals could not be calculated. Your entered information is still available; please try again.";
      if (phase === "save") setSaveMessage({ type: "error", text });
      else setExpenseCalculationError(text);
    } finally {
      expenseActionPendingRef.current = false;
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

  async function handleLodgingSelect(row, index, address) {
    const requestId = (lodgingRequestRef.current[index] ?? 0) + 1;
    lodgingRequestRef.current[index] = requestId;
    if (!address?.zip5) {
      setLodgingErrors((current) => ({
        ...current,
        [index]: "Select a recognized hotel address containing a ZIP code.",
      }));
      return;
    }
    setLodgingErrors((current) => {
      const next = { ...current };
      delete next[index];
      return next;
    });
    setPendingLodgingRows((current) => ({ ...current, [index]: true }));
    dispatch({
      type: "UPDATE_EXPENSE_ROW",
      group: "lodgingPerDiems",
      index,
      changes: { address },
    });
    try {
      const result = await calculateLodging.mutateAsync({
        date: row.date,
        address,
      });
      if (requestId !== lodgingRequestRef.current[index]) return;
      dispatch({
        type: "APPLY_LODGING_CALCULATION",
        index,
        calculation: result,
      });
    } catch {
      if (requestId !== lodgingRequestRef.current[index]) return;
      setLodgingErrors((current) => ({
        ...current,
        [index]:
          "The lodging rate could not be calculated. Select another address or try again.",
      }));
    } finally {
      if (requestId === lodgingRequestRef.current[index]) {
        setPendingLodgingRows((current) => {
          const next = { ...current };
          delete next[index];
          return next;
        });
      }
    }
  }

  async function confirmSubmission() {
    if (
      submissionPendingRef.current ||
      commit.isPending ||
      commit.isDisabled ||
      submissionState !== "confirming"
    )
      return;
    submissionPendingRef.current = true;
    setSubmissionState("submitting");
    setSubmissionError(null);
    try {
      const result = await commit.execute(state.workingDraft);
      setSubmissionState("success");
      setSubmissionResult(result);
    } catch (error) {
      submissionPendingRef.current = false;
      setSubmissionState("idle");
      setSubmissionError(error);
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

  function discardEdits() {
    if (!onCancel || submissionLocked || commit.isPending) return;
    setShowCancelEdits(false);
    onCancel();
  }

  const workflowActions = (
    <WorkflowActions
      steps={state.steps}
      stepId={state.currentStepId}
      onBack={() => dispatch({ type: "GO_BACK" })}
      onSave={saveDraft ? handleSave : null}
      onCancel={onCancel ? () => setShowCancelEdits(true) : null}
      finalActionLabel={presentation.finalActionLabel}
      isSaving={
        Boolean(saveDraft?.isPending) ||
        calculateExpenses.isPending ||
        isLodgingPending
      }
      onPrimary={handleNext}
      isPrimaryPending={
        isAdvancingRoute ||
        calculateExpenses.isPending ||
        isLodgingPending ||
        submissionState === "submitting"
      }
      isPrimaryDisabled={state.currentStepId === "review" && commit.isDisabled}
      isCancelDisabled={submissionLocked || commit.isPending}
      isDisabled={submissionLocked}
    />
  );

  return (
    <div className="mx-auto max-w-5xl space-y-5">
      <WorkflowProgress
        steps={state.steps}
        currentStepId={state.currentStepId}
        completedStepIds={state.completedStepIds}
        onSelect={(stepId) => dispatch({ type: "GO_TO_STEP", stepId })}
        isDisabled={
          submissionLocked ||
          isAdvancingRoute ||
          calculateExpenses.isPending ||
          isLodgingPending
        }
      />

      {renderCurrentStep()}

      <SaveMessage message={saveMessage} />

      {submissionState === "submitting" && (
        <p role="status" className="font-medium">
          {presentation.pendingText}
        </p>
      )}
      {submissionError && commit.renderError(submissionError)}

      <SubmissionConfirmationModal
        isOpen={submissionState === "confirming"}
        onCancel={() => setSubmissionState("idle")}
        onConfirm={confirmSubmission}
        title={presentation.confirmationTitle}
        body={presentation.confirmationBody}
        actionLabel={presentation.confirmationActionLabel}
      />
      {onCancel && (
        <CancelEditsModal
          isOpen={showCancelEdits}
          onKeepEditing={() => setShowCancelEdits(false)}
          onDiscard={discardEdits}
          isDiscardDisabled={submissionLocked || commit.isPending}
        />
      )}
      {submissionState === "success" && renderCompletion(submissionResult)}

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
                ...routeErrors,
                ...validateOutboundDestinations(state.dirtyRoute),
              }}
              errorSummaryRef={errorSummaryRef}
              segmentIdPrefix="outbound"
              addSegmentLabel="Add outbound segment"
              onAddSegment={() =>
                updateDirtyRoute(addOutboundLeg(state.dirtyRoute))
              }
              onRemoveLastSegment={() =>
                updateDirtyRoute(removeLastOutboundLeg(state.dirtyRoute))
              }
              onUpdateSegment={(index, changes) =>
                updateDirtyRoute(
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
                  updateDirtyRoute({
                    ...state.dirtyRoute,
                    firstLegQualifiesForBreakfast: checked,
                  }),
              }}
              pendingCounty={pendingCounty}
              onCountySubmit={submitCounty}
              onCountyCancel={() => setPendingCounty(null)}
              actions={workflowActions}
              isDisabled={isAdvancingRoute}
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
              precedingTravelDate={state.dirtyRoute.outboundLegs.at(-1)?.travelDate}
              errors={routeErrors}
              errorSummaryRef={errorSummaryRef}
              segmentIdPrefix="return"
              addSegmentLabel="Add return segment"
              onAddSegment={() =>
                updateDirtyRoute(addReturnLeg(state.dirtyRoute))
              }
              onRemoveLastSegment={() =>
                updateDirtyRoute(removeLastReturnLeg(state.dirtyRoute))
              }
              onUpdateSegment={(index, changes) =>
                updateDirtyRoute(
                  updateReturnLeg(state.dirtyRoute, index, changes),
                )
              }
              onDestinationSelect={() => {}}
              lastLegQualifier={{
                label: "Arriving after 7:00 PM",
                checked: Boolean(state.dirtyRoute.lastLegQualifiesForDinner),
                onChange: (checked) =>
                  updateDirtyRoute({
                    ...state.dirtyRoute,
                    lastLegQualifiesForDinner: checked,
                  }),
              }}
              pendingCounty={pendingCounty}
              onCountySubmit={submitCounty}
              onCountyCancel={() => setPendingCounty(null)}
              actions={workflowActions}
              isDisabled={isAdvancingRoute}
            />
            {routeCalculationError && (
              <p role="alert" className="font-medium text-red-700">
                {routeCalculationError}
              </p>
            )}
            <LongTripModal
              isOpen={showLongTripWarning}
              onReview={() => {
                setShowLongTripWarning(false);
                setPendingReturnAction(null);
              }}
              onConfirm={async () => {
                const pending = pendingReturnAction;
                setShowLongTripWarning(false);
                setPendingReturnAction(null);
                if (pending)
                  await completeReturnAction(pending.action, pending.route);
              }}
            />
          </>
        );
      case "expenses":
        return (
          <ExpensesStep
            draft={state.workingDraft}
            expenses={expenses}
            errors={expenseErrors}
            errorSummaryRef={errorSummaryRef}
            calculationError={expenseCalculationError}
            lodgingErrors={lodgingErrors}
            isDisabled={calculateExpenses.isPending}
            pendingLodgingRows={pendingLodgingRows}
            onExpensesChange={(next) => {
              setExpenseErrors({});
              setExpenses(next);
              dispatch({
                type: "UPDATE_DRAFT",
                draft: applyEditableExpenses(state.workingDraft, next),
              });
            }}
            onDraftChange={(next) =>
              dispatch({ type: "UPDATE_DRAFT", draft: next })
            }
            onLodgingSelect={handleLodgingSelect}
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
            isDisabled={calculateExpenses.isPending || submissionLocked}
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

  function updateDirtyRoute(route) {
    setRouteErrors({});
    dispatch({ type: "UPDATE_DIRTY_ROUTE", route });
  }

  function resetExpensesAfterRouteCalculation(calculatedDraft) {
    expenseOverrides.resetFromDraft(calculatedDraft);
    setExpenses(createEditableExpenses(calculatedDraft));
    setExpenseErrors({});
    setLodgingErrors({});
    setPendingLodgingRows({});
    setExpenseCalculationError(null);
    lodgingRequestRef.current = {};
  }
}

function SaveMessage({ message }) {
  if (!message) return null;
  const isError = message.type === "error";
  return (
    <p
      role={isError ? "alert" : "status"}
      className={
        isError ? "font-medium text-red-700" : "font-medium text-green-700"
      }
    >
      {message.text}
    </p>
  );
}

function routeErrorMessage(error) {
  const serialized = JSON.stringify(error?.data ?? {});
  if (
    error?.response?.status === 502 ||
    serialized.includes("DATA_PROVIDER_ERROR")
  ) {
    return "A third-party travel service is unavailable. Your route was not calculated; please try again.";
  }
  if (
    error?.response?.status === 422 ||
    serialized.includes("MEAL_RATES_UNAVAILABLE")
  ) {
    const date = error?.data?.errorData;
    return `${date ? `Your travel date ${date} is` : "One or more of your travel dates are"} outside the range we currently have meal rates for. Meal rates are published once a year for the federal fiscal year beginning October 1; please choose an earlier date or try again tomorrow.`;
  }
  if (
    error?.response?.status === 400 ||
    serialized.includes("INVALID_TRAVEL_DATES")
  ) {
    return "One or more outbound or return dates must be corrected.";
  }
  return "Your route could not be calculated. Your entered information is still available; please try again.";
}
