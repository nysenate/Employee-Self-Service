import React from "react"
import { ThemeContext, themes } from "app/contexts/ThemeContext";
import Navigation from "app/components/Navigation";
import AppLayout from "app/components/AppLayout";
import { Navigate, Route, Routes } from "react-router-dom";
import SummaryIndex from "app/views/myinfo/personnel/summary/SummaryIndex";
import CheckHistoryIndex from "app/views/myinfo/payroll/checkhistory/CheckHistoryIndex";
import EmergencyAlertInfoIndex from "app/views/myinfo/personnel/emergency-alert-info/EmergencyAlertInfoIndex";
import ToDoReporting from "app/views/myinfo/personnel/to-do-reporting/ToDoReporting";
import TaskAssignmentsListIndex
  from "app/views/myinfo/personnel/task-assignments/assignment-list/TaskAssignmentsListIndex";
import DocumentAcknowledgeAssignment
  from "app/views/myinfo/personnel/task-assignments/assignment-item/document-assignment/DocumentAcknowledgeAssignment";
import VideoAssignment
  from "app/views/myinfo/personnel/task-assignments/assignment-item/video-assignment/VideoAssignment";
import MoodleCourse from "app/views/myinfo/personnel/task-assignments/MoodleCourse";
import EthicsCourse from "app/views/myinfo/personnel/task-assignments/EthicsCourse";
import EthicsLiveCourse from "app/views/myinfo/personnel/task-assignments/EthicsLiveCourse";
import TaskAssignmentIndex from "app/views/myinfo/personnel/task-assignments/assignment-item/TaskAssignmentIndex";

export default function MyInfoRouter() {
  return (
    <ThemeContext.Provider value={themes.myinfo}>
      <Routes>
        <Route path="" element={<MyInfoLayout/>}>
          <Route path="personnel/summary" element={<SummaryIndex/>}/>
          <Route path="personnel/emergency-alert-info" element={<EmergencyAlertInfoIndex/>}/>
          <Route path="personnel/tasks/assignments" element={<TaskAssignmentsListIndex/>}/>
          <Route path="personnel/tasks/assignments/:taskId" element={<TaskAssignmentIndex/>}/>
          {/*<Route path="" element={<TaskAssignmentsIndex/>}/>*/}
          {/*<Route path="acknowledgment/:taskId" element={<AcknowledgmentAssignment/>}/>*/}
          {/*<Route path="video/:taskId" element={<VideoCourse/>}/>*/}
          {/*<Route path="legethics/:taskId" element={<MoodleCourse/>}/>*/}
          {/*<Route path="ethicscourse/:taskId" element={<EthicsCourse/>}/>*/}
          {/*<Route path="ethicslivecourse/:taskId" element={<EthicsLiveCourse/>}/>*/}
          <Route path="personnel/todo-report" element={<ToDoReporting/>}/>
          <Route path="payroll/checkhistory" element={<CheckHistoryIndex/>}/>
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
          <Navigation.Link to="/myinfo/personnel/tasks/assignments">
            To-Do List
          </Navigation.Link>
          <Navigation.Link to="/myinfo/personnel/todo-report">
            To-Do Reporting
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
