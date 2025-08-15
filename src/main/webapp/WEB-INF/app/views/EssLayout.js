import React, { useEffect, useState } from "react";
import EssNavBar from "app/core/EssNavBar/EssNavBar";
import { Outlet } from "react-router-dom";
import useAuthedUser from "app/core/useAuthedUser";

export default function EssLayout() {
  const { data: user, isPending } = useAuthedUser();

  if (isPending) {
    return <></>;
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
