import React from "react";
import Button from "app/components/Button";
import { STANDARD_STEPS } from "../../workflow/workflowSteps";

export default function WorkflowActions({
  stepId,
  steps = STANDARD_STEPS,
  onBack,
  onSave,
  onCancel,
  onPrimary,
  isSaving = false,
  isPrimaryPending = false,
  isPrimaryDisabled = false,
  isCancelDisabled = false,
  isDisabled = false,
  finalActionLabel = "Submit application",
}) {
  const stepIndex = steps.findIndex((step) => step.id === stepId);
  if (stepIndex < 0) throw new Error(`Unknown workflow step: ${stepId}`);
  const step = steps[stepIndex];
  const isReview = stepId === "review";
  const primaryLabel = isReview ? finalActionLabel : "Next";

  return (
    <div className="flex flex-wrap items-center justify-end gap-3">
      {onCancel && (
        <Button
          variant="secondary"
          className="px-5"
          onPress={onCancel}
          isDisabled={
            isDisabled || isCancelDisabled || isSaving || isPrimaryPending
          }
        >
          Cancel edits
        </Button>
      )}
      {stepIndex > 0 && (
        <Button
          variant="quiet"
          className="border border-transparent px-5"
          onPress={onBack}
          isDisabled={isDisabled || isPrimaryPending || isSaving}
        >
          Back
        </Button>
      )}
      {step.allowsDraftSave && onSave && (
        <Button
          variant="secondary"
          className="px-5"
          onPress={onSave}
          isDisabled={isDisabled || isPrimaryPending}
          isPending={isSaving}
        >
          Save
        </Button>
      )}
      <Button
        className="px-5"
        onPress={onPrimary}
        isDisabled={
          isDisabled || isPrimaryDisabled || isPrimaryPending || isSaving
        }
        isPending={isPrimaryPending}
      >
        {primaryLabel}
      </Button>
    </div>
  );
}
