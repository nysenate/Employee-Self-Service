import React from "react";
import { format, parseISO } from "date-fns";
import { getRecordStatusDisplay } from "app/views/time/attendance/recordStatus";

const HOUR_COLUMNS = [
  { field: "workHours", label: "Work" },
  { field: "holidayHours", label: "Holiday" },
  { field: "vacationHours", label: "Vacation" },
  { field: "personalHours", label: "Personal" },
  { field: "sickEmpHours", label: "Sick Emp" },
  { field: "sickFamHours", label: "Sick Fam" },
  { field: "miscHours", label: "Misc" },
  { field: "misc2Hours", label: "Misc 2" },
  { field: "total", label: "Total" },
];

/** Formats an ISO date the way the legacy pages did with the moment 'l' format. */
export function formatRecordDate(isoDate) {
  return isoDate ? format(parseISO(isoDate), "M/d/yyyy") : "";
}

/*
 * This table carries twelve columns, so the default px-5 cell padding of the shared table
 * classes overflows the page container. The tighter padding matches the legacy .ess-table,
 * which used 10px 5px. Utilities are applied after the component layer, so px-2 wins.
 */
const HEAD_CELL = "table__head__cell px-2";
const HEAD_CELL_NUMBER = "table__head__cell cell--number px-2";
const CELL = "table__cell px-2";
const CELL_NUMBER = "table__cell cell--number px-2";

/**
 * Lists time records with a column for each hour type.
 *
 * @param records The records to list.
 * @param onRecordClick Called with a record when its row is clicked.
 * @param isRecordClickable Returns whether a given record's row is interactive.
 * @param annualTotals When given, a totals row is appended to the table.
 */
export default function RecordTable({
  records,
  onRecordClick,
  isRecordClickable = () => true,
  annualTotals,
}) {
  return (
    <div className="overflow-x-auto py-3">
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className={HEAD_CELL}>Date Range</th>
            <th className={HEAD_CELL}>Pay Period</th>
            <th className={HEAD_CELL}>Status</th>
            {HOUR_COLUMNS.map((column) => (
              <th key={column.field} className={HEAD_CELL_NUMBER}>
                {column.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="table__body table__body--striped table__body--highlight">
          {records.map((record) => (
            <RecordRow
              key={recordKey(record)}
              record={record}
              onRecordClick={onRecordClick}
              isClickable={isRecordClickable(record)}
            />
          ))}
          {annualTotals && <AnnualTotalsRow annualTotals={annualTotals} />}
        </tbody>
      </table>
    </div>
  );
}

function RecordRow({ record, onRecordClick, isClickable }) {
  const status = getRecordStatusDisplay(record.recordStatus);

  return (
    <tr
      className="table__row"
      onClick={isClickable ? () => onRecordClick(record) : undefined}
      title={isClickable ? "Click to view record details" : undefined}
      style={isClickable ? undefined : { cursor: "default" }}
    >
      <td className={`${CELL} whitespace-nowrap`}>
        {formatRecordDate(record.beginDate)} -{" "}
        {formatRecordDate(record.endDate)}
      </td>
      <td className={CELL}>{record.payPeriod?.payPeriodNum}</td>
      <td className={CELL}>
        <span style={{ color: status.color }}>{status.label}</span>
        {record.paperTimesheet && <span> (paper)</span>}
      </td>
      {HOUR_COLUMNS.map((column) => (
        <td key={column.field} className={CELL_NUMBER}>
          {record.totals?.[column.field]}
        </td>
      ))}
    </tr>
  );
}

function AnnualTotalsRow({ annualTotals }) {
  return (
    <tr className="table__row table__totals">
      <td className={CELL} colSpan={2}></td>
      <td className={`${CELL} table__cell--bold whitespace-nowrap`}>
        Annual Totals
      </td>
      {HOUR_COLUMNS.map((column) => (
        <td key={column.field} className={`${CELL_NUMBER} table__cell--bold`}>
          {annualTotals[column.field]}
        </td>
      ))}
    </tr>
  );
}

/** Paper attendance records have no time record id, so fall back to their date range. */
function recordKey(record) {
  return record.timeRecordId || `${record.beginDate}-${record.endDate}`;
}
