import React, { useEffect, useState } from "react";
import EssNavBar from "app/components/EssNavBar";
import { Outlet } from "react-router-dom";
import useAuthedUser from "app/hooks/useAuthedUser";
import TimeoutChecker from "app/TimeoutChecker";

export default function EssLayout() {
  const { data: user, isPending } = useAuthedUser();

  if (isPending) {
    return <></>;
  }

  return (
    <div className="w-screen">
      <TimeoutChecker>
        <EssNavBar />
        <div className="mx-auto w-[1150px] pt-[70px]">
          <Outlet />
        </div>
      </TimeoutChecker>
    </div>
  );
}
