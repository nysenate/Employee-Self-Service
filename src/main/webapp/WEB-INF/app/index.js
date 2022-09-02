import React from 'react'
import "./app.css"
import MyInfo from "app/views/myinfo/MyInfo";
import { BrowserRouter, NavLink } from "react-router-dom";
import { ThemeContext, themes } from "app/contexts/ThemeContext";
import { createRoot } from "react-dom/client";
import EssNavBar from "app/shared/core/EssNavBar";


function App() {
  return (
    <div>
      <BrowserRouter>
        <EssNavBar/>
        <div className="w-[1150px] pt-[70px] mx-auto">
          <ThemeContext.Provider value={themes.myinfo}>
            <MyInfo/>
          </ThemeContext.Provider>
        </div>
      </BrowserRouter>
    </div>
  )
}

const root = createRoot(document.getElementById('app'))
root.render(<App />)
