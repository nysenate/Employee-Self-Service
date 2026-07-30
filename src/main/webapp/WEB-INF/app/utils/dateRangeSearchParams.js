import { isValid, parseISO } from "date-fns";
import {
  createCustomDateRange,
  DATE_RANGE_PRESETS,
  DEFAULT_DATE_RANGE_PRESET,
  isDateRangePreset,
  resolveDateRangePreset,
} from "app/utils/dateRangeUtils";

export function readDateRangeSearchParams(
  searchParams,
  {
    defaultPreset = DEFAULT_DATE_RANGE_PRESET,
    presets = DATE_RANGE_PRESETS,
    today = new Date(),
  } = {},
) {
  const range = searchParams.get("range");
  let fromDate = parseDate(searchParams.get("fromDate"));
  let toDate = parseDate(searchParams.get("toDate"));

  if (range === "custom" || (!range && fromDate && toDate)) {
    if (fromDate && toDate) {
      if (fromDate > toDate) {
        [fromDate, toDate] = [toDate, fromDate];
      }

      return createCustomDateRange(fromDate, toDate);
    }
  } else if (isDateRangePreset(range, presets)) {
    return resolveDateRangePreset(range, today, presets);
  }

  return resolveDateRangePreset(defaultPreset, today, presets);
}

export function writeDateRangeSearchParams(
  searchParams,
  dateRange,
  {
    defaultPreset = DEFAULT_DATE_RANGE_PRESET,
    presets = DATE_RANGE_PRESETS,
  } = {},
) {
  const params = new URLSearchParams(searchParams);

  params.delete("range");
  params.delete("fromDate");
  params.delete("toDate");

  if (dateRange.selection.type === "custom") {
    params.set("range", "custom");
    params.set("fromDate", dateRange.fromDate);
    params.set("toDate", dateRange.toDate);
  } else {
    const preset = dateRange.selection.preset;

    if (!isDateRangePreset(preset, presets)) {
      throw new Error(`Unknown date range preset: ${preset}`);
    }

    if (preset !== defaultPreset) {
      params.set("range", preset);
    }
  }

  return params;
}

function parseDate(value) {
  if (!value || !/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    return null;
  }

  return isValid(parseISO(value)) ? value : null;
}
