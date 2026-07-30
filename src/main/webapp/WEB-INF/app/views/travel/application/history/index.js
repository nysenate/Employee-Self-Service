import React from "react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import { useTravelApps } from "app/views/travel/application/history/useTravelApps";
import TravelApplicationResults from "app/views/travel/application/history/TravelApplicationResults";
import DateRangeFilter from "app/components/DateRangeFilter";
import {
  APPLICATION_HISTORY_SORT_OPTIONS,
  APPLICATION_HISTORY_STATUS_OPTIONS,
  useApplicationHistorySearchParams,
} from "app/views/travel/application/history/useApplicationHistorySearchParams";

export default function ApplicationHistory() {
  const { state, updateDateRange, updateSearchParams } =
    useApplicationHistorySearchParams();

  const appQuery = useTravelApps({
    from: state.dateRange.fromDate,
    to: state.dateRange.toDate,
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
        <div className="flex flex-wrap items-start gap-3 px-4 py-3">
          <DateRangeFilter
            value={state.dateRange}
            onChange={(dateRange) =>
              updateDateRange(dateRange, { replace: false })
            }
          />
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
