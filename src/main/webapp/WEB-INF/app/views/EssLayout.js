import React from "react";
import EssNavBar from "app/components/EssNavBar";
import { Outlet } from "react-router-dom";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import TimeoutChecker from "app/TimeoutChecker";

export default function EssLayout() {
  const { isPending, isError, isFetching } = useRequireAuthedUser();

  if (isPending || isFetching || isError) {
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
