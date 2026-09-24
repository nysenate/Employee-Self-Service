import React from "react";
import Card from "app/components/Card";
import FormErrorSummary from "../submit/components/FormErrorSummary";
import { OVERRIDE_FIELDS, updateOverride } from "./overrideModel";

export default function OverridesStep({
  draft,
  errors,
  errorSummaryRef,
  calculationError,
  isDisabled,
  onChange,
  actions,
}) {
  return (
    <Card>
      <Card.Content className="space-y-6 p-5 sm:p-6">
        <div className="border-l-4 border-teal-600 pl-4">
          <h1 className="text-2xl font-semibold text-teal-900">
            Expense Overrides
          </h1>
          <p className="mt-1 text-sm text-gray-600">
            Replace the calculated meals or lodging total for this trip. Leave
            an override unchecked, or enter $0, to use the calculated amount.
          </p>
        </div>
        <FormErrorSummary
          ref={errorSummaryRef}
          errors={errors}
          fieldIdPrefix="override-"
        />
        {calculationError && (
          <p role="alert" className="text-red-700">
            {calculationError}
          </p>
        )}
        <fieldset disabled={isDisabled} className="space-y-5">
          {OVERRIDE_FIELDS.map(([field, label]) => {
            const perDiems = draft.amendment[field];
            const enabled = Boolean(perDiems?.isOverridden);
            return (
              <div key={field} className="flex flex-wrap items-center gap-4">
                <label className="flex w-44 items-center gap-2 font-medium">
                  <input
                    type="checkbox"
                    checked={enabled}
                    onChange={(event) =>
                      onChange(
                        updateOverride(draft, field, {
                          isOverridden: event.target.checked,
                          overrideRate: event.target.checked
                            ? (perDiems?.overrideRate ?? 0)
                            : 0,
                        }),
                      )
                    }
                  />
                  Override {label}
                </label>
                <label htmlFor={`override-${field}`}>{label} total ($)</label>
                <input
                  id={`override-${field}`}
                  className={`input w-32 ${errors[field] ? "input--invalid" : ""}`}
                  inputMode="decimal"
                  disabled={!enabled}
                  value={perDiems?.overrideRate ?? ""}
                  aria-invalid={Boolean(errors[field])}
                  aria-describedby={
                    errors[field] ? `override-${field}-error` : undefined
                  }
                  onChange={(event) =>
                    onChange(
                      updateOverride(draft, field, {
                        overrideRate: event.target.value,
                      }),
                    )
                  }
                />
                {errors[field] && (
                  <p
                    id={`override-${field}-error`}
                    className="w-full text-sm text-red-700"
                  >
                    {errors[field]}
                  </p>
                )}
              </div>
            );
          })}
        </fieldset>
        {!draft.amendment.mealPerDiems?.isAllowedMeals && (
          <p className="text-sm text-gray-600">
            This traveler is not eligible for meals. A meals override will not
            change their meal reimbursement.
          </p>
        )}
      </Card.Content>
      <Card.Footer className="mt-0 justify-end bg-gray-50 px-5 py-4 sm:px-6">
        {actions}
      </Card.Footer>
    </Card>
  );
}
