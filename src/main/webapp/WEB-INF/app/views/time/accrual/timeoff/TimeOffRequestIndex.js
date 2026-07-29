import React, { useMemo } from "react";
import { format } from "date-fns";
import { useNavigate } from "react-router-dom";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import Button from "app/components/Button";
import Notification from "app/components/Notification";
import LoadingIndicator from "app/components/LoadingIndicator";
import AssertPermission from "app/components/AssertPermission";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import { useEmployeeTimeOffRequests } from "app/views/time/accrual/timeoff/useTimeOffRequests";
import TimeOffRequestList from "app/views/time/accrual/timeoff/TimeOffRequestList";
import {
  compareRequests,
  formatRequests,
  withoutActiveRequests,
} from "app/views/time/accrual/timeoff/timeOffRequestUtils";

/**
 * An employee's own time off requests: the ones still ahead of them, and everything before that.
 * Ported from the legacy RequestCtrl (assets/js/src/time/accrual/time-off-request.js) and
 * WEB-INF/view/template/time/accrual/time-off-request.jsp.
 */
export default function TimeOffRequestIndex() {
  const { data: user } = useRequireAuthedUser();

  return (
    <AssertPermission permission="time:time-off-request-page">
      <div>
        <Hero>Time Off Requests</Hero>
        {user && <TimeOffRequests empId={user.employeeId} />}
      </div>
    </AssertPermission>
  );
}

function TimeOffRequests({ empId }) {
  const navigate = useNavigate();
  const { today, yesterday } = useDateBounds();

  // Anything still running from today onward is active; anything ending before that is history.
  const active = useEmployeeTimeOffRequests(empId, today, null);
  const history = useEmployeeTimeOffRequests(empId, null, yesterday);

  const activeRequests = useMemo(
    () => formatRequests(active.data).sort(compareRequests),
    [active.data],
  );

  /*
   * A request spanning yesterday and today satisfies both queries, so the history list drops
   * whatever is already shown as active.
   */
  const pastRequests = useMemo(
    () =>
      withoutActiveRequests(formatRequests(history.data), activeRequests).sort(
        compareRequests,
      ),
    [history.data, activeRequests],
  );

  if (active.isPending || history.isPending) {
    return <LoadingIndicator />;
  }

  return (
    <div>
      <Card className="mt-3">
        <p className="p-3 text-center font-semibold">
          Submit a time-off request for approval by your Time and Attendance
          supervisor.
          <br />
          If the hours are approved you will still have to enter them in the
          time record for that date.
        </p>

        <h2 className="px-3 text-xl font-semibold text-teal-700">
          Active Time Off Requests
        </h2>
        <TimeOffRequestList
          requests={activeRequests}
          emptyMessage="No Time Off Requests"
        />

        <div className="p-3 text-center">
          <Button
            onPress={() => navigate("/time/accrual/time-off-request/new")}
          >
            New Time Off Request
          </Button>
        </div>
      </Card>

      <Card className="mt-5">
        <h2 className="px-3 pt-3 text-xl font-semibold text-teal-700">
          Time Off Request History
        </h2>
        <TimeOffRequestList
          requests={pastRequests}
          emptyMessage="No Time Off Requests"
        />
      </Card>
    </div>
  );
}

/** Today and yesterday as ISO dates, the boundary between an active request and a past one. */
export function useDateBounds() {
  return useMemo(() => {
    const now = new Date();
    const before = new Date();
    before.setDate(now.getDate() - 1);
    return {
      today: format(now, "yyyy-MM-dd"),
      yesterday: format(before, "yyyy-MM-dd"),
    };
  }, []);
}

/** Shown when a request cannot be loaded. */
export function RequestLoadError({ requestId }) {
  return (
    <Notification
      level="error"
      title={`Request number ${requestId} could not be opened.`}
      message="It does not exist, or you do not have permission to access it."
    />
  );
}
