import React, { useEffect, useReducer } from "react";
import { endOfDay, formatISO, startOfDay, subMonths } from "date-fns";
import { useItemSummary } from "app/views/supply/item-history/useItemSummary";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import LoadingIndicator from "app/components/LoadingIndicator";
import {
  RESET_FILTERS,
  SET_DATE_RANGE,
  SET_FILTER,
  SET_OFFSET,
} from "app/views/supply/SupplyFilterActions";
import ItemSummaryFilters from "app/views/supply/item-history/ItemSummaryFilters";
import ItemSummaryResults from "app/views/supply/item-history/ItemSummaryResults";
import { isValidDateString } from "app/utils/dateUtils";
import { UTCDate } from "@date-fns/utc";
import { useSearchParams } from "react-router-dom";

const initialFilters = {
  fromDate: formatISO(subMonths(new Date(), 1), { representation: "date" }),
  toDate: formatISO(new Date(), { representation: "date" }),
  commodityCode: null,
  locationCode: null,
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
    case RESET_FILTERS:
      return {
        ...initialFilters,
      };
    default:
      return state;
  }
}

export default function ItemSummary() {
  let [searchParams, setSearchParams] = useSearchParams();
  const [filters, dispatch] = useReducer(
    filtersReducer,
    initialFilters,
    initializeFilters,
  );
  const itemSummaryQuery = useItemSummary({
    fromDateTime: formatISO(startOfDay(new UTCDate(filters.fromDate))),
    toDateTime: formatISO(endOfDay(new UTCDate(filters.toDate))),
    locationCode: filters.locationCode,
    commodityCode: filters.commodityCode,
  });

  function initializeFilters(initFilters) {
    const urlFilters = {
      fromDate: searchParams.get("fromDate"),
      toDate: searchParams.get("toDate"),
      commodityCode: searchParams.get("commodityCode"),
      locationCode: searchParams.get("locationCode"),
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
      <Hero>Item History</Hero>
      <Controls>
        <div className="p-4">
          <ItemSummaryFilters filters={filters} dispatch={dispatch} />
        </div>
      </Controls>
      {itemSummaryQuery.isPending ? (
        <LoadingIndicator />
      ) : (
        <div className="mt-6">
          <ItemSummaryResults
            itemSummaries={itemSummaryQuery.data}
            filters={filters}
            dispatch={dispatch}
          />
        </div>
      )}
    </div>
  );
}
