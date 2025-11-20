import React, { useMemo, useReducer } from "react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import { formatISO, subMonths } from "date-fns";
import { useSearchParams } from "react-router-dom";
import InputDebounced from "app/components/InputDebounced";
import { Label } from "app/components/ui/label";
import { Input } from "app/components/ui/input";

const initialState = {
  fromDate: formatISO(subMonths(new Date(), 1), { representation: "date" }),
  toDate: formatISO(new Date(), { representation: "date" }),
  limit: 16,
  offset: 1,
};

function fromSearchParams(searchParams) {
  return {
    fromDate: searchParams.get("fromDate") ?? initialState.fromDate,
    toDate: searchParams.get("toDate") ?? initialState.toDate,
    limit: Number(searchParams.get("limit") ?? initialState.limit),
    offset: Number(searchParams.get("offset") ?? initialState.offset),
  };
}

function appHistoryReducer(state, action) {
  switch (action.type) {
    case "SET_FILTER":
      return {
        ...state,
        [action.filter]: action.value,
        offset: 1,
      };
    case "SET_FROM_DATE":
      return {
        ...state,
        fromDate: action.value,
        offset: 1,
      };
    case "SET_TO_DATE":
      return {
        ...state,
        toDate: action.value,
        offset: 1,
      };
    default:
      return state;
  }
}

export default function ApplicationHistory() {
  const [searchParams, setSearchParams] = useSearchParams();
  const initial = useMemo(() => fromSearchParams(searchParams), []);
  const [state, dispatch] = useReducer(appHistoryReducer, initial);

  console.log(state);

  // TODO update state on query param change, i.e. forward/back navigation
  // TODO update query params on state change

  return (
    <div>
      <Hero>Travel Application History</Hero>
      <Controls>
        <div className="flex gap-3 p-4">
          <div>
            <label htmlFor="fromDate" className="font-semibold">
              From Date
            </label>
            <InputDebounced
              id="fromDate"
              value={state.fromDate}
              type="date"
              onChange={(value) => dispatch(setFromDate(value))}
            />
          </div>
          <div>
            <Label htmlFor="toDate">To Date</Label>
            <Input
              id="toDate"
              type="date"
              value={state.toDate}
              onChange={(value) => dispatch(setToDate(value))}
            />
          </div>
        </div>
      </Controls>
    </div>
  );
}

function setFromDate(fromDate) {
  return {
    type: "SET_FROM_DATE",
    value: fromDate,
  };
}

function setToDate(toDate) {
  return {
    type: "SET_TO_DATE",
    value: toDate,
  };
}
