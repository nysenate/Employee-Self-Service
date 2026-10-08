export const OVERRIDE_FIELDS = [
  ["mealPerDiems", "Meals"],
  ["lodgingPerDiems", "Lodging"],
];

export function validateOverrides(draft) {
  const errors = {};
  for (const [field, label] of OVERRIDE_FIELDS) {
    const perDiems = draft.amendment?.[field];
    if (!perDiems?.isOverridden) continue;
    const value = String(perDiems.overrideRate ?? "").trim();
    if (value && !/^(?:\d+(?:\.\d{0,2})?|\.\d{1,2})$/.test(value)) {
      errors[field] =
        `${label} override must be a nonnegative amount in increments of 0.01.`;
    }
  }
  return errors;
}

export function updateOverride(draft, field, changes) {
  return {
    ...draft,
    amendment: {
      ...draft.amendment,
      [field]: { ...draft.amendment?.[field], ...changes },
    },
  };
}

export function normalizeOverrides(draft) {
  return OVERRIDE_FIELDS.reduce((next, [field]) => {
    const perDiems = next.amendment[field];
    const rate = perDiems?.isOverridden
      ? Number(perDiems.overrideRate || 0)
      : 0;
    return updateOverride(next, field, {
      overrideRate: rate,
      isOverridden: rate !== 0,
    });
  }, draft);
}

export function editableOverrides(draft) {
  return Object.fromEntries(
    OVERRIDE_FIELDS.map(([field]) => [
      field,
      {
        isOverridden: Boolean(draft.amendment?.[field]?.isOverridden),
        overrideRate: String(draft.amendment?.[field]?.overrideRate ?? 0),
      },
    ]),
  );
}

export function applyOverrides(draft, overrides) {
  return OVERRIDE_FIELDS.reduce(
    (next, [field]) => updateOverride(next, field, overrides[field]),
    draft,
  );
}
