import React from "react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import { useTravelApps } from "app/views/travel/application/history/useTravelApps";
import TravelApplicationResults from "app/views/travel/application/history/TravelApplicationResults";
import InputDebounced from "app/components/InputDebounced";
import {
  APPLICATION_HISTORY_SORT_OPTIONS,
  APPLICATION_HISTORY_STATUS_OPTIONS,
  useApplicationHistorySearchParams,
} from "app/views/travel/application/history/useApplicationHistorySearchParams";

export default function ApplicationHistory() {
  const { state, updateSearchParams } = useApplicationHistorySearchParams();

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
              max={state.toDate}
              onChange={(value) => {
                if (value !== state.fromDate) {
                  updateSearchParams({
                    fromDate: value,
                    offset: 1,
                  });
                }
              }}
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
              min={state.fromDate}
              onChange={(value) => {
                if (value !== state.toDate) {
                  updateSearchParams({
                    toDate: value,
                    offset: 1,
                  });
                }
              }}
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
                updateSearchParams(
                  { status: event.target.value, offset: 1 },
                  { replace: false },
                )
              }
            >
              {APPLICATION_HISTORY_STATUS_OPTIONS.map(({ value, label }) => (
                <option key={value} value={value}>
                  {label}
                </option>
              ))}
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
                updateSearchParams(
                  { sort: event.target.value, offset: 1 },
                  { replace: false },
                )
              }
            >
              {APPLICATION_HISTORY_SORT_OPTIONS.map(({ value, label }) => (
                <option key={value} value={value}>
                  {label}
                </option>
              ))}
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
        onPageChange={(offset) =>
          updateSearchParams({ offset }, { replace: false })
        }
      />
    </div>
  );
}
