import React from "react";
import { ThemeContext, themes } from "app/ThemeContext";
import Navigation from "app/components/Navigation";
import AppLayout from "app/components/AppLayout";
import { Navigate, Route, Routes } from "react-router-dom";
import PayrollCalendar from "app/views/time/attendance/payroll-calendar/PayrollCalendar";
import AttendanceHistoryIndex from "app/views/time/attendance/history/AttendanceHistoryIndex";
import RecordEntryIndex from "app/views/time/attendance/record-entry/RecordEntryIndex";
import AccrualHistoryIndex from "app/views/time/accrual/AccrualHistoryIndex";
import AccrualProjectionsIndex from "app/views/time/accrual/AccrualProjectionsIndex";
import SickLeaveDonationIndex from "app/views/time/accrual/SickLeaveDonationIndex";
import AllowanceStatusIndex from "app/views/time/allowance/AllowanceStatusIndex";
import RecordManageIndex from "app/views/time/attendance/manage/RecordManageIndex";
import EmployeeAttendanceHistoryIndex from "app/views/time/attendance/history/EmployeeAttendanceHistoryIndex";
import EmployeeAllowanceStatusIndex from "app/views/time/allowance/EmployeeAllowanceStatusIndex";
import EmployeeAccrualHistoryIndex from "app/views/time/accrual/EmployeeAccrualHistoryIndex";
import EmployeeAccrualProjectionsIndex from "app/views/time/accrual/EmployeeAccrualProjectionsIndex";
import GrantSupervisorAccessIndex from "app/views/time/personnel/GrantSupervisorAccessIndex";
import EmployeeSearchIndex from "app/views/time/personnel/EmployeeSearchIndex";
import TimeOffRequestIndex from "app/views/time/accrual/timeoff/TimeOffRequestIndex";
import NewTimeOffRequestIndex from "app/views/time/accrual/timeoff/NewTimeOffRequestIndex";
import SingleTimeOffRequestIndex from "app/views/time/accrual/timeoff/SingleTimeOffRequestIndex";
import EmployeeTimeOffRequestsIndex from "app/views/time/accrual/timeoff/EmployeeTimeOffRequestsIndex";
import EmployeeTimeOffRequestHistoryIndex from "app/views/time/accrual/timeoff/EmployeeTimeOffRequestHistoryIndex";
import {
  useSupervisorRequestBadge,
  useTimeOffRequestBadges,
} from "app/views/time/accrual/timeoff/useTimeOffRequestBadges";
import NotFound from "app/views/NotFound";

export default function TimeRouter() {
  return (
    <ThemeContext.Provider value={themes.time}>
      <Routes>
        <Route path="" element={<TimeLayout />}>
          <Route path="record/entry" element={<RecordEntryIndex />} />
          <Route path="record/history" element={<AttendanceHistoryIndex />} />
          <Route path="allowance/status" element={<AllowanceStatusIndex />} />
          <Route path="period/calendar" element={<PayrollCalendar />} />
          <Route path="accrual/history" element={<AccrualHistoryIndex />} />
          <Route
            path="accrual/projections"
            element={<AccrualProjectionsIndex />}
          />
          <Route path="accrual/donation" element={<SickLeaveDonationIndex />} />

          {/* The "new" path has to be matched before the request id it would otherwise look like. */}
          <Route
            path="accrual/time-off-request"
            element={<TimeOffRequestIndex />}
          />
          <Route
            path="accrual/time-off-request/new"
            element={<NewTimeOffRequestIndex />}
          />
          <Route
            path="accrual/time-off-request/:requestId"
            element={<SingleTimeOffRequestIndex />}
          />
          <Route
            path="accrual/emp-time-off-requests"
            element={<EmployeeTimeOffRequestsIndex />}
          />
          <Route
            path="accrual/emp-time-off-request-history"
            element={<EmployeeTimeOffRequestHistoryIndex />}
          />

          <Route path="record/manage" element={<RecordManageIndex />} />
          <Route
            path="record/emphistory"
            element={<EmployeeAttendanceHistoryIndex />}
          />
          <Route
            path="allowance/emp-status"
            element={<EmployeeAllowanceStatusIndex />}
          />
          <Route
            path="accrual/emphistory"
            element={<EmployeeAccrualHistoryIndex />}
          />
          <Route
            path="accrual/emp-projections"
            element={<EmployeeAccrualProjectionsIndex />}
          />
          <Route path="record/grant" element={<GrantSupervisorAccessIndex />} />

          <Route path="personnel/search" element={<EmployeeSearchIndex />} />

          <Route path="" element={<Navigate to="record/entry" replace />} />
          <Route path="*" element={<NotFound />} />
        </Route>
      </Routes>
    </ThemeContext.Provider>
  );
}

