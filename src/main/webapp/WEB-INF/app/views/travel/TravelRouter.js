import React from "react";
import { ThemeContext, themes } from "app/contexts/ThemeContext";
import Navigation from "app/components/Navigation";
import AppLayout from "app/components/AppLayout";
import { Navigate, Route, Routes } from "react-router-dom";
import SummaryIndex from "app/views/myinfo/personnel/summary/SummaryIndex";
import CheckHistoryIndex from "app/views/myinfo/payroll/checkhistory/CheckHistoryIndex";
import EmergencyAlertInfoIndex from "app/views/myinfo/personnel/emergency-alert-info/EmergencyAlertInfoIndex";
import TaskAssignmentsListIndex from "app/views/myinfo/personnel/pec/task-assignments/assignment-list/TaskAssignmentsListIndex";
import TaskAssignmentIndex from "app/views/myinfo/personnel/pec/task-assignments/assignment-item/TaskAssignmentIndex";
import ToDoAssignment from "./personnel/pec/to-do-assignment/ToDoAssignment";
import ToDoReporting from "./personnel/pec/to-do-reporting/ToDoReporting";
import Travel from "app/views/travel/Travel";

export default function TravelRouter() {
  return (
    <ThemeContext.Provider value={themes.travel}>
      <Routes>
        <Route path="" element={<TravelLayout />}>
          <Route path="/stats" element={<Travel />} />
        </Route>
      </Routes>
    </ThemeContext.Provider>
  );
}

function TravelLayout() {
  return (
    <AppLayout>
      <Navigation>
        <Navigation.Title>Travel Menu</Navigation.Title>
        <Navigation.Section name="My Travel">
          <Navigation.Link to="/travel/stats">Statistics</Navigation.Link>
        </Navigation.Section>
      </Navigation>
    </AppLayout>
  );
}
