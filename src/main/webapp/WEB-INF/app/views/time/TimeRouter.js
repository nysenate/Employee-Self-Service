import React from 'react'
import { ThemeContext, themes } from "app/contexts/ThemeContext";
import Navigation from "app/components/Navigation";
import AppLayout from "app/components/AppLayout";
import { Navigate, Route, Routes } from "react-router-dom";

import RecordEntryDirective from "app/views/time/record/RecordEntryDirective";
import RecordHistoryIndex from "./record/RecordHistoryIndex.js";
import AllowanceStatusIndex from "app/views/time/allowance/AllowanceStatusIndex";
import CalendarIndex from "./period/CalendarIndex";
import AccrualHistoryIndex from "./accrual/AccrualHistoryIndex.js";
import AccrualProjections from "./accrual/AccrualProjectionsIndex.js";
import Donation from "app/views/time/accrual/Donation";
// review time records record/manage
import RecordEmpHistoryIndex from "./record/RecordEmpHistoryIndex.js";
import AllowanceEmpStatusIndex from "app/views/time/allowance/AllowanceEmpStatusIndex";
import AccrualEmpHistoryIndex from "./accrual/AccrualEmpHistoryIndex.js";
import AccrualEmpProjectionsIndex from "./accrual/AccrualEmpProjectionsIndex.js";
import Grant from "app/views/time/record/Grant";
import PersonnelSearchIndex from "app/views/time/personnel/PersonnelSearchIndex";

export default function TimeRouter() {
  return (
    <ThemeContext.Provider value={themes.time}>
      <Routes>
        <Route path="" element={<TimeLayout/>}>
          <Route path="record/entry" element={<RecordEntryDirective/>}/>
          <Route path="record/history" element={<RecordHistoryIndex/>}/>
          <Route path="allowance/status" element={<AllowanceStatusIndex/>}/>
          <Route path="period/calendar" element={<CalendarIndex/>}/>
          <Route path="accrual/history" element={<AccrualHistoryIndex/>}/>
          <Route path="accrual/projections" element={<AccrualProjections/>}/>
          <Route path="accrual/donation" element={<Donation/>}/>
          {/*<Route path="record/manage" element={<SickLeaveDonation/>}/>*/}
          <Route path="record/emphistory" element={<RecordEmpHistoryIndex/>}/>
          <Route path="allowance/emp-status" element={<AllowanceEmpStatusIndex/>}/>
          <Route path="accrual/emphistory" element={<AccrualEmpHistoryIndex/>}/>
          <Route path="accrual/emp-projections" element={<AccrualEmpProjectionsIndex/>}/>
          <Route path="record/grant" element={<Grant/>}/>
          <Route path="personnel/search" element={<PersonnelSearchIndex/>}/>
          <Route path="" element={<Navigate to="record/entry" replace/>}/>
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
          <Navigation.Link to="/time/allowance/status">
            Allowed Hours
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
  )
}
