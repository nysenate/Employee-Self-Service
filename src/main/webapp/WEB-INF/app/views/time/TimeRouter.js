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
import NotFound from "app/views/NotFound";

export default function TimeRouter() {
  return (
    <ThemeContext.Provider value={themes.time}>
      <Routes>
        <Route path="" element={<TimeLayout />}>
          <Route path="record/entry" element={<RecordEntryIndex />} />
          <Route path="record/history" element={<AttendanceHistoryIndex />} />
          <Route path="period/calendar" element={<PayrollCalendar />} />
          <Route path="accrual/history" element={<AccrualHistoryIndex />} />
          <Route
            path="accrual/projections"
            element={<AccrualProjectionsIndex />}
          />
          <Route path="accrual/donation" element={<SickLeaveDonationIndex />} />

          <Route
            path="record/manage"
            element={<div>Review Time Records</div>}
          />
          <Route
            path="record/emphistory"
            element={<div>Employee Attendance History</div>}
          />
          <Route
            path="allowance/emp-status"
            element={<div>Employee Allowed Hours</div>}
          />
          <Route
            path="accrual/emphistory"
            element={<div>Employee Accrual History</div>}
          />
          <Route
            path="accrual/emp-projections"
            element={<div>Employee Accrual Projections</div>}
          />
          <Route
            path="record/grant"
            element={<div>Grant Supervisor Access</div>}
          />

          <Route path="personnel/search" element={<div>Employee Search</div>} />

          <Route path="" element={<Navigate to="record/entry" replace />} />
          <Route path="*" element={<NotFound />} />
        </Route>
      </Routes>
    </ThemeContext.Provider>
  );
}

function TimeLayout() {
  return (
    <AppLayout>
      <Navigation>
        <Navigation.Title>Time & Attendance Menu</Navigation.Title>
        <Navigation.Section name="My Attendance">
          <Navigation.Link to="/time/record/entry">
            Attendance Record Entry
          </Navigation.Link>
          <Navigation.Link to="/time/record/history">
            Attendance History
          </Navigation.Link>
          <Navigation.Link to="/time/period/calendar">
            Payroll Calendar
          </Navigation.Link>
        </Navigation.Section>
        <Navigation.Section name="My Accruals">
          <Navigation.Link to="/time/accrual/history">
            Accrual History
          </Navigation.Link>
          <Navigation.Link to="/time/accrual/projections">
            Accrual Projections
          </Navigation.Link>
          <Navigation.Link to="/time/accrual/donation">
            Sick Leave Donation
          </Navigation.Link>
        </Navigation.Section>
        <Navigation.Section name="Manage Employees">
          <Navigation.Link to="/time/record/manage">
            Review Time Records
          </Navigation.Link>
          <Navigation.Link to="/time/record/emphistory">
            Employee Attendance History
          </Navigation.Link>
          <Navigation.Link to="/time/allowance/emp-status">
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
        </Navigation.Section>
        <Navigation.Section name="Personnel">
          <Navigation.Link to="/time/personnel/search">
            Employee Search
          </Navigation.Link>
        </Navigation.Section>
      </Navigation>
    </AppLayout>
  );
}
