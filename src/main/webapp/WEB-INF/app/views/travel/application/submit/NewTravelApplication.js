import React, { useReducer, useRef, useState } from "react";
import Card from "app/components/Card";
import PurposeStep from "./components/PurposeStep";
import RouteStep from "./components/RouteStep";
import OutsideConusModal from "./components/OutsideConusModal";
import UnsavedChangesModal from "./components/UnsavedChangesModal";
import LongTripModal from "./components/LongTripModal";
import WorkflowActions from "./components/WorkflowActions";
import WorkflowProgress from "./components/WorkflowProgress";
import { useUnsavedChangesGuard } from "./hooks/useUnsavedChangesGuard";
import {
  useSaveTravelDraft,
  useUploadSupportingDocuments,
} from "./hooks/usePurposeMutations";
import { validatePurpose } from "./purposeValidation";
import {
  isLongTrip,
  normalizeCompleteRoute,
  normalizeOutboundRoute,
  isOutsideConus,
  validateOutboundDestinations,
  validateOutboundRoute,
  validateReturnRoute,
} from "./routeValidation";
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
} from "./routeModel";
import { useCalculateTravelRoute } from "./hooks/useRouteMutations";
import { useAddressCounty } from "app/views/travel/shared/hooks/useAddressCounty";
import {
  createWorkflowState,
  hasUnsavedChanges,
  needsRouteRecalculation,
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
  const [routeErrors, setRouteErrors] = useState({});
  const [pendingCounty, setPendingCounty] = useState(null);
  const [isAdvancingRoute, setIsAdvancingRoute] = useState(false);
  const [showConusWarning, setShowConusWarning] = useState(false);
  const [showLongTripWarning, setShowLongTripWarning] = useState(false);
  const [pendingReturnAction, setPendingReturnAction] = useState(null);
  const [routeCalculationError, setRouteCalculationError] = useState(null);
  const [saveMessage, setSaveMessage] = useState(null);
  const [uploadError, setUploadError] = useState(false);
  const errorSummaryRef = useRef(null);
  const routeAdvancePendingRef = useRef(false);
  const saveDraft = useSaveTravelDraft();
  const calculateRoute = useCalculateTravelRoute();
  const uploadDocuments = useUploadSupportingDocuments();
  const addressCounty = useAddressCounty();
  const guard = useUnsavedChangesGuard(hasUnsavedChanges(state));
  const routeNeedsRecalculation = needsRouteRecalculation(state);

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
    if (state.currentStep === 0 && !validateCurrentPurpose()) return;
    if (state.currentStep === 1) {
      const errors = validateOutboundRoute(state.dirtyRoute);
      setRouteErrors(errors);
      if (!reportValidationResult(errors)) return;
      await continueOutbound(normalizeOutboundRoute(state.dirtyRoute));
      return;
    }
    if (state.currentStep === 2) {
      await prepareReturnAction("next");
      return;
    }
    dispatch({ type: "COMPLETE_CURRENT_STEP" });
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
    setSaveMessage(null);
    if (state.currentStep === 0 && !validateCurrentPurpose()) return;
    if (state.currentStep === 2) {
      await prepareReturnAction("save");
      return;
    }
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
      }
      if (action === "save") {
        phase = "save";
        const savedDraft = await saveDraft.mutateAsync(draftToUse);
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
      isPrimaryPending={isAdvancingRoute}
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
      ) : state.currentStep === 1 ? (
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
              checked: Boolean(state.dirtyRoute.firstLegQualifiesForBreakfast),
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
      ) : state.currentStep === 2 ? (
        <>
          <RouteStep
            title="Return"
            description="Enter the route from your final destination back to your original departure point."
            legs={state.dirtyRoute.returnLegs}
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

  function updateDirtyRoute(route) {
    setRouteErrors({});
    dispatch({ type: "UPDATE_DIRTY_ROUTE", route });
  }
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
    error?.response?.status === 400 ||
    serialized.includes("INVALID_TRAVEL_DATES")
  ) {
    return "One or more outbound or return dates must be corrected.";
  }
  return "Your route could not be calculated. Your entered information is still available; please try again.";
}
