import React, { useEffect } from "react";
import { ArrowUpDown, ListFilter } from "lucide-react";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";
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
import { resolveTravelResultsStatus } from "app/views/travel/shared/travelResultsStatus";
import TravelApplicationModal from "app/views/travel/application/history/TravelApplicationModal";
import {
  parseApplicationId,
  resubmitUrl,
} from "app/views/travel/application/workflow/applicationRoutes";

export default function ApplicationHistory() {
  const location = useLocation();
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const selectedAppId = parseApplicationId(searchParams.get("appId"));
  const {
    state,
    hasActiveFilters,
    resetFilters,
    updateDateRange,
    updateSearchParams,
  } = useApplicationHistorySearchParams();

  const appQuery = useTravelApps({
    from: state.dateRange.fromDate,
    to: state.dateRange.toDate,
    status: state.status,
    sort: state.sort,
    limit: state.limit,
    offset: state.offset,
  });

  const apps = Array.isArray(appQuery.data?.result) ? appQuery.data.result : [];
  const resultsStatus = resolveTravelResultsStatus({
    isFetching: appQuery.isFetching && !appQuery.isPending,
    isPlaceholderData: appQuery.isPlaceholderData,
  });

  useEffect(() => {
    const total = appQuery.data?.total;
    if (
      !appQuery.isSuccess ||
      appQuery.isPlaceholderData ||
      appQuery.isFetching ||
      !Number.isFinite(total) ||
      total < 0 ||
      state.offset <= 1 ||
      state.offset - 1 < total
    ) {
      return;
    }
    const nextSearch = new URLSearchParams(location.search);
    nextSearch.set("offset", "1");
    navigate(`${location.pathname}?${nextSearch}`, {
      replace: true,
      state: location.state,
    });
  }, [
    appQuery.data,
    appQuery.isFetching,
    appQuery.isPlaceholderData,
    appQuery.isSuccess,
    location.pathname,
    location.search,
    location.state,
    navigate,
    state.offset,
  ]);

  const selectApplication = (appId) => {
    setSearchParams(
      (current) => {
        const next = new URLSearchParams(current);
        next.set("appId", String(appId));
        return next;
      },
      { replace: false },
    );
  };

  const closeApplication = () => {
    setSearchParams(
      (current) => {
        const next = new URLSearchParams(current);
        next.delete("appId");
        return next;
      },
      { replace: true },
    );
  };

  const resubmitApplication = (appId) => {
    navigate(resubmitUrl(appId), {
      state: { returnTo: `${location.pathname}${location.search}` },
    });
  };

  return (
    <div>
      <Hero>Travel Application History</Hero>
      <Controls>
        <div className="mb-3 text-center text-gray-600">
          View your previously submitted travel applications.
        </div>
        <div className="my-3 flex flex-wrap items-start gap-3 px-4">
          <DateRangeFilter
            label="Travel dates"
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
        status={resultsStatus}
        limit={state.limit}
        offset={state.offset}
        total={appQuery.data?.total ?? 0}
        onResetFilters={
          hasActiveFilters ? () => resetFilters({ replace: false }) : undefined
        }
        onPageChange={(offset) =>
          updateSearchParams({ offset }, { replace: false })
        }
        onSelectApp={selectApplication}
      />
      <TravelApplicationModal
        appId={selectedAppId}
        onClose={closeApplication}
        onResubmit={resubmitApplication}
      />
    </div>
  );
}
