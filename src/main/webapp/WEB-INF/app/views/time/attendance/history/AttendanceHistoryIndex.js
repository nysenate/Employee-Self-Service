import React, { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import Controls from "app/components/Controls";
import LoadingIndicator from "app/components/LoadingIndicator";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import RecordTable from "app/views/time/attendance/history/RecordTable";
import RecordDetailsModal from "app/views/time/attendance/history/RecordDetailsModal";
import {
  useActiveTimeRecordYears,
  useAttendanceRecords,
  useTimeRecords,
} from "app/views/time/attendance/useTimeRecords";
import {
  compareRecords,
  formatAttendRecord,
  initTimesheetRecord,
} from "app/views/time/attendance/recordUtils";

export default function AttendanceHistoryIndex() {
  const { data: user } = useRequireAuthedUser();

  return (
    <div>
      <Hero>Attendance History</Hero>
      <AttendanceHistorySection
        empId={user?.employeeId}
        emptyMessage="You have no time records."
      />
    </div>
  );
}

/**
 * The attendance history for a single employee: a year selector over the years the employee
 * has records for, and the active and submitted record tables for the selected year. Used
 * both by the My Attendance page and, for an arbitrary employee, by the Employee Search page.
 */
export function AttendanceHistorySection({
  empId,
  emptyMessage = "This employee has no time records.",
  linkActiveToEntry = true,
}) {
  const recordYears = useActiveTimeRecordYears(empId);

  if (recordYears.isPending) {
    return <LoadingIndicator />;
  }

  if (recordYears.data.length === 0) {
    return (
      <NoticeCard title="No Time Record History">{emptyMessage}</NoticeCard>
    );
  }

  return (
    <AttendanceHistory
      empId={empId}
      recordYears={recordYears.data}
      linkActiveToEntry={linkActiveToEntry}
    />
  );
}

function AttendanceHistory({ empId, recordYears, linkActiveToEntry = true }) {
  const [year, setYear] = useState(recordYears[0]);
  const [detailsRecord, setDetailsRecord] = useState(null);

  // The legacy page requested the full calendar year, ending at the start of the next.
  const from = `${year}-01-01`;
  const to = `${year + 1}-01-01`;

  const timeRecords = useTimeRecords(empId, from, to);
  const attendanceRecords = useAttendanceRecords(empId, from, to);

  const isLoading = timeRecords.isPending || attendanceRecords.isPending;

  const { active, submitted, annualTotals, paperTimesheetsDisplayed } = useMemo(
    () => combineRecords(timeRecords.data, attendanceRecords.data),
    [timeRecords.data, attendanceRecords.data],
  );

  return (
    <div>
      <Controls>
        <label className="font-semibold text-teal-700" htmlFor="year">
          View attendance records for year&nbsp;
        </label>
        <select
          id="year"
          name="year"
          className="select"
          value={year}
          onChange={(e) => setYear(parseInt(e.target.value))}
        >
          {recordYears.map((recordYear) => (
            <option value={recordYear} key={recordYear}>
              {recordYear}
            </option>
          ))}
        </select>
      </Controls>

      {isLoading ? (
        <LoadingIndicator />
      ) : (
        <>
          {active.length === 0 && submitted.length === 0 && (
            <NoticeCard title={`No Employee Records For ${year}`}>
              It appears as if the employee has no records for the selected
              year.
              <br />
              Please contact Senate Personnel at (518) 455-3376 if you require
              any assistance.
            </NoticeCard>
          )}

          {active.length > 0 && (
            <ActiveRecords
              records={active}
              linkActiveToEntry={linkActiveToEntry}
              onRecordClick={setDetailsRecord}
            />
          )}

          {submitted.length > 0 && (
            <SubmittedRecords
              records={submitted}
              year={year}
              annualTotals={annualTotals}
              paperTimesheetsDisplayed={paperTimesheetsDisplayed}
              onRecordClick={setDetailsRecord}
            />
          )}
        </>
      )}

      <RecordDetailsModal
        record={detailsRecord}
        onClose={() => setDetailsRecord(null)}
      />
    </div>
  );
}

function ActiveRecords({ records, linkActiveToEntry, onRecordClick }) {
  const navigate = useNavigate();

  return (
    <Card className="mt-3">
      <Card.Header>
        <Card.Title>Active Attendance Records</Card.Title>
      </Card.Header>
      <Card.Content>
        <p className="text-center">
          The following time records are in progress or awaiting submission.
          <br />
          {linkActiveToEntry
            ? "You can edit a record by clicking the row."
            : "Click a row to view the in-progress record."}
        </p>
        <RecordTable
          records={records}
          onRecordClick={
            linkActiveToEntry
              ? (record) =>
                  navigate(`/time/record/entry?record=${record.beginDate}`)
              : onRecordClick
          }
        />
      </Card.Content>
    </Card>
  );
}

function SubmittedRecords({
  records,
  year,
  annualTotals,
  paperTimesheetsDisplayed,
  onRecordClick,
}) {
  return (
    <Card className="mt-3">
      <Card.Header>
        <Card.Title>Submitted Attendance Records</Card.Title>
      </Card.Header>
      <Card.Content>
        <p className="text-center">
          Time records that have been submitted for pay periods during {year}{" "}
          are listed in the table below.
          <br />
          You can view details about each pay period by clicking the row.
          {paperTimesheetsDisplayed && (
            <>
              <br />
              <span className="font-semibold">Note:</span> Details are
              unavailable for attendance records entered via paper timesheet
              (designated by "(paper)" under Status)
            </>
          )}
        </p>
        <RecordTable
          records={records}
          annualTotals={annualTotals}
          onRecordClick={onRecordClick}
          isRecordClickable={(record) => !record.paperTimesheet}
        />
      </Card.Content>
    </Card>
  );
}

function NoticeCard({ title, children }) {
  return (
    <Card className="mt-3">
      <Card.Header>
        <Card.Title>{title}</Card.Title>
      </Card.Header>
      <Card.Content>
        <p className="text-center">{children}</p>
      </Card.Content>
    </Card>
  );
}

/**
 * Merges electronic timesheets with attendance records into the two displayed lists.
 *
 * Attendance records are the personnel-side source of truth for periods that have been
 * processed. Each one either points at the timesheets it covers, or - when time was
 * submitted on paper - stands in for a timesheet that does not exist. Timesheets ending
 * after the last attendance record have not been processed yet, so they are shown as
 * active (employee scoped) or submitted.
 *
 * Ported from combineRecords in assets/js/src/time/record/record-history-directive.js.
 */
function combineRecords(timeRecords, attendanceRecords) {
  const empty = {
    active: [],
    submitted: [],
    annualTotals: {},
    paperTimesheetsDisplayed: false,
  };

  if (!timeRecords || !attendanceRecords) {
    return empty;
  }

  const timesheets = timeRecords.map(initTimesheetRecord);
  const timesheetMap = timesheets.reduce((map, record) => {
    map[record.timeRecordId] = record;
    return map;
  }, {});

  const active = [];
  const submitted = [];
  let paperTimesheetsDisplayed = false;
  let latestAttendanceEnd = "1970-01-01";

  attendanceRecords.forEach((attendanceRecord) => {
    if (attendanceRecord.endDate > latestAttendanceEnd) {
      latestAttendanceEnd = attendanceRecord.endDate;
    }
    if (attendanceRecord.timesheetIds.length === 0) {
      paperTimesheetsDisplayed = true;
      submitted.push(formatAttendRecord(attendanceRecord));
      return;
    }
    attendanceRecord.timesheetIds.forEach((timesheetId) => {
      const record = timesheetMap[timesheetId];
      if (!record) {
        console.error("Could not find timesheet with id:", timesheetId);
        return;
      }
      submitted.push(record);
    });
  });

  timesheets.forEach((timesheet) => {
    if (timesheet.endDate > latestAttendanceEnd) {
      if (timesheet.scope === "E") {
        active.push(timesheet);
      } else {
        submitted.push(timesheet);
      }
    }
  });

  const annualTotals = submitted.reduce((totals, record) => {
    Object.entries(record.totals || {}).forEach(([field, value]) => {
      totals[field] = (totals[field] || 0) + value;
    });
    return totals;
  }, {});

  return {
    active: active.sort(compareRecords).reverse(),
    submitted: submitted.sort(compareRecords).reverse(),
    annualTotals,
    paperTimesheetsDisplayed,
  };
}
