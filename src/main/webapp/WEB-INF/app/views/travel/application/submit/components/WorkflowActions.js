import React from "react";
import Button from "app/components/Button";
import { STANDARD_STEPS } from "../../workflow/workflowSteps";

export default function WorkflowActions({
  stepId,
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
  const stepIndex = STANDARD_STEPS.findIndex((step) => step.id === stepId);
  if (stepIndex < 0) throw new Error(`Unknown workflow step: ${stepId}`);
  const step = STANDARD_STEPS[stepIndex];
  const isReview = stepId === "review";
  const primaryLabel = isReview ? finalActionLabel : "Next";

  return (
    <div className="flex flex-wrap items-center justify-end gap-3">
      {onCancel && (
        <Button
          variant="secondary"
          onPress={onCancel}
          isDisabled={isDisabled || isCancelDisabled}
        >
          Cancel edits
        </Button>
      )}
      {stepIndex > 0 && (
        <Button
          variant="secondary"
          onPress={onBack}
          isDisabled={isDisabled || isPrimaryPending}
        >
          Back
        </Button>
      )}
      {step.allowsDraftSave && onSave && (
        <Button
          variant="secondary"
          onPress={onSave}
          isDisabled={isDisabled || isPrimaryPending}
          isPending={isSaving}
        >
          Save
        </Button>
      )}
      <Button
        onPress={onPrimary}
        isDisabled={isDisabled || isPrimaryDisabled || isPrimaryPending}
        isPending={isPrimaryPending}
      >
        {primaryLabel}
      </Button>
    </div>
  );
}
