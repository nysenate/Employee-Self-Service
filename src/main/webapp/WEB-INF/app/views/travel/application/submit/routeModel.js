export function createEmptyRoute() {
  return { outboundLegs: [], returnLegs: [] };
}

export function toEditableRoute(route = createEmptyRoute()) {
  const hydrate = (legs = []) =>
    legs.map((leg) => ({
      ...leg,
      from: {
        ...leg.from,
        addressText:
          leg.from?.addressText ??
          leg.from?.address?.formattedAddressWithCounty ??
          "",
      },
      to: {
        ...leg.to,
        addressText:
          leg.to?.addressText ??
          leg.to?.address?.formattedAddressWithCounty ??
          "",
      },
    }));
  return {
    ...route,
    outboundLegs: hydrate(route.outboundLegs),
    returnLegs: hydrate(route.returnLegs),
  };
}

export function toRouteDto(route) {
  const strip = (legs = []) =>
    legs.map((leg) => {
      const from = { ...leg.from };
      const to = { ...leg.to };
      delete from.addressText;
      delete to.addressText;
      return { ...leg, from, to };
    });
  return {
    ...route,
    outboundLegs: strip(route.outboundLegs),
    returnLegs: strip(route.returnLegs),
  };
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

export function createReturnLeg(route, previousLeg = null) {
  const outboundOrigin = route.outboundLegs[0]?.from ?? {
    address: null,
    addressText: "",
  };
  const finalOutboundDestination = route.outboundLegs.at(-1)?.to ?? {
    address: null,
    addressText: "",
  };
  const outboundModes = new Set(
    route.outboundLegs
      .map((leg) => leg.methodOfTravelDisplayName)
      .filter(Boolean),
  );
  const inferredLeg = outboundModes.size === 1 ? route.outboundLegs[0] : null;
  const modeSource = previousLeg ?? inferredLeg;
  const usesOther = modeSource?.methodOfTravelDisplayName === "Other";

  return {
    from: { ...(previousLeg?.to ?? finalOutboundDestination) },
    to: { ...outboundOrigin },
    travelDate: "",
    methodOfTravelDisplayName: modeSource?.methodOfTravelDisplayName ?? "",
    methodOfTravelDescription: usesOther
      ? (modeSource.methodOfTravelDescription ?? "")
      : "",
  };
}

export function initializeReturnRoute(route) {
  if (route.returnLegs.length > 0) return route;
  return { ...route, returnLegs: [createReturnLeg(route)] };
}

export function addReturnLeg(route) {
  return {
    ...route,
    returnLegs: [
      ...route.returnLegs,
      createReturnLeg(route, route.returnLegs.at(-1)),
    ],
  };
}

export function updateReturnLeg(route, index, changes) {
  return {
    ...route,
    returnLegs: route.returnLegs.map((leg, legIndex) =>
      legIndex === index ? { ...leg, ...changes } : leg,
    ),
  };
}

export function removeLastReturnLeg(route) {
  if (route.returnLegs.length <= 1) return route;
  return { ...route, returnLegs: route.returnLegs.slice(0, -1) };
}

export function findMissingRouteCounty(route) {
  for (const routePart of ["outboundLegs", "returnLegs"]) {
    for (const [index, leg] of (route[routePart] ?? []).entries()) {
      for (const direction of ["from", "to"]) {
        const addressField = leg[direction];
        if (addressField?.address && !addressField.address.county?.trim()) {
          return { routePart, index, direction, addressField };
        }
      }
    }
  }
  return null;
}

export function setRouteAddressCounty(route, target, county) {
  const targetAddress = target.addressField.address;
  const matches = (address) =>
    address === targetAddress ||
    (address?.formattedAddressWithCounty &&
      address.formattedAddressWithCounty ===
        targetAddress.formattedAddressWithCounty);
  const updateLegs = (legs = []) =>
    legs.map((leg) => {
      const updated = { ...leg };
      for (const direction of ["from", "to"]) {
        if (matches(leg[direction]?.address)) {
          updated[direction] = {
            ...leg[direction],
            address: { ...leg[direction].address, county },
          };
        }
      }
      return updated;
    });
  return {
    ...route,
    outboundLegs: updateLegs(route.outboundLegs),
    returnLegs: updateLegs(route.returnLegs),
  };
}
