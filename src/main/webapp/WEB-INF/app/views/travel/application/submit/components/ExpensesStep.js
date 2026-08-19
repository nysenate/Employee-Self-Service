import React, { useRef, useState } from "react";
import Card from "app/components/Card";
import AddressAutocomplete from "app/views/travel/shared/components/AddressAutocomplete";
import FormErrorSummary from "./FormErrorSummary";
import { EXPENSE_FIELDS, updateExpenseRow } from "../expenseModel";

const addressText = (address) => address?.formattedAddressWithCounty ?? "";

export default function ExpensesStep({
  draft,
  expenses,
  errors,
  errorSummaryRef,
  calculationError,
  lodgingErrors,
  pendingLodgingRows,
  isDisabled,
  onExpensesChange,
  onDraftChange,
  onLodgingSelect,
  actions,
}) {
  return (
    <Card>
      <Card.Content className="space-y-6 p-5 sm:p-6">
        <div className="border-l-4 border-teal-600 pl-4">
          <h1 className="text-2xl font-semibold text-teal-900">Expenses</h1>
          <p className="mt-1 text-sm text-gray-600">
            Review eligible reimbursements and enter estimated trip expenses.
          </p>
        </div>
        <FormErrorSummary
          ref={errorSummaryRef}
          errors={errors}
          fieldIdPrefix="expense-"
        />
        {calculationError && (
          <p role="alert" className="font-medium text-red-700">
            {calculationError}
          </p>
        )}
        <fieldset disabled={isDisabled} className="space-y-6">
          <section aria-labelledby="miscellaneous-expenses-heading">
            <h2
              id="miscellaneous-expenses-heading"
              className="text-lg font-semibold"
            >
              Miscellaneous estimated expenses
            </h2>
            <p className="mt-1 text-sm text-gray-600">
              Enter anticipated costs. Leave a field blank when no expense is
              expected.
            </p>
            <div className="mt-4 grid gap-4 sm:grid-cols-2">
              {EXPENSE_FIELDS.map(([field, label]) => (
                <label
                  key={field}
                  htmlFor={`expense-${field}`}
                  className="font-medium"
                >
                  {label}
                  <input
                    id={`expense-${field}`}
                    className={`input mt-1 w-full ${errors[field] ? "input--invalid" : ""}`}
                    inputMode="decimal"
                    value={expenses[field]}
                    aria-invalid={Boolean(errors[field])}
                    aria-describedby={
                      errors[field] ? `expense-${field}-error` : undefined
                    }
                    onChange={(e) =>
                      onExpensesChange({ ...expenses, [field]: e.target.value })
                    }
                  />
                  {errors[field] && (
                    <span
                      id={`expense-${field}-error`}
                      className="mt-1 block text-sm text-red-700"
                    >
                      {errors[field]}
                    </span>
                  )}
                </label>
              ))}
            </div>
          </section>
          <Meals draft={draft} onDraftChange={onDraftChange} />
          <Lodging
            draft={draft}
            errors={lodgingErrors}
            pendingRows={pendingLodgingRows}
            onDraftChange={onDraftChange}
            onSelect={onLodgingSelect}
          />
          <Mileage draft={draft} onDraftChange={onDraftChange} />
        </fieldset>
      </Card.Content>
      <Card.Footer className="mt-0 justify-end bg-gray-50 px-5 py-4 sm:px-6">
        {actions}
      </Card.Footer>
    </Card>
  );
}

