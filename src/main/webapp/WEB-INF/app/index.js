import React, { StrictMode } from 'react'
import "./app.css"
import { createRoot } from "react-dom/client";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { AuthProvider } from "app/core/Auth/useAuth";
import EssLayout from "app/core/EssLayout";
import Time from "app/views/time/Time";
import Supply from "app/views/supply/Supply";
import Travel from "app/views/travel/Travel";
import LoginIndex from "app/views/login/LoginIndex";
import MyInfoRouter from "app/views/myinfo/MyInfoRouter";


function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        {/*<TimeoutChecker>*/}
        <Routes>
          <Route path="/" element={<EssLayout/>}>
            <Route path="/myinfo/*" element={<MyInfoRouter/>}/>
            <Route path="/time/*" element={<Time/>}/>
            <Route path="/supply/*" element={<Supply/>}/>
            <Route path="/travel/*" element={<Travel/>}/>
          </Route>
          <Route path="/login" element={<LoginIndex/>}/>
          {/* TODO Login, Errors, etc */}
        </Routes>
        {/*</TimeoutChecker>*/}
      </AuthProvider>
    </BrowserRouter>
  )
}

const root = createRoot(document.getElementById('app'))
root.render(<App/>)
