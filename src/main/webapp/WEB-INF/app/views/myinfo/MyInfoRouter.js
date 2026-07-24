import React from "react";
import { ThemeContext, themes } from "app/ThemeContext";
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
import AssertPermission from "app/components/AssertPermission";
import NotFound from "app/views/NotFound";
import { AsyncBadge } from "app/components/Badge";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import { useTaskAssignments } from "app/views/myinfo/personnel/pec/useTaskAssignment";

export default function MyInfoRouter() {
  return (
    <ThemeContext.Provider value={themes.myinfo}>
      <Routes>
        <Route path="" element={<MyInfoLayout />}>
          <Route path="personnel/summary" element={<SummaryIndex />} />
          <Route
            path="personnel/emergency-alert-info"
            element={<EmergencyAlertInfoIndex />}
          />
          <Route
            path="personnel/tasks/assignments"
            element={<TaskAssignmentsListIndex />}
          />
          <Route
            path="personnel/tasks/assignments/:taskId"
            element={<TaskAssignmentIndex />}
          />
          <Route
            path="personnel/todo/report"
            element={
              <AssertPermission permission="core:pec-report-generation">
                <ToDoReporting />
              </AssertPermission>
            }
          />
          <Route
            path="personnel/todo/assignment"
            element={
              <AssertPermission permission="core:pec-report-generation">
                <ToDoAssignment />
              </AssertPermission>
            }
          />
          <Route path="payroll/checkhistory" element={<CheckHistoryIndex />} />
          <Route
            path=""
            element={<Navigate to="personnel/summary" replace />}
          />
          <Route path="*" element={<NotFound />} />
        </Route>
      </Routes>
    </ThemeContext.Provider>
  );
}

function MyInfoLayout() {
  const { data: user } = useRequireAuthedUser();

  return (
    <AppLayout>
      <Navigation>
        <Navigation.Title>My Info Menu</Navigation.Title>
        <Navigation.Section name="Personnel">
          <Navigation.Link to="/myinfo/personnel/summary">
            Current Info
          </Navigation.Link>
          <Navigation.Link to="/myinfo/personnel/emergency-alert-info">
            Emergency Alert Info
          </Navigation.Link>
          <Navigation.Link to="/myinfo/personnel/tasks/assignments">
            <span className="mr-auto">To-Do List</span>
            <AsyncBadge
              useData={useTaskAssignments}
              queryArgs={[user?.employeeId]}
              selectCount={countIncompleteAssignments}
              hideWhenZero
              title="Personnel tasks needing attention"
            />
          </Navigation.Link>
          <Navigation.Link
            to="/myinfo/personnel/todo/report"
            permission="core:pec-report-generation"
          >
            To-Do Reporting
          </Navigation.Link>
          <Navigation.Link
            to="/myinfo/personnel/todo/assignment"
            permission="core:pec-report-generation"
          >
            To-Do Assignment
          </Navigation.Link>
        </Navigation.Section>
        <Navigation.Section name="Payroll">
          <Navigation.Link to="/myinfo/payroll/checkhistory">
            Paycheck History
          </Navigation.Link>
        </Navigation.Section>
      </Navigation>
    </AppLayout>
  );
}

function countIncompleteAssignments(assignments) {
  return assignments.filter(
    (assignment) =>
      !assignment.completed && assignment.active && assignment.task?.active,
  ).length;
}
