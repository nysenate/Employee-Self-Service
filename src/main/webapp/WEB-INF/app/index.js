import React from 'react'
import ReactDOM from 'react-dom'
import "./app.css"
import MyInfo from "app/views/myinfo/MyInfo";
import { BrowserRouter, NavLink } from "react-router-dom";

function App() {
  return (
    <div>
      <BrowserRouter>
        <EssNavBar/>
        <div className="w-[1150px] pt-[70px] mx-auto">
          <MyInfo/>
        </div>
      </BrowserRouter>
    </div>
  )
}

ReactDOM.render(<App/>, document.getElementById('app'))

function EssNavBar() {
  return (
    <nav className="fixed h-[45px] w-full bg-gray-50 shadow">
      <div className="w-[1150px] h-full mx-auto">
        <div className="h-full flex justify-between items-center">
          <div className="h-full ml-2">
            <img src="/assets/img/nysslogo.png" alt="logo" className="inline-block h-[35px] w-[35px]"/>
            <div className="ml-3 mr-6 inline-block">
              <span className="text-lg font-medium">NYS</span>&nbsp;
              <span className="text-lg font-light">ESS</span>
            </div>
            <ul className="inline-block h-full">
              <li className="leading-[40px] inline-block h-full">
                <AppLink name="My Info" to="myinfo" />
              </li>
              <li className="leading-[40px] inline">
                <AppLink name="Time & Attendance" to="time" />
              </li>
              <li className="leading-[40px] inline">
                <AppLink name="Supply" to="supply" />
              </li>
              <li className="leading-[40px] inline">
                <AppLink name="Travel" to="travel" />
              </li>
            </ul>
          </div>
          <div>
            Kevin
          </div>
        </div>
      </div>
    </nav>
  )
}

function AppLink({to, name}) {
  const themeColor = '#175B81'
  const classes = "px-6"
  const activeClasses = "border-b-[3px] border-blue-700 inline-block h-full"
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        isActive ? `${classes} ${activeClasses}` : `${classes}`
      }
    >
      {name}
    </NavLink>
  )
}