function Meals({ draft, onDraftChange }) {
  const rows = (draft.amendment?.mealPerDiems?.allMealPerDiems ?? [])
    .map((row, index) => ({ row, index }))
    .filter(({ row }) => row.qualifiesForBreakfast || row.qualifiesForDinner);
  if (!draft.amendment?.mealPerDiems?.isAllowedMeals || !rows.length)
    return null;
  return (
    <section
      aria-labelledby="meal-reimbursements-heading"
      className="border-t border-gray-200 pt-6"
    >
      <h2 id="meal-reimbursements-heading" className="text-lg font-semibold">
        Meal reimbursements
      </h2>
      <p className="mt-1 text-sm text-gray-600">
        Select each eligible meal you expect to claim.
      </p>
      <div className="mt-4 space-y-3">
        {rows.map(({ row, index }) => (
          <div key={index} className="border border-gray-200 p-4 sm:p-5">
            <p className="font-medium">
              {addressText(row.address)} — {row.date}
            </p>
            <div className="mt-2 flex gap-5">
              {row.qualifiesForBreakfast && (
                <Check
                  label="Breakfast"
                  checked={row.isBreakfastRequested}
                  onChange={(checked) =>
                    onDraftChange(
                      updateExpenseRow(draft, "mealPerDiems", index, {
                        isBreakfastRequested: checked,
                      }),
                    )
                  }
                />
              )}
              {row.qualifiesForDinner && (
                <Check
                  label="Dinner"
                  checked={row.isDinnerRequested}
                  onChange={(checked) =>
                    onDraftChange(
                      updateExpenseRow(draft, "mealPerDiems", index, {
                        isDinnerRequested: checked,
                      }),
                    )
                  }
                />
              )}
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}

function Lodging({ draft, errors, pendingRows = {}, onDraftChange, onSelect }) {
  const rows = draft.amendment?.lodgingPerDiems?.allLodgingPerDiems ?? [];
  if (!rows.length) return null;
  return (
    <section
      aria-labelledby="lodging-reimbursements-heading"
      className="border-t border-gray-200 pt-6"
    >
      <h2 id="lodging-reimbursements-heading" className="text-lg font-semibold">
        Lodging reimbursements
      </h2>
      <p className="mt-1 text-sm text-gray-600">
        Confirm each lodging night and select a hotel address to calculate its
        rate.
      </p>
      <div className="mt-4 space-y-4">
        {rows.map((row, index) => (
          <LodgingRow
            key={index}
            row={row}
            index={index}
            error={errors[index]}
            isPending={Boolean(pendingRows[index])}
            onRequested={(checked) =>
              onDraftChange(
                updateExpenseRow(draft, "lodgingPerDiems", index, {
                  isReimbursementRequested: checked,
                }),
              )
            }
            onSelect={(address) => onSelect(row, index, address)}
          />
        ))}
      </div>
    </section>
  );
}

function LodgingRow({ row, index, error, isPending, onRequested, onSelect }) {
  const [text, setText] = useState(addressText(row.address));
  const selectedRef = useRef(row.address);
  return (
    <div className="border border-gray-200 p-4 sm:p-5">
      <Check
        label={`${row.date} lodging reimbursement`}
        checked={row.isReimbursementRequested}
        onChange={onRequested}
      />
      <p className="my-3 text-sm text-gray-600">
        Rate: ${row.rate} · Requested: ${row.requestedPerDiem}
      </p>
      <AddressAutocomplete
        id={`expense-lodging-${index}`}
        label={`Hotel address for ${row.date}`}
        value={text}
        error={error}
        isDisabled={isPending}
        onTextChange={(value) => {
          setText(value);
          if (value !== addressText(selectedRef.current)) onSelect(null);
        }}
        onSelect={(address) => {
          selectedRef.current = address;
          setText(addressText(address));
          onSelect(address);
        }}
      />
    </div>
  );
}

function Mileage({ draft, onDraftChange }) {
  const rows = (draft.amendment?.mileagePerDiems?.allPerDiems ?? [])
    .map((row, index) => ({ row, index }))
    .filter(({ row }) => row.qualifiesForReimbursement);
  if (!rows.length) return null;
  return (
    <section
      aria-labelledby="mileage-reimbursements-heading"
      className="border-t border-gray-200 pt-6"
    >
      <h2 id="mileage-reimbursements-heading" className="text-lg font-semibold">
        Mileage reimbursements
      </h2>
      <p className="mt-1 text-sm text-gray-600">
        Select each qualifying route you expect to claim.
      </p>
      <div className="mt-4 space-y-3">
        {rows.map(({ row, index }) => (
          <div key={index} className="border border-gray-200 p-4 sm:p-5">
            <Check
              label={`${addressText(row.from)} to ${addressText(row.to)} — ${row.travelDate}`}
              checked={row.isReimbursementRequested}
              onChange={(checked) =>
                onDraftChange(
                  updateExpenseRow(draft, "mileagePerDiems", index, {
                    isReimbursementRequested: checked,
                  }),
                )
              }
            />
          </div>
        ))}
      </div>
    </section>
  );
}

function Check({ label, checked, onChange }) {
  return (
    <label className="flex items-center gap-2">
      <input
        type="checkbox"
        checked={Boolean(checked)}
        onChange={(e) => onChange(e.target.checked)}
      />
      {label}
    </label>
  );
}
