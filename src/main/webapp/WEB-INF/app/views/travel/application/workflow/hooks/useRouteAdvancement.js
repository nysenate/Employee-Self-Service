import { useRef, useState } from "react";
import { useAddressCounty } from "app/views/travel/shared/hooks/useAddressCounty";
import { useCalculateTravelRoute } from "../../submit/hooks/useRouteMutations";
import {
  isLongTrip,
  normalizeCompleteRoute,
  normalizeOutboundRoute,
  validateOutboundRoute,
  validateReturnRoute,
} from "../../submit/routeValidation";
import {
  findMissingOutboundCounty,
  findMissingRouteCounty,
  setOutboundAddressCounty,
  setRouteAddressCounty,
  toRouteDto,
} from "../../submit/routeModel";
import { needsRouteRecalculation } from "../workflowReducer";

/**
 * Owns route validation, county prompts, long-trip confirmation, and calculation.
 * The workflow owns the draft and handles calculated results, saving, and navigation.
 * onComplete must handle persistence errors so they remain distinct from route errors.
 */
export function useRouteAdvancement({
  state,
  dispatch,
  reportValidationResult,
  onCalculated,
  onComplete,
}) {
  const [routeErrors, setRouteErrors] = useState({});
  const [pendingCounty, setPendingCounty] = useState(null);
  const [isAdvancingRoute, setIsAdvancingRoute] = useState(false);
  const [pendingMessage, setPendingMessage] = useState(null);
  const [pendingReturnAction, setPendingReturnAction] = useState(null);
  const [routeCalculationError, setRouteCalculationError] = useState(null);
  const routeAdvancePendingRef = useRef(false);
  const calculateRoute = useCalculateTravelRoute();
  const addressCounty = useAddressCounty();
  const routeNeedsRecalculation = needsRouteRecalculation(state);

  async function advanceOutboundStep() {
    const errors = validateOutboundRoute(state.dirtyRoute);
    setRouteErrors(errors);
    if (!reportValidationResult(errors)) return;
    await advanceOutbound(normalizeOutboundRoute(state.dirtyRoute));
  }

  async function runPending(action) {
    if (routeAdvancePendingRef.current) return;
    routeAdvancePendingRef.current = true;
    setIsAdvancingRoute(true);
    setPendingMessage("Preparing your route…");
    try {
      await action();
    } finally {
      routeAdvancePendingRef.current = false;
      setIsAdvancingRoute(false);
      setPendingMessage(null);
    }
  }

  async function advanceOutbound(initialRoute) {
    let route = initialRoute;
    let missingCounty = findMissingOutboundCounty(route);
    while (missingCounty) {
      const { index, direction, addressField } = missingCounty;
      const county = await lookupCounty(addressField.address);
      if (!county) {
        dispatch({ type: "UPDATE_DIRTY_ROUTE", route });
        setPendingCounty({
          index,
          direction,
          addressText: addressField.addressText,
        });
        return;
      }
      route = setOutboundAddressCounty(route, index, direction, county);
      missingCounty = findMissingOutboundCounty(route);
    }
    dispatch({ type: "UPDATE_DIRTY_ROUTE", route });
    await onComplete("next");
  }

  async function lookupCounty(address) {
    setPendingMessage("Checking route locations…");
    try {
      return await addressCounty.mutateAsync(address);
    } catch {
      return "";
    }
  }

  async function submitCounty(county) {
    if (!pendingCounty) return;
    if (pendingCounty.routePart) {
      const route = setRouteAddressCounty(
        state.dirtyRoute,
        pendingCounty,
        county,
      );
      const action = pendingCounty.returnAction;
      dispatch({ type: "UPDATE_DIRTY_ROUTE", route });
      setPendingCounty(null);
      await continueReturn(route, action);
      return;
    }
    const route = setOutboundAddressCounty(
      state.dirtyRoute,
      pendingCounty.index,
      pendingCounty.direction,
      county,
    );
    dispatch({ type: "UPDATE_DIRTY_ROUTE", route });
    setPendingCounty(null);
    await advanceOutbound(route);
  }

  async function prepareReturnAction(action) {
    setRouteCalculationError(null);
    const errors = validateReturnRoute(state.dirtyRoute);
    setRouteErrors(errors);
    if (!reportValidationResult(errors)) return;
    await continueReturn(normalizeCompleteRoute(state.dirtyRoute), action);
  }

  async function continueReturn(route, action) {
    const completeRoute = await resolveReturnCounties(route, action);
    if (!completeRoute) return;
    dispatch({ type: "UPDATE_DIRTY_ROUTE", route: completeRoute });
    if (routeNeedsRecalculation && isLongTrip(completeRoute)) {
      setPendingReturnAction({ action, route: completeRoute });
      return;
    }
    await completeReturnAction(action, completeRoute);
  }

  async function resolveReturnCounties(initialRoute, action) {
    let route = initialRoute;
    let missingCounty = findMissingRouteCounty(route);
    while (missingCounty) {
      const county = await lookupCounty(missingCounty.addressField.address);
      if (!county) {
        dispatch({ type: "UPDATE_DIRTY_ROUTE", route });
        setPendingCounty({
          ...missingCounty,
          returnAction: action,
          addressText: missingCounty.addressField.addressText,
        });
        return null;
      }
      route = setRouteAddressCounty(route, missingCounty, county);
      missingCounty = findMissingRouteCounty(route);
    }
    return route;
  }

  async function completeReturnAction(action, route) {
    setRouteCalculationError(null);
    try {
      let draftToUse = state.workingDraft;
      if (routeNeedsRecalculation) {
        const draftWithRoute = {
          ...state.workingDraft,
          amendment: {
            ...state.workingDraft.amendment,
            route: toRouteDto(route),
          },
        };
        setPendingMessage("Calculating your route…");
        draftToUse = await calculateRoute.mutateAsync(draftWithRoute);
        onCalculated(draftToUse);
      }
      await onComplete(action, draftToUse);
    } catch (error) {
      setRouteCalculationError(routeErrorMessage(error));
    }
  }

  function updateRoute(route) {
    setRouteErrors({});
    dispatch({ type: "UPDATE_DIRTY_ROUTE", route });
  }

  async function confirmLongTrip() {
    const pending = pendingReturnAction;
    setPendingReturnAction(null);
    if (pending) await completeReturnAction(pending.action, pending.route);
  }

  return {
    errors: routeErrors,
    pendingCounty,
    isPending: isAdvancingRoute,
    pendingMessage,
    calculationError: routeCalculationError,
    showLongTripWarning: Boolean(pendingReturnAction),
    advanceOutbound: () => runPending(advanceOutboundStep),
    advanceReturn: (action) => runPending(() => prepareReturnAction(action)),
    submitCounty: (county) => runPending(() => submitCounty(county)),
    cancelCounty: () => setPendingCounty(null),
    confirmLongTrip: () => runPending(confirmLongTrip),
    reviewLongTrip: () => setPendingReturnAction(null),
    updateRoute,
  };
}

function routeErrorMessage(error) {
  const serialized = JSON.stringify(error?.data ?? {});
  if (
    error?.response?.status === 502 ||
    serialized.includes("DATA_PROVIDER_ERROR")
  ) {
    return "A third-party travel service is unavailable. Your route was not calculated; please try again.";
  }
  if (
    error?.response?.status === 422 ||
    serialized.includes("MEAL_RATES_UNAVAILABLE")
  ) {
    const date = error?.data?.errorData;
    return `${date ? `Your travel date ${date} is` : "One or more of your travel dates are"} outside the range we currently have meal rates for. Meal rates are published once a year for the federal fiscal year beginning October 1; please choose an earlier date or try again tomorrow.`;
  }
  if (
    error?.response?.status === 400 ||
    serialized.includes("INVALID_TRAVEL_DATES")
  ) {
    return "One or more outbound or return dates must be corrected.";
  }
  return "Your route could not be calculated. Your entered information is still available; please try again.";
}
