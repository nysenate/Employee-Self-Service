export function createEmptyRoute() {
  return { outboundLegs: [], returnLegs: [] };
}

export function createOutboundLeg(origin = null, previousLeg = null) {
  return {
    from: previousLeg
      ? { ...previousLeg.to }
      : {
          address: origin,
          addressText: origin?.formattedAddressWithCounty ?? "",
        },
    to: { address: null, addressText: "" },
    travelDate: "",
    methodOfTravelDisplayName: previousLeg?.methodOfTravelDisplayName ?? "",
    methodOfTravelDescription: previousLeg?.methodOfTravelDescription ?? "",
  };
}

export function initializeOutboundRoute(route, origin) {
  if (route.outboundLegs.length > 0) return route;
  return { ...route, outboundLegs: [createOutboundLeg(origin)] };
}

export function addOutboundLeg(route) {
  const previousLeg = route.outboundLegs.at(-1);
  return {
    ...route,
    outboundLegs: [...route.outboundLegs, createOutboundLeg(null, previousLeg)],
  };
}

export function updateOutboundLeg(route, index, changes) {
  return {
    ...route,
    outboundLegs: route.outboundLegs.map((leg, legIndex) =>
      legIndex === index ? { ...leg, ...changes } : leg,
    ),
  };
}

export function removeLastOutboundLeg(route) {
  if (route.outboundLegs.length <= 1) return route;
  return { ...route, outboundLegs: route.outboundLegs.slice(0, -1) };
}

export function setOutboundAddressCounty(route, index, direction, county) {
  const leg = route.outboundLegs[index];
  return updateOutboundLeg(route, index, {
    [direction]: {
      ...leg[direction],
      address: { ...leg[direction].address, county },
    },
  });
}

export function findMissingOutboundCounty(route) {
  for (const [index, leg] of route.outboundLegs.entries()) {
    for (const direction of ["from", "to"]) {
      const addressField = leg[direction];
      if (!addressField.address.county?.trim()) {
        return { index, direction, addressField };
      }
    }
  }
  return null;
}
