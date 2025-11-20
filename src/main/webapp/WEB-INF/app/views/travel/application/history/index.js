import React, { useEffect, useMemo } from "react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import { endOfDay, formatISO, startOfDay, subMonths } from "date-fns";
import { useSearchParams } from "react-router-dom";
import { Label } from "app/components/ui/label";
import { UTCDate } from "@date-fns/utc";
import { useTravelApps } from "app/views/travel/application/history/useTravelApps";
import TravelApplicationResults from "app/views/travel/application/history/TravelApplicationResults";
import InputDebounced from "app/components/InputDebounced";

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
    from: formatISO(startOfDay(new UTCDate(state.fromDate))),
    to: formatISO(endOfDay(new UTCDate(state.toDate))),
    limit: state.limit,
    offset: state.offset,
  });

  const apps = Array.isArray(appQuery.data?.result) ? appQuery.data.result : [];

  const filteredApps = useMemo(() => {
    if (!state.fromDate || !state.toDate) {
      return apps;
    }

    const from = startOfDay(new UTCDate(state.fromDate));
    const to = endOfDay(new UTCDate(state.toDate));

    return apps.filter((app) => {
      const travelDate = app?.startDate;
      if (!travelDate) {
        return false;
      }
      const travelDay = new UTCDate(travelDate);
      return travelDay >= from && travelDay <= to;
    });
  }, [apps, state.fromDate, state.toDate]);

  return (
    <div>
      <Hero>Travel Application History</Hero>
      <Controls>
        <div className="flex gap-3 p-4">
          <div className="grid gap-1">
            <Label htmlFor="fromDate">From Date</Label>
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
            <Label htmlFor="toDate">To Date</Label>
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
        </div>
      </Controls>
      <TravelApplicationResults
        apps={filteredApps}
        isLoading={appQuery.isPending}
      />
    </div>
  );
}
