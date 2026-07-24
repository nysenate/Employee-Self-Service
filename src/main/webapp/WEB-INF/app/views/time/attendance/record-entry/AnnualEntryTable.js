import React from "react";
import { format, parseISO } from "date-fns";
import { cn } from "app/utils/cn";
import EntryHoursInput, {
  EntryCell,
  ENTRY_DATE_HEAD_CELL,
  ENTRY_HEAD_CELL,
  ENTRY_ROW,
} from "app/views/time/attendance/record-entry/EntryHoursInput";
import {
  getHolidayHours,
  isHoliday,
} from "app/views/time/attendance/record-entry/recordEntryValidation";

/** The most hours that may be entered in a day for a single accrual type. */
const MAX_ACCRUAL_HOURS = 12;

/** A day of fewer hours than this is assumed to still need time entered against it. */
const FULL_WORK_DAY = 7;

/*
 * Adding the Misc 2 pair takes the table to twelve columns, which is more than the page is
 * wide at the sizes used for ten. Rather than making the employee scroll sideways to see the
 * hours they just entered, every column tightens up once those two appear: smaller headers,
 * a narrower date column, and misc type dropdowns just wide enough for their longest label
 * plus the browser's arrow.
 */
function getColumnStyles(compact) {
  const head = "py-2.5 text-center font-semibold whitespace-nowrap";
  return compact
    ? {
        hourHead: `w-[56px] px-0.5 text-sm ${head}`,
        dateHead: `w-[118px] px-1 text-sm ${head}`,
        miscTypeHead: `min-w-[104px] px-0.5 text-sm ${head}`,
        dateCell: "px-1",
        miscTypeCell: "px-0.5",
        select: "text-xs",
      }
    : {
        hourHead: ENTRY_HEAD_CELL,
        dateHead: ENTRY_DATE_HEAD_CELL,
        miscTypeHead: `min-w-[150px] px-1 ${head}`,
        dateCell: "px-5",
        miscTypeCell: "px-1",
        select: "text-sm",
      };
}

const ACCRUAL_COLUMNS = [
  { field: "vacationHours", label: "Vacation", name: "numVacationHours" },
  { field: "personalHours", label: "Personal", name: "numPersonalHours" },
  { field: "sickEmpHours", label: "Sick Emp", name: "numSickEmpHours" },
  { field: "sickFamHours", label: "Sick Fam", name: "numSickFamHours" },
];

/**
 * The time entry table for regular and special annual pay.
 * Ported from the annual entry form of the legacy entry page
 * (WEB-INF/view/template/time/record/entry.jsp).
 *
 * @param entries The record's annual entries, each carrying its index within the record.
 * @param totals Record wide hour totals.
 * @param miscEntered Whether the second misc leave columns are shown.
 * @param miscLeaveTypes Every misc leave type, for the misc type dropdowns.
 * @param miscLeaveGrants The employee's misc leave grants, which unlock restricted types.
 * @param holidays Holidays keyed by ISO date.
 * @param invalidFields The "<entryIndex>:<field>" keys that failed validation.
 * @param onEntryChange Called with an entry index and the changed fields.
 * @param focus The shared entry focus state, see useEntryFocus.
 * @param recordFocused Whether the record has been validated against, which opens up the
 *                      tab order to every field that already holds hours.
 * @param mixedPayTypes Whether the record also holds temporary pay entries, which are shown
 *                      in a table of their own.
 */
export default function AnnualEntryTable({
  entries,
  totals,
  miscEntered,
  miscLeaveTypes,
  miscLeaveGrants,
  holidays,
  invalidFields,
  onEntryChange,
  focus,
  recordFocused,
  mixedPayTypes,
}) {
  const styles = getColumnStyles(miscEntered);

  return (
    <div className="overflow-x-auto">
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className={styles.dateHead}>Date</th>
            <th className={styles.hourHead}>Work</th>
            <th className={styles.hourHead}>Holiday</th>
            {ACCRUAL_COLUMNS.map((column) => (
              <th key={column.field} className={styles.hourHead}>
                {column.label}
              </th>
            ))}
            <th className={styles.hourHead}>Misc</th>
            <th className={styles.miscTypeHead}>Misc Type</th>
            {miscEntered && (
              <>
                <th className={styles.hourHead}>Misc 2</th>
                <th className={styles.miscTypeHead}>Misc 2 Type</th>
              </>
            )}
            <th className={styles.hourHead}>Total</th>
          </tr>
        </thead>
        <tbody className="table__body">
          {entries.map((entry) => (
            <AnnualEntryRow
              key={entry.date}
              entry={entry}
              miscEntered={miscEntered}
              miscLeaveTypes={miscLeaveTypes}
              miscLeaveGrants={miscLeaveGrants}
              holidays={holidays}
              invalidFields={invalidFields}
              onEntryChange={onEntryChange}
              focus={focus}
              recordFocused={recordFocused}
              styles={styles}
            />
          ))}
          <TotalsRow
            totals={totals}
            miscEntered={miscEntered}
            label={mixedPayTypes ? "RA/SA Record Totals" : "Record Totals"}
            styles={styles}
          />
        </tbody>
      </table>
    </div>
  );
}

