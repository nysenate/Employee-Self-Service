export const EXPENSE_FIELDS = Object.freeze([
  ["tolls", "Tolls"],
  ["parking", "Parking"],
  ["alternateTransportation", "Taxi/Bus/Subway"],
  ["trainAndPlane", "Train/Airplane"],
  ["registration", "Registration Fee"],
]);

const CURRENCY = /^(?:\d+(?:\.\d{0,2})?|\.\d{1,2})$/;

export function createEditableExpenses(draft) {
  const allowances = draft.amendment?.allowances ?? {};
  return Object.fromEntries(
    EXPENSE_FIELDS.map(([field]) => [
      field,
      allowances[field] == null ? "" : String(allowances[field]),
    ]),
  );
}

export function validateExpenses(expenses) {
  const errors = {};
  for (const [field, label] of EXPENSE_FIELDS) {
    const value = expenses[field]?.trim() ?? "";
    if (!value) continue;
    if (!CURRENCY.test(value)) {
      errors[field] = value.includes("-")
        ? `${label} cannot be negative.`
        : value.includes(".") && /\.\d{3,}$/.test(value)
          ? `${label} must be in increments of 0.01.`
          : `${label} must be numbers using ordinary currency notation.`;
    }
  }
  return errors;
}

export function applyEditableExpenses(draft, expenses) {
  return withoutExpenseTotals({
    ...draft,
    amendment: {
      ...draft.amendment,
      allowances: {
        ...(draft.amendment?.allowances ?? {}),
        ...Object.fromEntries(
          EXPENSE_FIELDS.map(([field]) => [
            field,
            Number(expenses[field]?.trim() || 0),
          ]),
        ),
      },
    },
  });
}

export function expenseSignature(draft) {
  const amendment = draft.amendment ?? {};
  return JSON.stringify({
    allowances: amendment.allowances ?? {},
    meals: (amendment.mealPerDiems?.allMealPerDiems ?? []).map((row) => ({
      id: row.id,
      breakfast: row.isBreakfastRequested,
      dinner: row.isDinnerRequested,
    })),
    lodging: (amendment.lodgingPerDiems?.allLodgingPerDiems ?? []).map(
      (row) => ({
        id: row.id,
        requested: row.isReimbursementRequested,
        address: row.address,
      }),
    ),
    mileage: (amendment.mileagePerDiems?.allPerDiems ?? []).map((row) => ({
      id: row.id,
      requested: row.isReimbursementRequested,
    })),
  });
}

export function updateExpenseRow(draft, group, index, changes) {
  const collection = {
    mealPerDiems: "allMealPerDiems",
    lodgingPerDiems: "allLodgingPerDiems",
    mileagePerDiems: "allPerDiems",
  }[group];
  const container = draft.amendment?.[group] ?? {};
  return withoutExpenseTotals({
    ...draft,
    amendment: {
      ...draft.amendment,
      [group]: {
        ...container,
        [collection]: (container[collection] ?? []).map((row, rowIndex) =>
          rowIndex === index ? { ...row, ...changes } : row,
        ),
      },
    },
  });
}

export function applyLodgingCalculation(draft, index, calculation) {
  const rows = draft.amendment?.lodgingPerDiems?.allLodgingPerDiems ?? [];
  const currentRow = rows[index];
  if (!currentRow) return draft;
  return updateExpenseRow(draft, "lodgingPerDiems", index, {
    ...calculation,
    id: currentRow.id,
    isReimbursementRequested: currentRow.isReimbursementRequested,
  });
}

function withoutExpenseTotals(draft) {
  const amendment = { ...draft.amendment };
  for (const field of [
    "mealAllowance",
    "lodgingAllowance",
    "mileageAllowance",
    "tollsAndParkingAllowance",
    "transportationAllowance",
    "registrationAllowance",
    "totalAllowance",
  ])
    delete amendment[field];
  return { ...draft, amendment };
}
