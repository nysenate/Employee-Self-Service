import { useCallback, useEffect, useMemo } from "react";
import { useSearchParams } from "react-router-dom";
import {
  readDateRangeSearchParams,
  writeDateRangeSearchParams,
} from "app/utils/dateRangeSearchParams";
import { DEFAULT_DATE_RANGE_PRESET } from "app/utils/dateRangeUtils";

const DEFAULT_FILTERS = {};
const MAX_LIMIT = 100;

function parsePositiveInteger(value, fallback, max = Number.MAX_SAFE_INTEGER) {
  if (value === null || value.trim() === "") {
    return fallback;
  }

  const parsed = Number(value);
  return Number.isInteger(parsed) && parsed >= 1 && parsed <= max
    ? parsed
    : fallback;
}

function readFilter(searchParams, key, config) {
  const value = searchParams.get(key) ?? config.defaultValue;
  return config.validValues.has(value) ? value : config.defaultValue;
}

/**
 * Synchronizes travel-history filters and pagination with the URL.
 * Invalid values are replaced with defaults and canonicalized in the URL.
 *
 * @param {object} options
 * @param {number} options.defaultLimit Default page size; URL values are capped at 100.
 * @param {Object<string, {defaultValue: string, validValues: Set<string>, persistDefault?: boolean}>} [options.filters]
 * Additional validated query parameters. `persistDefault` keeps a default value in the URL.
 * @returns {{state: object, hasActiveFilters: boolean, resetFilters: Function,
 * updateDateRange: Function, updateSearchParams: Function}}
 */
export function useTravelHistorySearchParams({
  defaultLimit,
  filters = DEFAULT_FILTERS,
}) {
  const [searchParams, setSearchParams] = useSearchParams();
  const state = useMemo(() => {
    const filterState = Object.fromEntries(
      Object.entries(filters).map(([key, config]) => [
        key,
        readFilter(searchParams, key, config),
      ]),
    );

    return {
      dateRange: readDateRangeSearchParams(searchParams),
      limit: parsePositiveInteger(
        searchParams.get("limit"),
        defaultLimit,
        MAX_LIMIT,
      ),
      offset: parsePositiveInteger(searchParams.get("offset"), 1),
      ...filterState,
    };
  }, [defaultLimit, filters, searchParams]);

  useEffect(() => {
    const canonicalParams = writeDateRangeSearchParams(
      searchParams,
      state.dateRange,
    );

    canonicalParams.set("limit", state.limit.toString());
    canonicalParams.set("offset", state.offset.toString());

    Object.entries(filters).forEach(([key, config]) => {
      const value = state[key];
      if (config.persistDefault || value !== config.defaultValue) {
        canonicalParams.set(key, value);
      } else {
        canonicalParams.delete(key);
      }
    });

    if (canonicalParams.toString() !== searchParams.toString()) {
      setSearchParams(canonicalParams, { replace: true });
    }
  }, [filters, searchParams, setSearchParams, state]);

  const updateSearchParams = useCallback(
    (updates, { replace = true } = {}) => {
      setSearchParams(
        (currentParams) => {
          const nextParams = new URLSearchParams(currentParams);

          Object.entries(updates).forEach(([key, value]) => {
            if (value === undefined || value === null || value === "") {
              nextParams.delete(key);
            } else {
              nextParams.set(key, value.toString());
            }
          });

          return nextParams;
        },
        { replace },
      );
    },
    [setSearchParams],
  );

  const updateDateRange = useCallback(
    (dateRange, { replace = true } = {}) => {
      setSearchParams(
        (currentParams) => {
          const nextParams = writeDateRangeSearchParams(
            currentParams,
            dateRange,
          );

          nextParams.set("offset", "1");
          return nextParams;
        },
        { replace },
      );
    },
    [setSearchParams],
  );

  const resetFilters = useCallback(
    ({ replace = false } = {}) => {
      setSearchParams(
        (currentParams) => {
          const nextParams = new URLSearchParams(currentParams);

          nextParams.delete("range");
          nextParams.delete("fromDate");
          nextParams.delete("toDate");
          nextParams.set("offset", "1");

          Object.entries(filters).forEach(([key, config]) => {
            if (config.persistDefault) {
              nextParams.set(key, config.defaultValue);
            } else {
              nextParams.delete(key);
            }
          });

          return nextParams;
        },
        { replace },
      );
    },
    [filters, setSearchParams],
  );

  const hasActiveFilters =
    Object.entries(filters).some(
      ([key, config]) => state[key] !== config.defaultValue,
    ) ||
    state.dateRange.selection.type !== "preset" ||
    state.dateRange.selection.preset !== DEFAULT_DATE_RANGE_PRESET;

  return {
    state,
    hasActiveFilters,
    resetFilters,
    updateDateRange,
    updateSearchParams,
  };
}