function AnnualEntryRow({
  entry,
  miscEntered,
  miscLeaveTypes,
  miscLeaveGrants,
  holidays,
  invalidFields,
  onEntryChange,
  focus,
  recordFocused,
  styles,
}) {
  const index = entry.index;
  const isRowActive = focus.activeEntryIndex === index;
  const isFutureDate = entry.date > format(new Date(), "yyyy-MM-dd");
  const entryIsHoliday = isHoliday(entry, { holidays });

  const isInvalid = (field) => invalidFields.has(`${index}:${field}`);
  const change = (field) => (value) => onEntryChange(index, { [field]: value });
  const fieldFocus = (field) => ({
    onFocus: () => focus.onFocus(index, field),
    onBlur: () => focus.onBlur(index, field),
  });

  /**
   * The tab order skips fields that are unlikely to need an entry, so that tabbing runs down
   * the days that still need hours rather than through every cell of the table.
   */
  const accrualTabIndex = (field) => {
    if (entry[field] !== null && entry[field] !== undefined && recordFocused) {
      return 1;
    }
    if (entry.total < FULL_WORK_DAY && !isWeekend(entry.date)) {
      return 1;
    }
    return focus.isFieldFocused(index, field) ? 1 : -1;
  };

  return (
    <tr
      className={cn(
        ENTRY_ROW,
        isWeekend(entry.date) ? "bg-gray-100" : "bg-gray-25",
      )}
    >
      <td
        className={cn(
          "border-r text-center",
          styles.dateCell,
          isRowActive
            ? "border-teal-600 bg-teal-600 text-white"
            : "border-gray-300",
        )}
      >
        {formatEntryDate(entry.date)}
      </td>
      <EntryCell isInvalid={isInvalid("workHours")}>
        <EntryHoursInput
          value={entry.workHours}
          onChange={change("workHours")}
          step={0.5}
          max={24}
          isDisabled={isFutureDate}
          tabIndex={entry.total < FULL_WORK_DAY || recordFocused ? 1 : -1}
          name="numWorkHours"
          {...fieldFocus("workHours")}
        />
      </EntryCell>
      <EntryCell isInvalid={isInvalid("holidayHours")}>
        <EntryHoursInput
          value={entry.holidayHours}
          onChange={change("holidayHours")}
          step={0.5}
          max={getHolidayHours(entry, { holidays })}
          isReadOnly={!entryIsHoliday}
          placeholder={entryIsHoliday ? "--" : ""}
          tabIndex={entryIsHoliday ? accrualTabIndex("holidayHours") : -1}
          name="numHolidayHours"
          {...fieldFocus("holidayHours")}
        />
      </EntryCell>
      {ACCRUAL_COLUMNS.map((column) => (
        <EntryCell key={column.field} isInvalid={isInvalid(column.field)}>
          <EntryHoursInput
            value={entry[column.field]}
            onChange={change(column.field)}
            step={0.5}
            max={MAX_ACCRUAL_HOURS}
            tabIndex={accrualTabIndex(column.field)}
            name={column.name}
            {...fieldFocus(column.field)}
          />
        </EntryCell>
      ))}
      <EntryCell isInvalid={isInvalid("miscHours")}>
        <EntryHoursInput
          value={entry.miscHours}
          onChange={change("miscHours")}
          step={0.5}
          max={MAX_ACCRUAL_HOURS}
          tabIndex={accrualTabIndex("miscHours")}
          name="numMiscHours"
          {...fieldFocus("miscHours")}
        />
      </EntryCell>
      <EntryCell
        isInvalid={isInvalid("miscType")}
        className={styles.miscTypeCell}
      >
        <MiscTypeSelect
          value={entry.miscType}
          onChange={change("miscType")}
          miscLeaveTypes={miscLeaveTypes}
          miscLeaveGrants={miscLeaveGrants}
          date={entry.date}
          tabIndex={
            focus.isFieldFocused(index, "miscType") || entry.miscHours ? 1 : -1
          }
          name="miscHourType"
          className={styles.select}
          {...fieldFocus("miscType")}
        />
      </EntryCell>
      {miscEntered && (
        <>
          <EntryCell isInvalid={isInvalid("misc2Hours")}>
            <EntryHoursInput
              value={entry.misc2Hours}
              onChange={change("misc2Hours")}
              step={0.5}
              max={MAX_ACCRUAL_HOURS}
              tabIndex={accrualTabIndex("miscHours")}
              name="numMisc2Hours"
              {...fieldFocus("misc2Hours")}
            />
          </EntryCell>
          <EntryCell
            isInvalid={isInvalid("miscType2")}
            className={styles.miscTypeCell}
          >
            <MiscTypeSelect
              value={entry.miscType2}
              onChange={change("miscType2")}
              miscLeaveTypes={miscLeaveTypes}
              miscLeaveGrants={miscLeaveGrants}
              date={entry.date}
              tabIndex={
                focus.isFieldFocused(index, "miscType2") || entry.misc2Hours
                  ? 1
                  : -1
              }
              name="miscHourType2"
              className={styles.select}
              {...fieldFocus("miscType2")}
            />
          </EntryCell>
        </>
      )}
      <EntryCell isInvalid={isInvalid("totalHours")} className="px-2">
        {entry.total}
      </EntryCell>
    </tr>
  );
}

