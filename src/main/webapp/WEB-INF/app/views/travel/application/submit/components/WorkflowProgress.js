import React from "react";
import { Check } from "lucide-react";
import { WORKFLOW_STEPS } from "../newTravelApplicationReducer";

export default function WorkflowProgress({
  currentStep,
  furthestCompletedStep,
  onSelect,
}) {
  return (
    <nav aria-label="Travel application progress">
      <ol className="grid grid-cols-5 border border-teal-700 bg-white">
        {WORKFLOW_STEPS.map((step, index) => {
          const isCurrent = index === currentStep;
          const isCompleted = index <= furthestCompletedStep;
          const canSelect = isCompleted && !isCurrent;

          return (
            <li key={step} className="border-r border-teal-200 last:border-r-0">
              <button
                type="button"
                aria-current={isCurrent ? "step" : undefined}
                disabled={!canSelect}
                onClick={() => onSelect(index)}
                className={`flex min-h-14 w-full items-center justify-center gap-1 px-2 py-2 text-xs font-semibold sm:text-sm ${
                  isCurrent
                    ? "bg-teal-700 text-white"
                    : isCompleted
                      ? "cursor-pointer bg-teal-50 text-teal-900 hover:bg-teal-100"
                      : "cursor-not-allowed text-gray-500"
                }`}
              >
                {isCompleted && !isCurrent && (
                  <Check
                    aria-hidden="true"
                    className="hidden h-4 w-4 sm:block"
                  />
                )}
                <span>{step}</span>
                {isCompleted && !isCurrent && (
                  <span className="sr-only"> (completed)</span>
                )}
              </button>
            </li>
          );
        })}
      </ol>
    </nav>
  );
}
