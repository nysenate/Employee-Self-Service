import React from 'react'
import { Outlet } from "react-router-dom";


export default function AppLayout({ children }) {
  return (
    <div className="flex flex-row justify-between">
      {children}
      <main className="w-[880px] flex-none">
        <Outlet/>
      </main>
    </div>
  )
}
