import React from "react";
import { cn } from "app/utils/cn";
import { formatRecordDate } from "app/views/time/attendance/history/RecordTable";

const HOUR_COLUMNS = [
  { field: "workHours", label: "Work" },
  { field: "holidayHours", label: "Holiday" },
  { field: "vacationHours", label: "Vacation" },
  { field: "personalHours", label: "Personal" },
  { field: "sickEmpHours", label: "Sick Emp" },
  { field: "sickFamHours", label: "Sick Fam" },
  { field: "miscHours", label: "Misc" },
  { field: "misc2Hours", label: "Misc2" },
];

const HEAD_CELL = "table__head__cell";
const CELL = "table__cell";

/**
 * A table of an employee group's time records, one row per record, with the employee's name
 * shown once above their first record.
 *
 * When selection is offered, clicking a row toggles that record and clicking the name column
 * toggles every record belonging to that employee.
 *
 * Ported from the legacy supervisorRecordList directive
 * (assets/js/src/time/record/supervisor-record-list.js).
 *
 * @param records The records to list.
 * @param selected A Set of selected time record ids, or undefined for a read only list.
 * @param onSelectedChange Called with the next Set of selected ids.
 * @param userEmpId If given, the user's own records cannot be selected.
 * @param onRecordClick Called with a record when a row of a read only list is clicked.
 */
export default function SupervisorRecordList({
  records,
  selected,
  onSelectedChange,
  userEmpId,
  onRecordClick,
}) {
  const selectable = !!selected;

  const isOwnRecord = (record) => record.employeeId === userEmpId;

  const setSelection = (recordsToSet, isSelected) => {
    const next = new Set(selected);
    recordsToSet.forEach((record) => {
      if (isSelected && !isOwnRecord(record)) {
        next.add(record.timeRecordId);
      } else {
        next.delete(record.timeRecordId);
      }
    });
    onSelectedChange(next);
  };

  const handleRowClick = (record) => {
    if (!selectable) {
      onRecordClick?.(record);
      return;
    }
    setSelection([record], !selected.has(record.timeRecordId));
  };

  // Clicking a name acts on the whole employee, following whichever way that first record goes.
  const handleNameClick = (record) => {
    if (!selectable) {
      return;
    }
    const empRecords = records.filter(
      (candidate) => candidate.employeeId === record.employeeId,
    );
    setSelection(empRecords, !selected.has(empRecords[0].timeRecordId));
  };

  return (
    <div className="overflow-x-auto py-3">
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className={HEAD_CELL}>Employee</th>
            {selectable && <th className={HEAD_CELL}>Select</th>}
            <th className={HEAD_CELL}>Pay Period</th>
            {HOUR_COLUMNS.map((column) => (
              <th key={column.field} className={`${HEAD_CELL} cell--number`}>
                {column.label}
              </th>
            ))}
            <th className={`${HEAD_CELL} cell--number`}>Total Hours</th>
          </tr>
        </thead>
        <tbody className="table__body">
          {records.map((record, index) => {
            const showName =
              index === 0 ||
              records[index - 1].employeeId !== record.employeeId;
            const isSelected = selectable && selected.has(record.timeRecordId);
            const ownRecord = isOwnRecord(record);

            return (
              <tr
                key={record.timeRecordId}
                onClick={() => handleRowClick(record)}
                title={
                  ownRecord
                    ? "You cannot review your own timesheet."
                    : "Select record"
                }
                className={cn(
                  "table__row",
                  showName && "border-t-2 border-teal-600/40",
                  isSelected && "bg-teal-50",
                  (selectable || onRecordClick) && "cursor-pointer",
                )}
              >
                <td
                  className={`${CELL} whitespace-nowrap`}
                  onClick={(e) => {
                    e.stopPropagation();
                    handleNameClick(record);
                  }}
                >
                  {showName && (
                    <div>
                      {record.employee?.fullName || record.employeeId}
                      <br />
                      <small className="text-teal-600">
                        Supervisor: {record.supervisor?.lastName}
                      </small>
                    </div>
                  )}
                </td>
                {selectable && (
                  <td className={`${CELL} text-center`}>
                    {!ownRecord && (
                      <input
                        type="checkbox"
                        checked={isSelected}
                        onChange={() => handleRowClick(record)}
                        onClick={(e) => e.stopPropagation()}
                        aria-label={`Select record for ${record.employee?.fullName}`}
                      />
                    )}
                  </td>
                )}
                <td className={`${CELL} whitespace-nowrap`}>
                  {formatRecordDate(record.beginDate)} -{" "}
                  {formatRecordDate(record.endDate)}
                </td>
                {HOUR_COLUMNS.map((column) => (
                  <td key={column.field} className={`${CELL} cell--number`}>
                    {record.totals?.[column.field]}
                  </td>
                ))}
                <td className={`${CELL} cell--number`}>
                  {record.totals?.total}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
