import React from 'react'
import { Outlet, useNavigation } from "react-router-dom";
import LoadingCircle from "app/components/LoadingCircle";
import LoadingIndicator from "app/components/LoadingIndicator";


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
