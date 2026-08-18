import React, { useReducer, useRef, useState } from "react";
import Card from "app/components/Card";
import PurposeStep from "./components/PurposeStep";
import RouteStep from "./components/RouteStep";
import OutsideConusModal from "./components/OutsideConusModal";
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
  normalizeOutboundRoute,
  isOutsideConus,
  validateOutboundDestinations,
  validateOutboundRoute,
} from "./routeValidation";
import {
  addOutboundLeg,
  findMissingOutboundCounty,
  removeLastOutboundLeg,
  setOutboundAddressCounty,
  updateOutboundLeg,
} from "./routeModel";
import { useAddressCounty } from "app/views/travel/shared/hooks/useAddressCounty";
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
  const [routeErrors, setRouteErrors] = useState({});
  const [pendingCounty, setPendingCounty] = useState(null);
  const [isAdvancingRoute, setIsAdvancingRoute] = useState(false);
  const [showConusWarning, setShowConusWarning] = useState(false);
  const [saveMessage, setSaveMessage] = useState(null);
  const [uploadError, setUploadError] = useState(false);
  const errorSummaryRef = useRef(null);
  const routeAdvancePendingRef = useRef(false);
  const saveDraft = useSaveTravelDraft();
  const uploadDocuments = useUploadSupportingDocuments();
  const addressCounty = useAddressCounty();
  const guard = useUnsavedChangesGuard(hasUnsavedChanges(state));

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
          />
          <OutsideConusModal
            isOpen={showConusWarning}
            onClose={() => setShowConusWarning(false)}
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
