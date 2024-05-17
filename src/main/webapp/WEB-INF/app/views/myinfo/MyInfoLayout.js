import React from "react"
import { ThemeContext, themes } from "app/contexts/ThemeContext";
import Navigation from "app/components/Navigation";
import AppLayout from "app/components/AppLayout";


export default function MyInfoLayout() {
  return (
    <ThemeContext.Provider value={themes.myinfo}>
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
    </ThemeContext.Provider>
  )
}
