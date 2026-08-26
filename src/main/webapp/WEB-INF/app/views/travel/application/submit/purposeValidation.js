export function validatePurpose(draft) {
  const purpose = draft.amendment?.purposeOfTravel;
  const eventType = purpose?.eventType;
  const errors = {};

  if (!eventType) {
    errors.eventType = "A purpose of travel is required.";
  }
  if (eventType?.requiresName && !purpose?.eventName?.trim()) {
    errors.eventName = `The ${eventType.displayName.toLowerCase()} name is required.`;
  }
  if (
    eventType?.requiresAdditionalPurpose &&
    !purpose?.additionalPurpose?.trim()
  ) {
    errors.additionalPurpose =
      "A description of your purpose of travel is required.";
  }

  return errors;
}