/**
 * The counts beside the employee's own Time Off Requests link. These only render inside a
 * permitted Navigation.Link, so the queries behind them never run for a user without the page.
 */
function TimeOffRequestBadges() {
  const counts = useTimeOffRequestBadges();

  return (
    <>
      <Navigation.Badge
        count={counts.pending}
        color="teal"
        title="Pending Request Count"
      />
      <Navigation.Badge
        count={counts.approved}
        color="green"
        title="Approved Request Count"
      />
      <Navigation.Badge
        count={counts.rejected}
        color="orange"
        title="Rejected Request Count"
      />
    </>
  );
}

/** The count of requests waiting on the user as a supervisor. */
function SupervisorRequestBadge() {
  const count = useSupervisorRequestBadge();

  return (
    <Navigation.Badge
      count={count}
      color="teal"
      title="Requests needing action"
    />
  );
}

function TimeLayout() {
  return (
    <AppLayout>
      <Navigation>
        <Navigation.Title>Time & Attendance Menu</Navigation.Title>
        {/* Permissions mirror the legacy nav (WEB-INF/tags/component/nav/time-nav.tag). */}
        <Navigation.Section name="My Attendance">
          <Navigation.Link
            to="/time/record/entry"
            permission="time:attendance-record-pages"
          >
            Attendance Record Entry
          </Navigation.Link>
          <Navigation.Link
            to="/time/record/history"
            permission="time:attendance-record-pages"
          >
            Attendance History
          </Navigation.Link>
          <Navigation.Link
            to="/time/allowance/status"
            permission="time:allowance-page"
          >
            Allowed Hours
          </Navigation.Link>
          <Navigation.Link to="/time/period/calendar">
            Payroll Calendar
          </Navigation.Link>
        </Navigation.Section>
        <Navigation.Section name="My Accruals" permission="time:accrual-pages">
          <Navigation.Link to="/time/accrual/history">
            Accrual History
          </Navigation.Link>
          <Navigation.Link
            to="/time/accrual/projections"
            permission="time:accrual-projections:view"
          >
            Accrual Projections
          </Navigation.Link>
          <Navigation.Link to="/time/accrual/donation">
            Sick Leave Donation
          </Navigation.Link>
          <Navigation.Link
            to="/time/accrual/time-off-request"
            permission="time:time-off-request-page"
          >
            Time Off Requests
            <TimeOffRequestBadges />
          </Navigation.Link>
        </Navigation.Section>
        <Navigation.Section
          name="Manage Employees"
          permission="time:management-pages"
        >
          <Navigation.Link to="/time/record/manage">
            Review Time Records
          </Navigation.Link>
          <Navigation.Link to="/time/record/emphistory">
            Employee Attendance History
          </Navigation.Link>
          <Navigation.Link
            to="/time/allowance/emp-status"
            permission="time:emp-allowance-page"
          >
            Employee Allowed Hours
          </Navigation.Link>
          <Navigation.Link to="/time/accrual/emphistory">
            Employee Accrual History
          </Navigation.Link>
          <Navigation.Link to="/time/accrual/emp-projections">
            Employee Accrual Projections
          </Navigation.Link>
          <Navigation.Link to="/time/record/grant">
            Grant Supervisor Access
          </Navigation.Link>
          <Navigation.Link to="/time/accrual/emp-time-off-requests">
            Review Time Off Requests
            <SupervisorRequestBadge />
          </Navigation.Link>
          <Navigation.Link to="/time/accrual/emp-time-off-request-history">
            Employee Time Off Request History
          </Navigation.Link>
        </Navigation.Section>
        <Navigation.Section name="Personnel" permission="time:personnel-pages">
          <Navigation.Link to="/time/personnel/search">
            Employee Search
          </Navigation.Link>
        </Navigation.Section>
      </Navigation>
    </AppLayout>
  );
}
