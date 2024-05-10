import React from "react"
import { ThemeContext, themes } from "app/contexts/ThemeContext";
import { Routes, Route, Navigate } from "react-router-dom";
import Navigation from "app/components/Navigation";
import AppLayout from "app/components/AppLayout";
import SummaryIndex from "app/views/myinfo/personnel/summary/SummaryIndex";


export default function MyInfoIndex() {
  return (
    <ThemeContext.Provider value={themes.myinfo}>
      <Routes>
        <Route path="" element={<MyInfoLayout/>}>
          <Route path="personnel/summary" element={<SummaryIndex/>}/>
          <Route path="personnel/emergency-alert-info" element={<h1>Emergency Alert Info</h1>}/>
          <Route path="personnel/todo" element={<h1>Personnel To-Do List</h1>}/>
          <Route path="payroll/checkhistory" element={<h1>Paycheck History</h1>}/>
          <Route path="" element={<Navigate to="personnel/summary" replace/>}/>
        </Route>
      </Routes>
    </ThemeContext.Provider>
  )
}

function MyInfoLayout() {
  return (
    <AppLayout>
      <Navigation>
        <Navigation.Title>
          My Info Menu
        </Navigation.Title>
        <Navigation.Section name="Personnel">
          <Navigation.Link to="/myinfo/personnel/summary">
            Current Info
          </Navigation.Link>
          <Navigation.Link to="/myinfo/personnel/emergency-alert-info">
            Emergency Alert Info
          </Navigation.Link>
          <Navigation.Link to="/myinfo/personnel/todo">
            To-Do List
          </Navigation.Link>
        </Navigation.Section>
        <Navigation.Section name="Payroll">
          <Navigation.Link to="/myinfo/payroll/checkhistory">
            Paycheck History
          </Navigation.Link>
        </Navigation.Section>
      </Navigation>
    </AppLayout>
  )
}