/**
 * The misc leave types that may be used on a given date.
 * Restricted types are only offered when a grant of that type covers the date.
 */
function MiscTypeSelect({
  value,
  onChange,
  miscLeaveTypes,
  miscLeaveGrants,
  date,
  tabIndex,
  name,
  onFocus,
  onBlur,
  className,
}) {
  const available = (miscLeaveTypes || []).filter(
    (miscLeave) =>
      // A type already on the entry stays selectable even if its grant no longer covers the
      // date, so that saving the record cannot quietly drop it.
      miscLeave.type === value ||
      isMiscLeaveAvailable(miscLeave, date, miscLeaveGrants),
  );

  return (
    <select
      className={cn("w-full border border-gray-300 bg-white p-0.5", className)}
      value={value || ""}
      onChange={(e) => onChange(e.target.value || null)}
      tabIndex={tabIndex}
      name={name}
      onFocus={onFocus}
      onBlur={onBlur}
    >
      <option value="">No Misc Hours</option>
      {available.map((miscLeave) => (
        <option value={miscLeave.type} key={miscLeave.type}>
          {miscLeave.shortName}
        </option>
      ))}
    </select>
  );
}

function isMiscLeaveAvailable(miscLeave, date, miscLeaveGrants) {
  if (!miscLeave.restricted) {
    return true;
  }
  return (miscLeaveGrants || []).some(
    ({ grant }) =>
      grant.miscLeaveType === miscLeave.type &&
      grant.beginDate <= date &&
      date <= grant.endDate,
  );
}

function TotalsRow({ totals, miscEntered, label, styles }) {
  // The totals row carries the same column dividers as the entries above it.
  const cell = "border-r border-gray-200 py-1";

  return (
    <tr className="table__row table__totals text-center">
      <td
        className={cn(
          "border-r border-gray-300 py-1 text-right font-semibold",
          styles.dateCell,
        )}
      >
        {label}
      </td>
      <td className={cell}>{totals.raSaWorkHours}</td>
      <td className={cell}>{totals.holidayHours}</td>
      {ACCRUAL_COLUMNS.map((column) => (
        <td key={column.field} className={cell}>
          {totals[column.field]}
        </td>
      ))}
      <td className={cell}>{totals.miscHours}</td>
      <td className={cell}></td>
      {miscEntered && (
        <>
          <td className={cell}>{totals.misc2Hours}</td>
          <td className={cell}></td>
        </>
      )}
      <td className={`${cell} font-semibold`}>{totals.raSaTotal}</td>
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
