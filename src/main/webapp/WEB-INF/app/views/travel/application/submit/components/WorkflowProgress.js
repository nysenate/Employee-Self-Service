import React from "react";
import { Check } from "lucide-react";
import { WORKFLOW_STEPS } from "../newTravelApplicationReducer";

export default function WorkflowProgress({
  currentStep,
  furthestCompletedStep,
  onSelect,
}) {
  return (
    <nav
      aria-label="Travel application progress"
      className="border border-gray-200 bg-white px-8 py-5"
    >
      <div className="relative">
        <div
          aria-hidden="true"
          className="absolute top-4 right-[10%] left-[10%] h-0.5 bg-gray-200"
        />
        <ol className="relative grid grid-cols-5">
          {WORKFLOW_STEPS.map((step, index) => {
            const isCurrent = index === currentStep;
            const isCompleted = index <= furthestCompletedStep;
            const canSelect = isCompleted && !isCurrent;

            return (
              <li key={step} className="relative flex justify-center">
                {index > 0 && index <= furthestCompletedStep + 1 && (
                  <div
                    aria-hidden="true"
                    className="absolute top-4 right-1/2 h-0.5 w-full bg-teal-600"
                  />
                )}
                <button
                  type="button"
                  aria-current={isCurrent ? "step" : undefined}
                  disabled={!canSelect}
                  onClick={() => onSelect(index)}
                  className={`group relative z-10 flex min-w-20 flex-col items-center gap-2 text-sm font-semibold ${
                    canSelect
                      ? "cursor-pointer text-teal-800"
                      : isCurrent
                        ? "cursor-default text-teal-900"
                        : "cursor-not-allowed text-gray-500"
                  } focus-visible:ring-2 focus-visible:ring-teal-600 focus-visible:ring-offset-4`}
                >
                  <span
                    aria-hidden="true"
                    className={`flex h-8 w-8 items-center justify-center rounded-full border-2 text-xs transition-colors ${
                      isCurrent
                        ? "border-teal-700 bg-teal-700 text-white ring-4 ring-teal-100"
                        : isCompleted
                          ? "border-teal-600 bg-teal-600 text-white group-hover:border-teal-800 group-hover:bg-teal-800"
                          : "border-gray-300 bg-white text-gray-500"
                    }`}
                  >
                    {isCompleted && !isCurrent ? (
                      <Check className="h-4 w-4" strokeWidth={3} />
                    ) : (
                      index + 1
                    )}
                  </span>
                  <span>{step}</span>
                  {isCompleted && !isCurrent && (
                    <span className="sr-only"> (completed)</span>
                  )}
                </button>
              </li>
            );
          })}
        </ol>
      </div>
    </nav>
  );
}
