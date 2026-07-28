import React from "react";
import EssNavBar from "app/components/EssNavBar";
import { Outlet } from "react-router-dom";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import TimeoutChecker from "app/TimeoutChecker";
import LoadingIndicator from "app/components/LoadingIndicator";
import ErrorPage from "app/views/ErrorPage";

export default function EssLayout() {
  const { error, isPending, isError, isFetching } = useRequireAuthedUser();

  // Do not mount protected routes while the user's session is being verified.
  // This prevents their queries from starting based only on cached user data.
  if (isPending || isFetching) {
    return <LoadingIndicator />;
  }

  if (isError) {
    return <ErrorPage error={error} />;
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
