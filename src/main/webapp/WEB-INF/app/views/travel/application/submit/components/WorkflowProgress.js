import React from "react";
import { Check } from "lucide-react";

export default function WorkflowProgress({
  steps,
  currentStepId,
  completedStepIds,
  onSelect,
  isDisabled = false,
}) {
  return (
    <nav
      aria-label="Travel application progress"
      className="overflow-x-auto border border-gray-200 bg-white px-8 py-5"
    >
      <div className="relative" style={{ minWidth: steps.length * 80 }}>
        <div
          aria-hidden="true"
          className="absolute top-4 h-0.5 bg-gray-200"
          style={{
            left: `${50 / steps.length}%`,
            right: `${50 / steps.length}%`,
          }}
        />
        <ol
          className="relative grid"
          style={{
            gridTemplateColumns: `repeat(${steps.length}, minmax(0, 1fr))`,
          }}
        >
          {steps.map((step, index) => (
            <ProgressStep
              key={step.id}
              step={step}
              index={index}
              isCurrent={step.id === currentStepId}
              isCompleted={completedStepIds.includes(step.id)}
              connectorCompleted={
                index > 0 && completedStepIds.includes(steps[index - 1].id)
              }
              onSelect={onSelect}
              isDisabled={isDisabled}
            />
          ))}
        </ol>
      </div>
    </nav>
  );
}

function ProgressStep({
  step,
  index,
  isCurrent,
  isCompleted,
  connectorCompleted,
  onSelect,
  isDisabled,
}) {
  const canSelect = !isDisabled && isCompleted && !isCurrent;
  return (
    <li className="relative flex justify-center">
      {connectorCompleted && (
        <div
          aria-hidden="true"
          className="absolute top-4 right-1/2 h-0.5 w-full bg-teal-600"
        />
      )}
      <button
        type="button"
        aria-current={isCurrent ? "step" : undefined}
        disabled={!canSelect}
        onClick={() => onSelect(step.id)}
        className={`group relative z-10 flex min-w-20 flex-col items-center gap-2 text-sm font-semibold ${stepTextClass(canSelect, isCurrent)} focus-visible:ring-2 focus-visible:ring-teal-600 focus-visible:ring-offset-4`}
      >
        <StepMarker
          index={index}
          isCurrent={isCurrent}
          isCompleted={isCompleted}
        />
        <span>{step.label}</span>
        {isCompleted && !isCurrent && (
          <span className="sr-only"> (completed)</span>
        )}
      </button>
    </li>
  );
}

function StepMarker({ index, isCurrent, isCompleted }) {
  const markerClass = isCurrent
    ? "border-teal-700 bg-teal-700 text-white ring-4 ring-teal-100"
    : isCompleted
      ? "border-teal-600 bg-teal-600 text-white group-hover:border-teal-800 group-hover:bg-teal-800"
      : "border-gray-300 bg-white text-gray-500";
  return (
    <span
      aria-hidden="true"
      className={`flex h-8 w-8 items-center justify-center rounded-full border-2 text-xs transition-colors ${markerClass}`}
    >
      {isCompleted && !isCurrent ? (
        <Check className="h-4 w-4" strokeWidth={3} />
      ) : (
        index + 1
      )}
    </span>
  );
}

function stepTextClass(canSelect, isCurrent) {
  if (canSelect) return "cursor-pointer text-teal-800";
  if (isCurrent) return "cursor-default text-teal-900";
  return "cursor-not-allowed text-gray-500";
}
