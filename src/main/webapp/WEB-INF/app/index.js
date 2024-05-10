import React from 'react'
import "./app.css"
import MyInfoIndex from "app/views/myinfo/MyInfoIndex";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { createRoot } from "react-dom/client";
import TimeoutChecker from "app/core/TimeoutChecker/TimeoutChecker";
import Supply from "app/views/supply/Supply";
import Time from "app/views/time/Time";
import Travel from "app/views/travel/Travel";
import EssLayout from "app/core/EssLayout";
import LoginIndex from "app/views/login/LoginIndex";
import { AuthProvider } from "app/core/Auth/useAuth";


function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        {/*<TimeoutChecker>*/}
        <Routes>
          <Route path="/" element={<EssLayout/>}>
            <Route path="/myinfo/*" element={<MyInfoIndex/>}/>
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
