import React from "react";
import { format, parseISO } from "date-fns";
import Modal from "app/components/Modal";
import Button from "app/components/Button";
import { getRecordStatusLabel } from "app/views/time/attendance/recordStatus";
import {
  entryHours,
  hasAnnualEntries,
  hasTempEntries,
} from "app/views/time/attendance/recordUtils";
import { useMiscLeaveTypes } from "app/views/time/attendance/useTimeRecords";
import { formatRecordDate } from "app/views/time/attendance/history/RecordTable";

const ANNUAL_HOUR_COLUMNS = [
  { field: "workHours", label: "Work" },
  { field: "holidayHours", label: "Holiday" },
  { field: "vacationHours", label: "Vacation" },
  { field: "personalHours", label: "Personal" },
  { field: "sickEmpHours", label: "Sick Emp" },
  { field: "sickFamHours", label: "Sick Fam" },
];

/**
 * Shows the day by day entries of a single time record.
 * Mirrors the legacy record details modal (WEB-INF/view/template/time/record/details.jsp).
 */
export default function RecordDetailsModal({ record, onClose }) {
  if (!record) {
    return null;
  }

  return (
    <Modal isOpen={!!record} onOpenChange={onClose} className="max-w-[95vw]">
      <Modal.Title>
        Attendance record for {record.employee?.fullName} from{" "}
        {formatRecordDate(record.beginDate)} to{" "}
        {formatRecordDate(record.endDate)}
      </Modal.Title>
      <Modal.Body>
        <RecordDetails record={record} />
      </Modal.Body>
      <Modal.Buttons>
        <Button variant="secondary" onPress={() => onClose(false)}>
          Exit
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}

/**
 * The entries and summary of a single record, without any surrounding dialog. The Review Time
 * Records page shows this beside its record list, the way the legacy recordDetails directive
 * was reused by the review modal.
 */
export function RecordDetails({ record }) {
  const miscLeaveTypes = useMiscLeaveTypes();

  if (!record) {
    return null;
  }

  const annualEntries = hasAnnualEntries(record);
  const tempEntries = hasTempEntries(record);

  return (
    <div className="flex flex-col gap-4 lg:flex-row">
      <div className="grow overflow-x-auto">
        {tempEntries && (
          <TempEntryTable record={record} showTitle={annualEntries} />
        )}
        {annualEntries && (
          <AnnualEntryTable
            record={record}
            showTitle={tempEntries}
            miscLeaveTypes={miscLeaveTypes.data}
          />
        )}
      </div>
      <div className="w-full shrink-0 lg:w-56">
        <DetailsSection title="Notes">
          {record.remarks || "This time record has no notes."}
        </DetailsSection>
        <DetailsSection title="Supervisor">
          {record.supervisor?.fullName}
        </DetailsSection>
        <DetailsSection title="Status">
          {getRecordStatusLabel(record.recordStatus)}
        </DetailsSection>
        <DetailsSection title="Actions">
          <a
            href={`/api/v1/attendance/report?timeRecordId=${record.timeRecordId}`}
            target="_blank"
            rel="noreferrer"
            title="Open a Printable View for this Record"
          >
            Print Record
          </a>
        </DetailsSection>
      </div>
    </div>
  );
}

function DetailsSection({ title, children }) {
  return (
    <div className="mb-3">
      <h3 className="font-semibold text-teal-700">{title}</h3>
      <div className="break-words">{children}</div>
    </div>
  );
}

function TempEntryTable({ record, showTitle }) {
  const entries = record.timeEntries.filter((entry) => entry.payType === "TE");

  return (
    <div>
      {showTitle && (
        <h1 className="my-2 font-semibold text-teal-700">
          Temporary Pay Entries
        </h1>
      )}
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell">Day</th>
            <th className="table__head__cell">Date</th>
            <th className="table__head__cell cell--number">Work</th>
            <th className="table__head__cell">
              Work Time Description / Comments
            </th>
          </tr>
        </thead>
        <tbody className="table__body table__body--striped">
          {entries.map((entry) => (
            <tr className="table__row" key={entry.date}>
              <td className="table__cell">{formatDayOfWeek(entry.date)}</td>
              <td className="table__cell">{formatRecordDate(entry.date)}</td>
              <td className="table__cell cell--number">
                {entryHours(entry.workHours)}
              </td>
              <td className="table__cell">{entry.empComment}</td>
            </tr>
          ))}
          {!showTitle && (
            <tr className="table__row table__totals">
              <td className="table__cell"></td>
              <td className="table__cell table__cell--bold">Record Totals</td>
              <td className="table__cell table__cell--bold cell--number">
                {record.totals?.tempWorkHours}
              </td>
              <td className="table__cell"></td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

function AnnualEntryTable({ record, showTitle, miscLeaveTypes }) {
  const entries = record.timeEntries.filter((entry) => entry.payType !== "TE");
  const miscLabel = (miscType) =>
    miscLeaveTypes?.[miscType]?.shortName ||
    (miscType ? `${miscType}?!` : "--");

  return (
    <div>
      {showTitle && (
        <h1 className="my-2 font-semibold text-teal-700">
          Regular/Special Annual Pay Entries
        </h1>
      )}
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell">Day</th>
            <th className="table__head__cell">Date</th>
            {ANNUAL_HOUR_COLUMNS.map((column) => (
              <th key={column.field} className="table__head__cell cell--number">
                {column.label}
              </th>
            ))}
            <th className="table__head__cell cell--number">Misc</th>
            <th className="table__head__cell">Misc Type</th>
            <th className="table__head__cell cell--number">Misc 2</th>
            <th className="table__head__cell">Misc Type 2</th>
            <th className="table__head__cell cell--number">Total</th>
          </tr>
        </thead>
        <tbody className="table__body table__body--striped">
          {entries.map((entry) => (
            <tr className="table__row" key={entry.date}>
              <td className="table__cell">{formatDayOfWeek(entry.date)}</td>
              <td className="table__cell">{formatRecordDate(entry.date)}</td>
              {ANNUAL_HOUR_COLUMNS.map((column) => (
                <td key={column.field} className="table__cell cell--number">
                  {entryHours(entry[column.field])}
                </td>
              ))}
              <td className="table__cell cell--number">
                {entryHours(entry.miscHours)}
              </td>
              <td className="table__cell">{miscLabel(entry.miscType)}</td>
              <td className="table__cell cell--number">
                {entryHours(entry.misc2Hours)}
              </td>
              <td className="table__cell">{miscLabel(entry.miscType2)}</td>
              <td className="table__cell cell--number">{entry.total}</td>
            </tr>
          ))}
          <tr className="table__row table__totals">
            <td className="table__cell"></td>
            <td className="table__cell table__cell--bold">Record Totals</td>
            {ANNUAL_HOUR_COLUMNS.map((column) => (
              <td
                key={column.field}
                className="table__cell table__cell--bold cell--number"
              >
                {record.totals?.[column.field]}
              </td>
            ))}
            <td className="table__cell table__cell--bold cell--number">
              {record.totals?.miscHours}
            </td>
            <td className="table__cell"></td>
            <td className="table__cell table__cell--bold cell--number">
              {record.totals?.misc2Hours}
            </td>
            <td className="table__cell"></td>
            <td className="table__cell table__cell--bold cell--number">
              {record.totals?.total}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}

function formatDayOfWeek(isoDate) {
  return isoDate ? format(parseISO(isoDate), "EEE") : "";
}
