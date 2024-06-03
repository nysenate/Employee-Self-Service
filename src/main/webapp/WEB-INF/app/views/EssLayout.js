import React from 'react'
import EssNavBar from "app/core/EssNavBar/EssNavBar";
import { Outlet } from "react-router-dom";
import PrivateContent from "app/core/PrivateContent";


export default function EssLayout() {
  return (
    <div className="w-screen">
      <PrivateContent>
        <EssNavBar/>
        <div className="w-[1150px] pt-[70px] mx-auto">
          <Outlet/>
        </div>
      </PrivateContent>
    </div>
  )
}