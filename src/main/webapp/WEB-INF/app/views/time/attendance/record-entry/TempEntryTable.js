import React from "react";
import { format, parseISO } from "date-fns";
import { cn } from "app/utils/cn";
import EntryHoursInput, {
  EntryCell,
  ENTRY_DATE_HEAD_CELL,
  ENTRY_HEAD_CELL,
  ENTRY_ROW,
} from "app/views/time/attendance/record-entry/EntryHoursInput";

/** The comment describing when the hours were worked is limited to this many characters. */
const MAX_COMMENT_LENGTH = 150;

/**
 * The time entry table for temporary pay, where hours must be described by a comment.
 * Ported from the temporary entry form of the legacy entry page
 * (WEB-INF/view/template/time/record/entry.jsp).
 *
 * @param entries The record's temporary entries, each carrying its index within the record.
 * @param totals Record wide hour totals.
 * @param invalidFields The "<entryIndex>:<field>" keys that failed validation.
 * @param onEntryChange Called with an entry index and the changed fields.
 * @param focus The shared entry focus state, see useEntryFocus.
 * @param mixedPayTypes Whether the record also holds annual pay entries.
 */
export default function TempEntryTable({
  entries,
  totals,
  invalidFields,
  onEntryChange,
  focus,
  mixedPayTypes,
}) {
  return (
    <div className="overflow-x-auto">
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className={ENTRY_DATE_HEAD_CELL}>Date</th>
            <th className={ENTRY_HEAD_CELL}>Work</th>
            <th className="px-1 py-2.5 text-center font-semibold">
              Work Time Description / Comments
            </th>
          </tr>
        </thead>
        <tbody className="table__body">
          {entries.map((entry) => (
            <TempEntryRow
              key={entry.date}
              entry={entry}
              invalidFields={invalidFields}
              onEntryChange={onEntryChange}
              focus={focus}
            />
          ))}
          {/* The totals row carries the same column dividers as the entries above it. */}
          <tr className="table__row table__totals text-center">
            <td className="border-r border-gray-300 px-5 py-1 text-right font-semibold">
              {mixedPayTypes ? "TE Record Totals" : "Record Totals"}
            </td>
            <td className="border-r border-gray-200 py-1 font-semibold">
              {totals.tempWorkHours}
            </td>
            <td className="border-r border-gray-200"></td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}

function TempEntryRow({ entry, invalidFields, onEntryChange, focus }) {
  const index = entry.index;
  const isRowActive = focus.activeEntryIndex === index;
  const isFutureDate = entry.date > format(new Date(), "yyyy-MM-dd");

  const isInvalid = (field) => invalidFields.has(`${index}:${field}`);
  const fieldFocus = (field) => ({
    onFocus: () => focus.onFocus(index, field),
    onBlur: () => focus.onBlur(index, field),
  });

  return (
    <tr
      className={cn(
        ENTRY_ROW,
        isWeekend(entry.date) ? "bg-gray-100" : "bg-gray-25",
      )}
    >
      <td
        className={cn(
          "border-r px-5 text-center",
          isRowActive
            ? "border-teal-600 bg-teal-600 text-white"
            : "border-gray-300",
        )}
      >
        {formatEntryDate(entry.date)}
      </td>
      <EntryCell isInvalid={isInvalid("workHours")} className="w-24">
        <EntryHoursInput
          value={entry.workHours}
          onChange={(value) => onEntryChange(index, { workHours: value })}
          step={0.25}
          max={24}
          isDisabled={isFutureDate}
          tabIndex={1}
          name="numWorkHours"
          {...fieldFocus("workHours")}
        />
      </EntryCell>
      <EntryCell isInvalid={isInvalid("empComment")} className="w-3/4">
        <textarea
          className="h-8 w-full resize-none overflow-hidden border-0 bg-transparent px-1 py-1 text-left"
          maxLength={MAX_COMMENT_LENGTH}
          value={entry.empComment || ""}
          onChange={(e) =>
            onEntryChange(index, { empComment: e.target.value || null })
          }
          tabIndex={entry.workHours ? 1 : -1}
          name="entryComment"
          {...fieldFocus("empComment")}
        />
      </EntryCell>
    </tr>
  );
}

/** Formats an entry date the way the legacy table did, i.e. "Mon 7/6/2026". */
function formatEntryDate(isoDate) {
  return format(parseISO(isoDate), "EEE M/d/yyyy");
}

function isWeekend(isoDate) {
  const day = parseISO(isoDate).getDay();
  return day === 0 || day === 6;
}
