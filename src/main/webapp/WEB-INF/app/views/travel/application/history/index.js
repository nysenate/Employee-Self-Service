import React, { useEffect, useMemo } from "react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import { formatISO, subMonths } from "date-fns";
import { useSearchParams } from "react-router-dom";
import { useTravelApps } from "app/views/travel/application/history/useTravelApps";
import TravelApplicationResults from "app/views/travel/application/history/TravelApplicationResults";
import InputDebounced from "app/components/InputDebounced";

const initialState = {
  fromDate: formatISO(subMonths(new Date(), 1), { representation: "date" }),
  toDate: formatISO(new Date(), { representation: "date" }),
  limit: 16,
  offset: 1,
  status: "",
  sort: "startDate:desc",
};

function fromSearchParams(searchParams) {
  return {
    fromDate: searchParams.get("fromDate") ?? initialState.fromDate,
    toDate: searchParams.get("toDate") ?? initialState.toDate,
    limit: Number(searchParams.get("limit") ?? initialState.limit),
    offset: Number(searchParams.get("offset") ?? initialState.offset),
    status: searchParams.get("status") ?? initialState.status,
    sort: searchParams.get("sort") ?? initialState.sort,
  };
}

export default function ApplicationHistory() {
  const [searchParams, setSearchParams] = useSearchParams();
  const state = useMemo(() => fromSearchParams(searchParams), [searchParams]);

  // Push default values to the URL if not present.
  useEffect(() => {
    const params = new URLSearchParams(searchParams);
    let changed = false;

    [
      ["fromDate", state.fromDate],
      ["toDate", state.toDate],
      ["limit", state.limit],
      ["offset", state.offset],
      ["sort", state.sort],
    ].forEach(([key, value]) => {
      if (!params.get(key) && value != null) {
        params.set(key, value.toString());
        changed = true;
      }
    });

    if (changed) {
      setSearchParams(params, { replace: true });
    }
  }, [searchParams, setSearchParams, state]);

  const updateSearchParams = (updates) => {
    const params = new URLSearchParams(searchParams);
    Object.entries(updates).forEach(([key, value]) => {
      if (value === undefined || value === null || value === "") {
        params.delete(key);
      } else {
        params.set(key, value.toString());
      }
    });
    setSearchParams(params, { replace: true });
  };

  const appQuery = useTravelApps({
    from: state.fromDate,
    to: state.toDate,
    status: state.status,
    sort: state.sort,
    limit: state.limit,
    offset: state.offset,
  });

  const apps = Array.isArray(appQuery.data?.result) ? appQuery.data.result : [];

  return (
    <div>
      <Hero>Travel Application History</Hero>
      <Controls>
        <div className="flex flex-wrap gap-3 px-4 py-3">
          <div className="grid gap-1">
            <label className="text-sm font-semibold" htmlFor="fromDate">
              From Date
            </label>
            <InputDebounced
              id="fromDate"
              type="date"
              value={state.fromDate}
              onChange={(value) =>
                updateSearchParams({
                  fromDate: value,
                  offset: 1,
                })
              }
            />
          </div>
          <div className="grid gap-1">
            <label className="text-sm font-semibold" htmlFor="toDate">
              To Date
            </label>
            <InputDebounced
              id="toDate"
              type="date"
              value={state.toDate}
              onChange={(value) =>
                updateSearchParams({
                  toDate: value,
                  offset: 1,
                })
              }
            />
          </div>
          <div className="grid gap-1">
            <label className="text-sm font-semibold" htmlFor="status">
              Status
            </label>
            <select
              id="status"
              className="select"
              value={state.status}
              onChange={(event) =>
                updateSearchParams({ status: event.target.value, offset: 1 })
              }
            >
              <option value="">All statuses</option>
              <option value="DEPARTMENT_HEAD">Department Head</option>
              <option value="TRAVEL_UNIT">Travel Unit</option>
              <option value="APPROVED">Approved</option>
              <option value="DISAPPROVED">Disapproved</option>
              <option value="CANCELED">Canceled</option>
              <option value="NOT_APPLICABLE">Not Applicable</option>
            </select>
          </div>
          <div className="grid gap-1">
            <label className="text-sm font-semibold" htmlFor="sort">
              Sort
            </label>
            <select
              id="sort"
              className="select"
              value={state.sort}
              onChange={(event) =>
                updateSearchParams({ sort: event.target.value, offset: 1 })
              }
            >
              <option value="startDate:desc">Start date: newest</option>
              <option value="startDate:asc">Start date: oldest</option>
              <option value="submittedDate:desc">Submitted: newest</option>
              <option value="submittedDate:asc">Submitted: oldest</option>
              <option value="status:asc">Status</option>
            </select>
          </div>
        </div>
      </Controls>
      <TravelApplicationResults
        apps={apps}
        isLoading={appQuery.isPending}
        limit={state.limit}
        offset={state.offset}
        total={appQuery.data?.total ?? 0}
        onPageChange={(offset) => updateSearchParams({ offset })}
      />
    </div>
  );
}
