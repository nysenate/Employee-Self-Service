import React from "react"
import { ThemeContext, themes } from "app/contexts/ThemeContext";
import { Routes, Route } from "react-router-dom";
import Navigation from "app/shared/Navigation";
import AppLayout from "app/shared/AppLayout";


// TODO remove Routes, instead use it so that loaders, etc can be used.
export default function MyInfo() {
  return (
    <ThemeContext.Provider value={themes.myinfo}>
      <Routes>
        <Route path="" element={<MyInfoLayout/>}>
          <Route path="personnel/summary" element={<h1>Personnel Summary</h1>}/>
          <Route path="personnel/emergency-alert-info" element={<h1>Emergency Alert Info</h1>}/>
          <Route path="personnel/todo" element={<h1>Personnel To-Do List</h1>}/>
          <Route path="payroll/checkhistory" element={<h1>Paycheck History</h1>}/>
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
            Paycheck History
          </Navigation.Link>
          <Navigation.Link to="/myinfo/personnel/emergency-alert-info">
            Emergency Alert Info
          </Navigation.Link>
        </Navigation.Section>
      </Navigation>
    </AppLayout>
  )
}
