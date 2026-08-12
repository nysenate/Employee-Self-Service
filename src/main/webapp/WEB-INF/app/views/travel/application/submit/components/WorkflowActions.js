import React from "react";
import Button from "app/components/Button";

export const WORKFLOW_ACTIONS = Object.freeze([
  { back: false, save: true, primary: "Next" },
  { back: true, save: false, primary: "Next" },
  { back: true, save: true, primary: "Next" },
  { back: true, save: true, primary: "Next" },
  { back: true, save: true, primary: "Submit Application" },
]);

export default function WorkflowActions({
  step,
  onBack,
  onSave,
  onPrimary,
  isSaving = false,
}) {
  const actions = WORKFLOW_ACTIONS[step];

  return (
    <div className="flex flex-wrap items-center justify-end gap-3">
      {actions.back && (
        <Button variant="secondary" onPress={onBack}>
          Back
        </Button>
      )}
      {actions.save && (
        <Button
          variant="secondary"
          onPress={onSave}
          isDisabled={!onSave}
          isPending={isSaving}
        >
          Save
        </Button>
      )}
      <Button onPress={onPrimary}>{actions.primary}</Button>
    </div>
  );
}
