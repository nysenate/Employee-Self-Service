import React, { useEffect, useReducer, useState } from "react";
import { endOfDay, formatISO, isValid, startOfDay, subMonths } from "date-fns";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import { useRequisitionSearch } from "app/views/supply/useRequisitionSearch";
import LoadingIndicator from "app/components/LoadingIndicator";
import RequisitionHistoryResults from "app/views/supply/requisition-history/RequisitionHistoryResults";
import { isValidDateString } from "app/utils/dateUtils";
import { UTCDate } from "@date-fns/utc";
import { useSearchParams } from "react-router-dom";
import {
  SET_DATE_RANGE,
  SET_FILTER,
  SET_OFFSET,
} from "app/views/supply/SupplyFilterActions";
import RequisitionHistoryFilters from "app/views/supply/requisition-history/RequisitionHistoryFilters";

const initialFilters = {
  fromDate: formatISO(subMonths(new Date(), 1), { representation: "date" }),
  toDate: formatISO(new Date(), { representation: "date" }),
  destinationId: null,
  itemId: null,
  issuerId: null,
  limit: 16,
  offset: 1,
};

function filtersReducer(state, action) {
  switch (action.type) {
    case SET_FILTER:
      return {
        ...state,
        [action.filter]: action.value,
        offset: 1,
      };
    case SET_DATE_RANGE:
      // If dates are invalid, don't update the state
      if (
        !isValidDateString(action.fromDate) ||
        !isValidDateString(action.toDate)
      ) {
        return state;
      }
      return {
        ...state,
        fromDate: action.fromDate,
        toDate: action.toDate,
        offset: 1,
      };
    case SET_OFFSET:
      return {
        ...state,
        offset: action.offset,
      };
    default:
      return state;
  }
}

export default function RequisitionHistoryIndex() {
  let [searchParams, setSearchParams] = useSearchParams();
  const [filters, dispatch] = useReducer(
    filtersReducer,
    initialFilters,
    initializeFilters,
  );
  const requisitionQuery = useRequisitionSearch({
    from: formatISO(startOfDay(new UTCDate(filters.fromDate))),
    to: formatISO(endOfDay(new UTCDate(filters.toDate))),
    location: filters.destinationId,
    itemId: filters.itemId,
    issuerId: filters.issuerId,
    limit: filters.limit,
    offset: filters.offset,
    status: ["APPROVED", "REJECTED"],
  });

  function initializeFilters(initFilters) {
    const urlFilters = {
      fromDate: searchParams.get("fromDate"),
      toDate: searchParams.get("toDate"),
      destinationId: searchParams.get("destinationId"),
      itemId: searchParams.get("itemId"),
      issuerId: searchParams.get("issuerId"),
      limit: searchParams.get("limit"),
      offset: searchParams.get("offset"),
    };
    return {
      ...initFilters,
      ...Object.fromEntries(
        Object.entries(urlFilters).filter(([_, v]) => v !== null),
      ),
    };
  }

  useEffect(() => {
    // Don't add null or undefined filters to the search params.
    const filteredParams = Object.fromEntries(
      Object.entries(filters).filter(
        ([, value]) => value !== null && value !== undefined,
      ),
    );
    setSearchParams(filteredParams, { replace: true });
  }, [filters]);

  return (
    <div>
      <Hero>Requisition History</Hero>
      <Controls>
        <div className="p-4">
          <RequisitionHistoryFilters filters={filters} dispatch={dispatch} />
        </div>
      </Controls>
      {requisitionQuery.isPending ? (
        <LoadingIndicator />
      ) : (
        <div className="mt-6">
          <RequisitionHistoryResults
            data={requisitionQuery.data}
            filters={filters}
            dispatch={dispatch}
          />
        </div>
      )}
    </div>
  );
}
