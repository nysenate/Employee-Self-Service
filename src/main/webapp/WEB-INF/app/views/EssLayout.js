import React, { useEffect, useState } from "react";
import EssNavBar from "app/core/EssNavBar/EssNavBar";
import { Navigate, Outlet } from "react-router-dom";
import useAuth from "app/contexts/Auth/useAuth";

export default function EssLayout() {
  const { isAuthed, isAuthLoading } = useAuth();

  if (isAuthLoading()) {
    return <></>;
  }

  if (!isAuthed()) {
    return <Navigate to="/login" />;
  }

  return (
    <div className="w-screen">
      <EssNavBar />
      <div className="mx-auto w-[1150px] pt-[70px]">
        <Outlet />
      </div>
    </div>
  );
}
