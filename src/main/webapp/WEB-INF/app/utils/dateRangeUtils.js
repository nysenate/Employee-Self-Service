import { formatISO, startOfYear, subMonths } from "date-fns";

export const DEFAULT_DATE_RANGE_PRESET = "pastMonth";

export const DATE_RANGE_PRESETS = [
  {
    value: "pastMonth",
    label: "Past month",
    resolve: (today) => rangeFrom(subMonths(today, 1), today),
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
    value: "yearToDate",
    label: "Year to date",
    resolve: (today) => rangeFrom(startOfYear(today), today),
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
