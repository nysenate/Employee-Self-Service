import React, { useReducer } from "react";
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
} from "app/views/supply/item-history/itemSummaryActions";
import ItemSummaryFilters from "app/views/supply/item-history/ItemSummaryFilters";
import ItemSummaryResults from "app/views/supply/item-history/ItemSummaryResults";
import { isValidDateString } from "app/utils/dateUtils";

const initialFilters = {
  commodityCode: null,
  locationCode: null,
  fromDate: formatISO(subMonths(new Date(), 1), { representation: "date" }),
  toDate: formatISO(new Date(), { representation: "date" }),
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
  const [filters, dispatch] = useReducer(filtersReducer, initialFilters);
  const itemSummaryQuery = useItemSummary({
    fromDateTime: formatISO(startOfDay(filters.fromDate)),
    toDateTime: formatISO(endOfDay(filters.toDate)),
    locationCode: filters.locationCode,
    commodityCode: filters.commodityCode,
  });

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
