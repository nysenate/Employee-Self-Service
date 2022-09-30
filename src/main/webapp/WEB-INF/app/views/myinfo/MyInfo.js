import React, { useContext } from "react"
import { ThemeContext, themes } from "app/contexts/ThemeContext";
import { Routes, Route, Outlet } from "react-router-dom";


// TODO remove Routes, instead use it so that loaders, etc can be used.
export default function MyInfo() {
  return (
    <ThemeContext.Provider value={themes.myinfo}>
      <Routes>
        <Route path="" element={<MyInfoLayout/>}>
          <Route path="personnel/summary" element={<h1>Personnel Summary</h1>}/>
          <Route path="personnel/emergency-alert-info" element={<h1>Emergency Alert Info</h1>}/>
          <Route path="personnel/todo" element={<h1>Personnel To-Do List</h1>}/>
          <Route path="payroll/checkhistory" element={<h1>Paycheck History</h1>}/>
        </Route>
      </Routes>
    </ThemeContext.Provider>
  )
}

function MyInfoLayout() {
  return (
    <div className="flex flex-row justify-between">
      <NavigationMenu>
        <Hero>
          My Info Menu
        </Hero>
        Paycheck
      </NavigationMenu>
      <AppContent>
        <Outlet/>
      </AppContent>
    </div>
  )
}

function NavigationMenu({ children }) {
  return (
    <nav className="w-[250px] flex-none">
      <Card>
        {children}
      </Card>
    </nav>
  )
}

function AppContent({ children }) {
  return (
    <main className="w-[880px] flex-none">
      {children}
    </main>
  )
}

function Card({ children }) {
  return (
    <div className="bg-white shadow">
      {children}
    </div>
  )
}

function Hero({ children }) {
  return (
    <div className="px-3 py-2 bg-blue-800 text-base text-white font-medium">
      {children}
    </div>
  )
}
