import React, { useMemo, useState } from "react";
import { format } from "date-fns";
import Controls from "app/components/Controls";
import Notification from "app/components/Notification";
import LoadingIndicator from "app/components/LoadingIndicator";
import AssertPermission from "app/components/AssertPermission";
import EmployeeSelectPage from "app/views/time/personnel/EmployeeSelectPage";
import TimeOffRequestList from "app/views/time/accrual/timeoff/TimeOffRequestList";
import { useActiveTimeRecordYears } from "app/views/time/attendance/useTimeRecords";
import { useEmployeeTimeOffRequests } from "app/views/time/accrual/timeoff/useTimeOffRequests";
import {
  compareRequests,
  formatRequests,
} from "app/views/time/accrual/timeoff/timeOffRequestUtils";

/**
 * A supervisor's view of an employee's past time off requests, a year at a time.
 * Ported from the legacy timeOffRequestHistory directive
 * (assets/js/src/time/accrual/time-off-request-history-directive.js) and
 * WEB-INF/view/template/time/accrual/emp-time-off-request-history.jsp.
 */
export default function EmployeeTimeOffRequestHistoryIndex() {
  return (
    <AssertPermission permission="time:management-pages">
      <EmployeeSelectPage
        heading="Employee Time Off Request History"
        subject="Time Off Requests"
      >
        {(empId) => <TimeOffRequestHistorySection empId={empId} />}
      </EmployeeSelectPage>
    </AssertPermission>
  );
}

/**
 * A single employee's request history.
 *
 * Time off requests do not have active years of their own, so the years an employee has time
 * records for are used instead, as in the legacy directive.
 */
export function TimeOffRequestHistorySection({ empId }) {
  const recordYears = useActiveTimeRecordYears(empId);

  if (recordYears.isPending) {
    return <LoadingIndicator />;
  }

  if (recordYears.data.length === 0) {
    return (
      <Notification level="info" title="No Time Off Request History">
        <p>No time off requests exist.</p>
      </Notification>
    );
  }

  return <RequestHistory empId={empId} recordYears={recordYears.data} />;
}

function RequestHistory({ empId, recordYears }) {
  const [year, setYear] = useState(recordYears[0]);

  /*
   * The whole selected year, except that the current year stops at yesterday: anything from
   * today on is still an active request rather than history.
   */
  const { startRange, endRange } = useMemo(() => {
    const now = new Date();
    const yesterday = new Date();
    yesterday.setDate(now.getDate() - 1);
    return {
      startRange: `${year}-01-01`,
      endRange:
        year === now.getFullYear()
          ? format(yesterday, "yyyy-MM-dd")
          : `${year}-12-31`,
    };
  }, [year]);

  const requests = useEmployeeTimeOffRequests(empId, startRange, endRange);

  // A request the employee only saved was never sent anywhere, so it is not history.
  const history = useMemo(
    () =>
      formatRequests(requests.data)
        .filter((request) => request.status !== "SAVED")
        .sort(compareRequests),
    [requests.data],
  );

  return (
    <div>
      <Controls>
        <label className="font-semibold text-teal-700" htmlFor="request-year">
          Filter By Year&nbsp;
        </label>
        <select
          id="request-year"
          name="request-year"
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

      {requests.isPending ? (
        <LoadingIndicator variant="sm" />
      ) : (
        <div className="bg-white">
          <TimeOffRequestList
            requests={history}
            emptyMessage="No Time Off Requests for this year"
          />
        </div>
      )}
    </div>
  );
}
