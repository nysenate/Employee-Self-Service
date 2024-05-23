import {
  createBrowserRouter,
  createRoutesFromElements, Navigate, Outlet,
  Route,
  RouterProvider,
} from "react-router-dom";
import { AuthProvider } from "app/core/Auth/useAuth";
import EssLayout from "app/core/EssLayout";
import Time from "app/views/time/Time";
import Supply from "app/views/supply/Supply";
import Travel from "app/views/travel/Travel";
import LoginIndex from "app/views/login/LoginIndex";
import React from "react";
import SummaryIndex, { summaryLoader } from "app/views/myinfo/personnel/summary/SummaryIndex";
import MyInfoLayout from "app/views/myinfo/MyInfoLayout";
import EmergencyAlertIndex, {
  emergencyAlertLoader
} from "app/views/myinfo/personnel/emergency-alert-info/EmergencyAlertInfoIndex";

const RoutesJSX = (
  <>
    <Route path="/" element={<EssLayout/>}>

      <Route path="/myinfo" element={<MyInfoLayout/>}>
        <Route path="/myinfo/personnel/summary"
               element={<SummaryIndex/>}
               loader={summaryLoader}
        />
        <Route path="/myinfo/personnel/emergency-alert-info"
               element={<EmergencyAlertIndex/>}
               loader={emergencyAlertLoader}
        />
        <Route path="/myinfo/personnel/todo" element={<h1>Personnel To-Do List</h1>}/>
        <Route path="/myinfo/payroll/checkhistory" element={<h1>Paycheck History</h1>}/>
        <Route index element={<Navigate to="/myinfo/personnel/summary" replace/>}/>
      </Route>

      <Route path="/time/*" element={<Time/>}/>
      <Route path="/supply/*" element={<Supply/>}/>
      <Route path="/travel/*" element={<Travel/>}/>
    </Route>
    <Route element={<Outlet/>}>
      <Route path="/login" element={<LoginIndex/>}/>
    </Route>
  </>
)

const routes = createRoutesFromElements(RoutesJSX);
const router = createBrowserRouter(routes);

function App() {
  return (
    <AuthProvider>
      {/*<TimeoutChecker>*/}
      <RouterProvider router={router}/>
      {/*</TimeoutChecker>*/}
    </AuthProvider>
  )
}

export default App;
