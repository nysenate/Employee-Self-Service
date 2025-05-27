import React, { useEffect, useReducer, useState } from "react";
import { endOfDay, formatISO, isValid, startOfDay, subMonths } from "date-fns";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import RequisitionHistoryFilters from "app/views/supply/requisition-history/RequisitionHistoryFilters";
import {
  SET_FILTER,
  SET_OFFSET,
} from "app/views/supply/requisition-history/RequisitionHistoryActions";
import { useRequisitionSearch } from "app/views/supply/useRequisitionSearch";
import LoadingIndicator from "app/components/LoadingIndicator";
import RequisitionHistoryResults from "app/views/supply/requisition-history/RequisitionHistoryResults";
import { SET_DATE_RANGE } from "app/views/supply/item-history/itemSummaryActions";
import { isValidDateString } from "app/utils/dateUtils";
import { UTCDate } from "@date-fns/utc";
import { useSearchParams } from "react-router-dom";

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
  const [filters, dispatch] = useReducer(filtersReducer, initialFilters);
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

  useEffect(() => {
    // setSearchParams(filters);
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
