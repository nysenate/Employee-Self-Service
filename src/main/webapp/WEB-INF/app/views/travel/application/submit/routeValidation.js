export function normalizeTravelDate(value) {
  const match = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(value?.trim());
  if (!match) return null;
  const year = Number(match[3]);
  const month = Number(match[1]);
  const day = Number(match[2]);
  const date = new Date(Date.UTC(year, month - 1, day));
  if (
    date.getUTCFullYear() !== year ||
    date.getUTCMonth() !== month - 1 ||
    date.getUTCDate() !== day
  )
    return null;
  return `${String(month).padStart(2, "0")}/${String(day).padStart(2, "0")}/${year}`;
}

function validAddress(address) {
  return Boolean(address && String(address.zip5 ?? "").trim());
}

export function validateOutboundRoute(route) {
  const errors = {};
  (route.outboundLegs ?? []).forEach((leg, index) => {
    const prefix = `segment-${index}`;
    if (!leg.from?.addressText?.trim())
      errors[`${prefix}-from`] = "Origin address is required.";
    else if (!validAddress(leg.from.address))
      errors[`${prefix}-from`] =
        "Select a recognized origin address containing a ZIP code.";
    if (!leg.to?.addressText?.trim())
      errors[`${prefix}-to`] = "Destination address is required.";
    else if (!validAddress(leg.to.address))
      errors[`${prefix}-to`] =
        "Select a recognized destination address containing a ZIP code.";
    if (!leg.travelDate?.trim())
      errors[`${prefix}-date`] = "Travel date is required.";
    else if (!normalizeTravelDate(leg.travelDate))
      errors[`${prefix}-date`] = "Enter a valid travel date as MM/DD/YYYY.";
    if (!leg.methodOfTravelDisplayName)
      errors[`${prefix}-mode`] = "Mode of transportation is required.";
    else if (
      leg.methodOfTravelDisplayName === "Other" &&
      !leg.methodOfTravelDescription?.trim()
    )
      errors[`${prefix}-modeOther`] = "Specify the mode of transportation.";
  });
  return { ...errors, ...validateOutboundDestinations(route) };
}

export function validateOutboundDestinations(route) {
  const errors = {};
  (route.outboundLegs ?? []).forEach((leg, index) => {
    if (leg.to?.address && isOutsideConus(leg.to.address)) {
      errors[`segment-${index}-to`] =
        "Travel destinations must be within the continental United States.";
    }
  });
  return errors;
}

export function isOutsideConus(address) {
  const state = String(address?.state ?? "").toUpperCase();
  return (
    address?.country !== "United States" ||
    ["AK", "HI", "ALASKA", "HAWAII"].includes(state)
  );
}

export function normalizeOutboundRoute(route) {
  return {
    ...route,
    outboundLegs: route.outboundLegs.map((leg) => ({
      ...leg,
      travelDate: normalizeTravelDate(leg.travelDate),
    })),
  };
}
