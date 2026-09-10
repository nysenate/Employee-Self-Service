import { endOfYear, formatISO, startOfYear, subMonths } from "date-fns";

export const DEFAULT_DATE_RANGE_PRESET = "pastThreeMonthsOnward";

export const DATE_RANGE_PRESETS = [
  {
    value: "pastThreeMonthsOnward",
    label: "Recent & future",
    resolve: (today) => ({
      fromDate: formatISO(subMonths(today, 3), { representation: "date" }),
      toDate: null,
    }),
  },
  {
    value: "pastThreeMonths",
    label: "Past 3 months",
    resolve: (today) => rangeFrom(subMonths(today, 3), today),
  },
  {
    value: "pastTwelveMonths",
    label: "Past 12 months",
    resolve: (today) => rangeFrom(subMonths(today, 12), today),
  },
  {
    value: "thisYear",
    label: "This year",
    resolve: (today) => rangeFrom(startOfYear(today), endOfYear(today)),
  },
  {
    value: "allTime",
    label: "All time",
    resolve: () => ({ fromDate: null, toDate: null }),
  },
];

export function isDateRangePreset(value, presets = DATE_RANGE_PRESETS) {
  return presets.some((preset) => preset.value === value);
}

export function createCustomDateRange(fromDate, toDate) {
  return {
    fromDate,
    toDate,
    selection: {
      type: "custom",
    },
  };
}

export function resolveDateRangePreset(
  presetValue,
  today = new Date(),
  presets = DATE_RANGE_PRESETS,
) {
  const preset = presets.find(({ value }) => value === presetValue);

  if (!preset) {
    throw new Error(`Unknown date range preset: ${presetValue}`);
  }

  return {
    ...preset.resolve(today),
    selection: {
      type: "preset",
      preset: presetValue,
    },
  };
}

function rangeFrom(fromDate, toDate) {
  return {
    fromDate: formatISO(fromDate, { representation: "date" }),
    toDate: formatISO(toDate, { representation: "date" }),
  };
}
