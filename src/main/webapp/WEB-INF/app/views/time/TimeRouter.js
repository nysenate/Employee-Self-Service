import React from 'react'
import { ThemeContext, themes } from "app/contexts/ThemeContext";
import Navigation from "app/components/Navigation";
import AppLayout from "app/components/AppLayout";
import { Navigate, Route, Routes } from "react-router-dom";

import AccrualHistoryIndex from "./accrual/AccrualHistoryIndex.js";
import CalendarIndex from "./period/CalendarIndex";
import AccrualEmpHistoryIndex from "./accrual/AccrualEmpHistoryIndex.js";
import RecordHistoryIndex from "./record/RecordHistoryIndex.js";
import RecordEmpHistoryIndex from "./record/RecordEmpHistoryIndex.js";
// import AccrualProjections from "./accruals/AccrualProjections.js";
// import SickLeaveDonation from "./accruals/SickLeaveDonation.js";
//
// import AttendanceEntry from "./attendance/AttendanceEntry.js";
// import AttendanceHistory from "./attendance/AttendanceHistory.js";
// import PayrollCalendar from "./attendance/PayrollCalendar.js";

export default function TimeRouter() {
  return (
    <ThemeContext.Provider value={themes.time}>
      <Routes>
        <Route path="" element={<TimeLayout/>}>
          {/*<Route path="record/entry" element={<AttendanceEntry/>}/>*/}
          <Route path="record/history" element={<RecordHistoryIndex/>}/>
          <Route path="period/calendar" element={<CalendarIndex/>}/>
          <Route path="accrual/history" element={<AccrualHistoryIndex/>}/>
          {/*<Route path="accrual/projections" element={<AccrualProjections/>}/>*/}
          {/*<Route path="accrual/donation" element={<SickLeaveDonation/>}/>*/}
          {/*<Route path="record/manage" element={<SickLeaveDonation/>}/>*/}
          <Route path="record/emphistory" element={<RecordEmpHistoryIndex/>}/>
          <Route path="accrual/emphistory" element={<AccrualEmpHistoryIndex/>}/>
          {/*<Route path="accrual/emp-projections" element={<AccrualEmpProjectionsIndex/>}/>*/}
          {/*<Route path="record/grant" element={<SickLeaveDonation/>}/>*/}
          {/*<Route path="" element={<Navigate to="attendance/entry" replace/>}/>*/}
          <Route path="*" element={<div>404</div>}/>
        </Route>
      </Routes>
    </ThemeContext.Provider>
  )
}

// Gonna Need to do hidden based on empId here too
function TimeLayout() {
  return (
    <AppLayout>
      <Navigation>
        <Navigation.Title>
          My Info Menu
        </Navigation.Title>
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
      </Navigation>
    </AppLayout>
  )
}
