import React from 'react'
import { ThemeContext, themes } from "app/contexts/ThemeContext";
import Navigation from "app/components/Navigation";
import AppLayout from "app/components/AppLayout";
import { Navigate, Route, Routes } from "react-router-dom";

import AccrualHistory from "./accruals/AccrualHistory.js"
import AccrualProjections from "./accruals/AccrualProjections.js"
import SickLeaveDonation from "./accruals/SickLeaveDonation.js"

import AttendanceEntry from "./attendance/AttendanceEntry.js"
import AttendanceHistory from "./attendance/AttendanceHistory.js"
import PayrollCalendar from "./attendance/PayrollCalendar.js"

export default function TimeRouter() {
  return (
    <ThemeContext.Provider value={themes.time}>
      <Routes>
        <Route path="" element={<TimeLayout/>}>
          <Route path="attendance/entry" element={<AttendanceEntry/>}/>
          <Route path="attendance/history" element={<AttendanceHistory/>}/>
          <Route path="calendar" element={<PayrollCalendar/>}/>
          <Route path="accrual/history" element={<AccrualHistory/>}/>
          <Route path="accrual/projections" element={<AccrualProjections/>}/>
          <Route path="donation" element={<SickLeaveDonation/>}/>
          <Route path="" element={<Navigate to="attendance/entry" replace/>}/>
          <Route path="*" element={<div>404</div>}/>
        </Route>
      </Routes>
    </ThemeContext.Provider>
  )
}

function TimeLayout() {
  return (
    <AppLayout>
      <Navigation>
        <Navigation.Title>
          My Info Menu
        </Navigation.Title>
        <Navigation.Section name="My Attendance">
          <Navigation.Link to="/time/attendance/entry">
            Attendance Record Entry
          </Navigation.Link>
          <Navigation.Link to="/time/attendance/history">
            Attendance History
          </Navigation.Link>
          <Navigation.Link to="/time/calendar">
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
          <Navigation.Link to="/time/donation">
            Sick Leave Donation
          </Navigation.Link>
        </Navigation.Section>
      </Navigation>
    </AppLayout>
  )
}
