import React from 'react'
import EssNavBar from "app/core/EssNavBar/EssNavBar";
import { Outlet } from "react-router-dom";


export default function AppLayout() {
  return (
    <div className="w-screen">
      <EssNavBar/>
      <div className="w-[1150px] pt-[70px] mx-auto">
        <Outlet/>
      </div>
    </div>
  )
}