import React from 'react'
import EssNavBar from "app/core/EssNavBar/EssNavBar";
import { Navigate, Outlet } from "react-router-dom";
import useAuth from "app/contexts/Auth/useAuth";


export default function EssLayout() {
  const auth = useAuth()

  if (auth.isAuthed()) {
    return (
      <div className="w-screen">
        <EssNavBar/>
        <div className="w-[1150px] pt-[70px] mx-auto">
          <Outlet/>
        </div>
      </div>
    )
  } else {
    return <Navigate to="/login"/>
  }
}