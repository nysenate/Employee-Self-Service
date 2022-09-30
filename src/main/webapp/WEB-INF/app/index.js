import React from 'react'
import "./app.css"
import MyInfo from "app/views/myinfo/MyInfo";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { createRoot } from "react-dom/client";
import TimeoutChecker from "app/core/TimeoutChecker/TimeoutChecker";
import Supply from "app/views/supply/Supply";
import Time from "app/views/time/Time";
import Travel from "app/views/travel/Travel";
import AppLayout from "app/AppLayout";


function App() {
  return (
    <BrowserRouter>
      <TimeoutChecker>
        <Routes>
          <Route path="/" element={<AppLayout/>}>
            <Route path="/myinfo/*" element={<MyInfo/>}/>
            <Route path="/time/*" element={<Time/>}/>
            <Route path="/supply/*" element={<Supply/>}/>
            <Route path="/travel/*" element={<Travel/>}/>
          </Route>
          <Route path="/login" element={<h1>Login</h1>}/>
          {/* TODO Login, Errors, etc */}
        </Routes>
      </TimeoutChecker>
    </BrowserRouter>
  )
}

const root = createRoot(document.getElementById('app'))
root.render(<App/>)
