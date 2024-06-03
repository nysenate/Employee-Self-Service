import React, { StrictMode } from 'react'
import "./app.css"
import { createRoot } from "react-dom/client";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { AuthProvider } from "app/contexts/Auth/useAuth";
import EssLayout from "app/views/EssLayout";
import Time from "app/views/time/Time";
import Travel from "app/views/travel/Travel";
import LoginIndex from "app/views/login/LoginIndex";
import MyInfoRouter from "app/views/myinfo/MyInfoRouter";
import SupplyRouter from "app/views/supply/SupplyRouter";
import Logout from "app/views/logout/Logout";


function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        {/*<TimeoutChecker>*/}
        <Routes>
          <Route path="/" element={<EssLayout/>}>
            <Route path="/myinfo/*" element={<MyInfoRouter/>}/>
            <Route path="/time/*" element={<Time/>}/>
            <Route path="/supply/*" element={<SupplyRouter/>}/>
            <Route path="/travel/*" element={<Travel/>}/>
          </Route>
          <Route path="/login" element={<LoginIndex/>}/>
          <Route path="/logout" element={<Logout/>}/>
          {/* TODO Errors page, 404 page. */}
        </Routes>
        {/*</TimeoutChecker>*/}
      </AuthProvider>
    </BrowserRouter>
  )
}

const root = createRoot(document.getElementById('app'))
root.render(
  <StrictMode>
    <App/>
  </StrictMode>
)
