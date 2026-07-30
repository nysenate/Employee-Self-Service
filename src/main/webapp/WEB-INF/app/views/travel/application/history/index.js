import React from "react";
import { ArrowUpDown, ListFilter } from "lucide-react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import SingleSelectFilter from "app/components/SingleSelectFilter";
import { useTravelApps } from "app/views/travel/application/history/useTravelApps";
import TravelApplicationResults from "app/views/travel/application/history/TravelApplicationResults";
import DateRangeFilter from "app/components/DateRangeFilter";
import { useApplicationHistorySearchParams } from "app/views/travel/application/history/useApplicationHistorySearchParams";
import {
  SORT_FILTER_OPTIONS,
  STATUS_FILTER_OPTIONS,
} from "app/views/travel/application/history/historyFilterOptions";

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
          <SingleSelectFilter
            label="Status"
            value={state.status}
            options={STATUS_FILTER_OPTIONS}
            icon={ListFilter}
            onChange={(status) =>
              updateSearchParams({ status, offset: 1 }, { replace: false })
            }
          />
          <SingleSelectFilter
            label="Sort"
            value={state.sort}
            options={SORT_FILTER_OPTIONS}
            icon={ArrowUpDown}
            className="w-52"
            onChange={(sort) =>
              updateSearchParams({ sort, offset: 1 }, { replace: false })
            }
          />
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
