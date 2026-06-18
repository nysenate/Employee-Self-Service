import React, { useEffect, useReducer, useState } from "react";
import { add, endOfDay, formatISO, startOfDay, subMonths } from "date-fns";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import { useEmployee } from "app/views/useEmployee";
import { useOrderHisotry } from "app/views/supply/orders/order-list/useOrderHistory";
import { UTCDate } from "@date-fns/utc";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import OrderHistoryFilters from "app/views/supply/orders/order-list/OrderHistoryFilters";
import {
  SET_DATE_RANGE,
  SET_FILTER,
  SET_OFFSET,
  setOffset,
} from "app/views/supply/shared/lib/supplyFilterActions";
import { isValidDateString } from "app/utils/dateUtils";
import LoadingIndicator from "app/components/LoadingIndicator";
import Card from "app/components/Card";
import OrderHistoryTable from "app/views/supply/orders/order-list/OrderHistoryTable";
import Pagination from "app/components/Pagination";
import NoMatchesFound from "app/components/NoMatchesFound";

const initialFilters = {
  from: formatISO(subMonths(new Date(), 1), { representation: "date" }),
  to: formatISO(new Date(), { representation: "date" }),
  status: ["PENDING", "PROCESSING", "COMPLETED", "APPROVED", "REJECTED"],
  limit: 12,
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
        console.error(
          `Dispatched SET_DATE_RANGE with invalid dates. Action: ${action}`,
        );
        return state;
      }
      return {
        ...state,
        from: action.fromDate,
        to: action.toDate,
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

export default function OrderHistoryIndex() {
  const { data: user } = useRequireAuthedUser();
  const [filters, dispatch] = useReducer(filtersReducer, initialFilters);
  const employeeQuery = useEmployee(user?.employeeId);
  const orderHistoryQuery = useOrderHisotry({
    ...filters,
    from: formatISO(startOfDay(new UTCDate(filters.from))),
    to: formatISO(endOfDay(new UTCDate(filters.to))),
    customerId: user.employeeId,
    location: employeeQuery.data?.empWorkLocation.locId,
  });

  return (
    <div>
      <Hero>Order History</Hero>
      <Controls>
        <div className="p-4">
          <OrderHistoryFilters filters={filters} dispatch={dispatch} />
        </div>
      </Controls>
      {orderHistoryQuery.isPending ? (
        <LoadingIndicator />
      ) : (
        OrderHistoryResults(orderHistoryQuery, filters, dispatch)
      )}
    </div>
  );
}

function OrderHistoryResults(orderHistoryQuery, filters, dispatch) {
  if (orderHistoryQuery.data.total == 0) {
    return <NoMatchesFound className="mt-6" />;
  }
  return (
    <Card className="mt-6">
      <div className="p-4">
        <OrderHistoryTable query={orderHistoryQuery} />
        <Pagination
          offset={filters.offset}
          limit={filters.limit}
          total={orderHistoryQuery.data.total}
          onPageChange={(offset) => dispatch(setOffset(offset))}
        />
      </div>
    </Card>
  );
}
