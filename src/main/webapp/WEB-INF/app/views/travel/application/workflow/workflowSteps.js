export const STANDARD_STEPS = Object.freeze([
  Object.freeze({ id: "purpose", label: "Purpose", allowsDraftSave: true }),
  Object.freeze({ id: "outbound", label: "Outbound", allowsDraftSave: false }),
  Object.freeze({ id: "return", label: "Return", allowsDraftSave: true }),
  Object.freeze({ id: "expenses", label: "Expenses", allowsDraftSave: true }),
  Object.freeze({ id: "review", label: "Review", allowsDraftSave: true }),
]);

export const ADMIN_EDIT_STEPS = Object.freeze([
  ...STANDARD_STEPS.slice(0, -1),
  Object.freeze({
    id: "overrides",
    label: "Overrides",
    allowsDraftSave: false,
  }),
  STANDARD_STEPS.at(-1),
]);
