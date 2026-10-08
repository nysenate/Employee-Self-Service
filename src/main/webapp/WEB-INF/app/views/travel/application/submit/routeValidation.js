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
  return {
    ...validateRouteLegs(route.outboundLegs),
    ...validateOutboundDestinations(route),
  };
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

export function validateReturnRoute(route) {
  const errors = validateRouteLegs(route.returnLegs);
  if (Object.keys(errors).length === 0) {
    const chronologyError = findRouteChronologyError(route);
    if (chronologyError) errors[chronologyError.field] = chronologyError.message;
  }
  return errors;
}

function validateRouteLegs(legs = []) {
  const errors = {};
  legs.forEach((leg, index) => {
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
  return errors;
}

export function normalizeCompleteRoute(route) {
  return {
    ...normalizeOutboundRoute(route),
    returnLegs: route.returnLegs.map((leg) => ({
      ...leg,
      travelDate: normalizeTravelDate(leg.travelDate),
    })),
  };
}

function findRouteChronologyError(route) {
  const segments = [
    ...(route.outboundLegs ?? []).map((leg, index) => ({
      leg,
      type: "Outbound",
      index,
    })),
    ...(route.returnLegs ?? []).map((leg, index) => ({
      leg,
      type: "Return",
      index,
    })),
  ];
  const normalized = segments.map(({ leg }) =>
    normalizeTravelDate(leg.travelDate),
  );
  if (normalized.some((date) => !date)) return null;

  for (let index = 1; index < segments.length; index += 1) {
    if (
      travelDateTime(normalized[index]) < travelDateTime(normalized[index - 1])
    ) {
      const current = segments[index];
      const previous = segments[index - 1];
      const previousLabel =
        current.type === "Return" && previous.type === "Outbound"
          ? "the final outbound date"
          : `${previous.type.toLowerCase()} segment ${previous.index + 1} date`;
      return {
        field:
          current.type === "Return"
            ? `segment-${current.index}-date`
            : "routeDates",
        message: `${current.type} segment ${current.index + 1} date (${normalized[index]}) cannot be before ${previousLabel} (${normalized[index - 1]}).${current.type === "Outbound" ? " Return to Outbound to correct the dates." : ""}`,
      };
    }
  }
  return null;
}

function travelDateTime(value) {
  const [month, day, year] = value.split("/").map(Number);
  return Date.UTC(year, month - 1, day);
}

export function isLongTrip(route) {
  const first = normalizeTravelDate(route.outboundLegs[0]?.travelDate);
  const last = normalizeTravelDate(route.returnLegs.at(-1)?.travelDate);
  if (!first || !last) return false;
  return (travelDateTime(last) - travelDateTime(first)) / 86400000 > 7;
}
