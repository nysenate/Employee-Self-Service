import React, { Suspense } from "react";
import EssNavBar from "app/components/EssNavBar";
import { Outlet } from "react-router-dom";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import LoadingIndicator from "app/components/LoadingIndicator";
import TimeoutChecker from "app/TimeoutChecker";
import ErrorPage from "app/views/ErrorPage";

export default function EssLayout() {
  const { error, isPending, isError } = useRequireAuthedUser();

  // Only the initial load blocks rendering. Background refetches of the authed user
  // (the query goes stale after 30s and refetches on window focus) must not unmount
  // the app, which would discard all page state and re-render everything.
  if (isPending) {
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
          <Suspense fallback={<LoadingIndicator />}>
            <Outlet />
          </Suspense>
        </div>
      </TimeoutChecker>
    </div>
  );
}
