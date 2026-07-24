import React from "react";
import { cn } from "app/utils/cn";

/** Keys that would let a non hour value into a number input. */
const RESTRICTED_KEYS = ["e", "E", "-", "+"];

/**
 * Entry table styles, kept to the proportions of the legacy .time-record-entry-table
 * (assets/css/less/time.less): narrow fixed width hour columns, a wide date column, and the
 * misc type column taking whatever is left over.
 */
export const ENTRY_HEAD_CELL =
  "w-[65px] px-1 py-2.5 text-center font-semibold whitespace-nowrap";
export const ENTRY_DATE_HEAD_CELL =
  "w-[150px] px-1 py-2.5 text-center font-semibold whitespace-nowrap";
export const ENTRY_ROW = "h-8 border-b border-gray-300 hover:bg-[#f3f6ff]";

/**
 * A cell of a time entry table.
 * An invalid cell is shaded rather than the input within it, as it was in the legacy table.
 */
export function EntryCell({ isInvalid, className, children }) {
  return (
    <td
      className={cn(
        "border-r border-gray-200 p-0 text-center",
        isInvalid && "bg-red-200",
        className,
      )}
    >
      {children}
    </td>
  );
}

/**
 * An hours field on a time entry.
 * The value is null when nothing has been entered, which displays as a "--" placeholder.
 *
 * @param value The entered hours, or null.
 * @param onChange Called with the new hours, or null when the field is cleared.
 */
export default function EntryHoursInput({
  value,
  onChange,
  step,
  min = 0,
  max,
  isDisabled,
  isReadOnly,
  placeholder = "--",
  tabIndex,
  name,
  onFocus,
  onBlur,
}) {
  return (
    <input
      type="number"
      className={cn(
        "h-9 w-full border-0 bg-transparent text-center text-lg",
        "[&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:appearance-none",
        isDisabled && "cursor-not-allowed bg-gray-100",
      )}
      value={value === null || value === undefined ? "" : value}
      onChange={(e) => onChange(parseHours(e.target.value))}
      onKeyDown={(e) => {
        if (RESTRICTED_KEYS.includes(e.key)) {
          e.preventDefault();
        }
      }}
      onFocus={onFocus}
      onBlur={onBlur}
      step={step}
      min={min}
      max={max}
      disabled={isDisabled}
      readOnly={isReadOnly}
      placeholder={placeholder}
      tabIndex={tabIndex}
      name={name}
    />
  );
}

/** Turns the text of an hours input into the value stored on the entry. */
function parseHours(inputValue) {
  if (inputValue === "") {
    return null;
  }
  const hours = parseFloat(inputValue);
  return isNaN(hours) ? null : hours;
}
