import React, { useRef, useState } from "react";
import Card from "app/components/Card";
import AddressAutocomplete from "app/views/travel/shared/components/AddressAutocomplete";
import { isoToMediumDate } from "app/utils/dateUtils";
import FormErrorSummary from "./FormErrorSummary";
import { EXPENSE_FIELDS, updateExpenseRow } from "../expenseModel";

const addressText = (address) => address?.formattedAddressWithCounty ?? "";

function mileageDateText(value) {
  const match = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(value ?? "");
  if (!match) return value ?? "";
  return isoToMediumDate(`${match[3]}-${match[1]}-${match[2]}`) || value;
}

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
            <div className="mt-5 grid grid-cols-[minmax(0,1fr)_minmax(20rem,1fr)] items-start gap-10">
              <div className="w-fit space-y-3">
                {EXPENSE_FIELDS.map(([field, label]) => (
                  <label
                    key={field}
                    htmlFor={`expense-${field}`}
                    className="grid grid-cols-[12rem_5rem] items-center gap-x-3 font-medium"
                  >
                    <span className="text-right">{label}: $</span>
                    <input
                      id={`expense-${field}`}
                      className={`input w-full ${errors[field] ? "input--invalid" : ""}`}
                      inputMode="decimal"
                      value={expenses[field]}
                      aria-invalid={Boolean(errors[field])}
                      aria-describedby={
                        errors[field] ? `expense-${field}-error` : undefined
                      }
                      onChange={(e) =>
                        onExpensesChange({
                          ...expenses,
                          [field]: e.target.value,
                        })
                      }
                    />
                    {errors[field] && (
                      <span
                        id={`expense-${field}-error`}
                        className="col-start-2 mt-1 block text-sm font-normal text-red-700"
                      >
                        {errors[field]}
                      </span>
                    )}
                  </label>
                ))}
              </div>
              <aside className="border-l-4 border-orange-400 bg-orange-50 p-5 text-orange-800">
                <p>
                  Meals, lodging, and mileage reimbursements are calculated
                  automatically.
                </p>
                <p className="mt-5">
                  Estimate toll expenses with the{" "}
                  <a
                    href="https://tollcalculator.thruway.ny.gov"
                    target="_blank"
                    rel="noopener noreferrer"
                    className="font-medium underline"
                  >
                    NYS Thruway Toll Calculator
                  </a>
                  .
                </p>
              </aside>
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
      <div className="mt-3 grid grid-cols-2 items-start gap-10">
        <p className="text-gray-600">
          You may qualify for the following meal reimbursements.
          Select each eligible meal you expect to claim.
        </p>
        <aside className="border-l-4 border-orange-400 bg-orange-50 p-4 text-orange-800">
          Meal reimbursement eligibility depends on your arrival and departure
          times. Refer to the{" "}
          <a
            href="https://my.nysenate.gov/department/secretary-senate/travel"
            target="_blank"
            rel="noopener noreferrer"
            className="font-medium underline"
          >
            Senate Travel Guidelines
          </a>{" "}
          for current eligibility rules and rates.
        </aside>
      </div>
      <table className="table mt-4" aria-label="Eligible meal reimbursements">
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell" scope="col">Address</th>
            <th className="table__head__cell w-40" scope="col">Date</th>
            <th className="table__head__cell w-32 text-center" scope="col">
              Breakfast
            </th>
            <th className="table__head__cell w-32 text-center" scope="col">
              Dinner
            </th>
          </tr>
        </thead>
        <tbody className="table__body table__body--striped">
          {rows.map(({ row, index }) => (
            <tr key={index} className="table__row">
              <td className="table__cell">{addressText(row.address)}</td>
              <td className="table__cell whitespace-nowrap">
                {isoToMediumDate(row.date)}
              </td>
              <td className="table__cell text-center">
                {row.qualifiesForBreakfast ? (
                  <Check
                    label="Breakfast"
                    visuallyHiddenLabel
                    checked={row.isBreakfastRequested}
                    onChange={(checked) =>
                      onDraftChange(
                        updateExpenseRow(draft, "mealPerDiems", index, {
                          isBreakfastRequested: checked,
                        }),
                      )
                    }
                  />
                ) : (
                  <span aria-hidden="true">—</span>
                )}
              </td>
              <td className="table__cell text-center">
                {row.qualifiesForDinner ? (
                  <Check
                    label="Dinner"
                    visuallyHiddenLabel
                    checked={row.isDinnerRequested}
                    onChange={(checked) =>
                      onDraftChange(
                        updateExpenseRow(draft, "mealPerDiems", index, {
                          isDinnerRequested: checked,
                        }),
                      )
                    }
                  />
                ) : (
                  <span aria-hidden="true">—</span>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
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
      <table className="table mt-4" aria-label="Eligible lodging reimbursements">
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell" scope="col">Hotel address</th>
            <th className="table__head__cell w-40" scope="col">Date</th>
            <th className="table__head__cell w-28 text-center" scope="col">
              Claim
            </th>
          </tr>
        </thead>
        <tbody className="table__body table__body--striped">
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
        </tbody>
      </table>
    </section>
  );
}

function LodgingRow({ row, index, error, isPending, onRequested, onSelect }) {
  const [text, setText] = useState(addressText(row.address));
  const selectedRef = useRef(row.address);
  return (
    <tr className="table__row">
      <td className="table__cell py-3 [&_label]:sr-only">
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
      </td>
      <td className="table__cell whitespace-nowrap">
        {isoToMediumDate(row.date)}
      </td>
      <td className="table__cell text-center">
        <Check
          label={`Claim lodging for ${isoToMediumDate(row.date)}`}
          visuallyHiddenLabel
          checked={row.isReimbursementRequested}
          onChange={onRequested}
        />
      </td>
    </tr>
  );
}

function Mileage({ draft, onDraftChange }) {
  const mileagePerDiems = draft.amendment?.mileagePerDiems;
  if (!mileagePerDiems?.doesTripQualifyForReimbursement) return null;
  const rows = (mileagePerDiems.allPerDiems ?? [])
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
      <table className="table mt-4" aria-label="Eligible mileage reimbursements">
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell" scope="col">From</th>
            <th className="table__head__cell" scope="col">To</th>
            <th className="table__head__cell w-40" scope="col">Date</th>
            <th className="table__head__cell w-28 text-center" scope="col">
              Claim
            </th>
          </tr>
        </thead>
        <tbody className="table__body table__body--striped">
          {rows.map(({ row, index }) => {
            const from = addressText(row.from);
            const to = addressText(row.to);
            const date = mileageDateText(row.travelDate);
            return (
              <tr key={index} className="table__row">
                <td className="table__cell">{from}</td>
                <td className="table__cell">{to}</td>
                <td className="table__cell whitespace-nowrap">{date}</td>
                <td className="table__cell text-center">
                  <Check
                    label={`Claim mileage from ${from} to ${to} for ${date}`}
                    visuallyHiddenLabel
                    checked={row.isReimbursementRequested}
                    onChange={(checked) =>
                      onDraftChange(
                        updateExpenseRow(draft, "mileagePerDiems", index, {
                          isReimbursementRequested: checked,
                        }),
                      )
                    }
                  />
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </section>
  );
}

function Check({ label, visuallyHiddenLabel = false, checked, onChange }) {
  return (
    <label
      className={`flex items-center gap-2 ${visuallyHiddenLabel ? "justify-center" : ""}`}
    >
      <input
        type="checkbox"
        checked={Boolean(checked)}
        onChange={(e) => onChange(e.target.checked)}
      />
      <span className={visuallyHiddenLabel ? "sr-only" : undefined}>{label}</span>
    </label>
  );
}
