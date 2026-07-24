import React from "react";
import { addDays, format, formatDistanceToNow, parseISO } from "date-fns";
import { getRecordStatusDisplay } from "app/views/time/attendance/recordStatus";
import { formatRecordDate } from "app/views/time/attendance/history/RecordTable";

/** Beyond this many active records the table gives way to a dropdown. */
const MAX_TABLE_RECORDS = 5;

/**
 * Lets the employee pick which of their active records to enter time for.
 * Ported from the record selection container of the legacy entry page
 * (WEB-INF/view/template/time/record/entry.jsp).
 *
 * @param records The employee's active records.
 * @param selectedIndex Index of the record being entered.
 * @param onSelect Called with the index of the record to select.
 */
export default function RecordSelection({ records, selectedIndex, onSelect }) {
  return (
    <div className="border-b border-teal-600/50 bg-white p-3 text-center">
      <p className="text-center">
        Enter a time and attendance record by selecting from the list of active
        pay periods.
      </p>
      {records.length <= MAX_TABLE_RECORDS ? (
        <RecordSelectionTable
          records={records}
          selectedIndex={selectedIndex}
          onSelect={onSelect}
        />
      ) : (
        <RecordSelectionMenu
          records={records}
          selectedIndex={selectedIndex}
          onSelect={onSelect}
        />
      )}
    </div>
  );
}

/* The selection table is centered and tightly padded, as the legacy one was. */
const SELECT_HEAD_CELL = "border-b border-gray-300 p-1.5 font-semibold";
const SELECT_CELL = "p-1.5";

function RecordSelectionTable({ records, selectedIndex, onSelect }) {
  return (
    <div className="overflow-x-auto py-3">
      <table className="mx-auto w-11/12 text-center">
        <thead>
          <tr>
            <th className={SELECT_HEAD_CELL}>Select</th>
            <th className={SELECT_HEAD_CELL}>Pay Period</th>
            <th className={SELECT_HEAD_CELL}>Supervisor</th>
            <th className={SELECT_HEAD_CELL}>Period End</th>
            <th className={SELECT_HEAD_CELL}>Status</th>
            <th className={SELECT_HEAD_CELL}>Last Updated</th>
          </tr>
        </thead>
        <tbody>
          {records.map((record, index) => (
            <tr
              key={record.timeRecordId}
              className={`cursor-pointer hover:bg-[#e4ebff] ${
                index === selectedIndex ? "bg-[#e7e9f6]" : ""
              }`}
              onClick={() => onSelect(index)}
            >
              <td className={SELECT_CELL}>
                <input
                  type="radio"
                  name="recordSelect"
                  value={index}
                  checked={index === selectedIndex}
                  onChange={() => onSelect(index)}
                />
              </td>
              <td className={`${SELECT_CELL} whitespace-nowrap`}>
                {formatRecordDate(record.payPeriod?.startDate)} -{" "}
                {formatRecordDate(record.payPeriod?.endDate)}
              </td>
              <td className={SELECT_CELL}>{record.supervisor?.fullName}</td>
              <td
                className={`${SELECT_CELL} whitespace-nowrap ${isDue(record) ? "text-[#B90504]" : ""}`}
              >
                {getDueFromNow(record)}
              </td>
              <td className={SELECT_CELL}>
                <RecordStatus status={record.recordStatus} />
              </td>
              <td className={`${SELECT_CELL} whitespace-nowrap`}>
                <LastUpdated record={record} />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function RecordSelectionMenu({ records, selectedIndex, onSelect }) {
  const selectedRecord = records[selectedIndex];

  return (
    <div className="flex flex-wrap items-center justify-center gap-x-3 gap-y-1 py-2">
      <label className="font-semibold" htmlFor="record-select">
        Record Dates:{" "}
        <select
          id="record-select"
          className="select w-48"
          value={selectedIndex}
          onChange={(e) => onSelect(parseInt(e.target.value))}
        >
          {records.map((record, index) => (
            <option value={index} key={record.timeRecordId}>
              {formatRecordDate(record.beginDate)} -{" "}
              {formatRecordDate(record.endDate)}
            </option>
          ))}
        </select>
      </label>
      <span>
        <span className="font-semibold">Supervisor: </span>
        {selectedRecord?.supervisor?.fullName}
      </span>
      <span>
        <span className="font-semibold">Status: </span>
        {getRecordStatusDisplay(selectedRecord?.recordStatus).label}
      </span>
      <span>
        <span className="font-semibold">Last Updated: </span>
        <LastUpdated record={selectedRecord} />
      </span>
    </div>
  );
}

function RecordStatus({ status }) {
  const display = getRecordStatusDisplay(status);
  return <span style={{ color: display.color }}>{display.label}</span>;
}

/** A record that has never been saved shows as new rather than showing its creation date. */
function LastUpdated({ record }) {
  if (!record || record.overallUpdateDate === record.originalDate) {
    return "New";
  }
  return format(parseISO(record.overallUpdateDate), "M/d/yyyy h:mm a");
}

/** Records are due the day after the pay period ends. */
function getRecordDueDate(record) {
  return addDays(parseISO(record.endDate), 1);
}

function getDueFromNow(record) {
  return formatDistanceToNow(getRecordDueDate(record), { addSuffix: true });
}

function isDue(record) {
  return getRecordDueDate(record) < new Date();
}
