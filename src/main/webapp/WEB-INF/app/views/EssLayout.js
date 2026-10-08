import React, { Suspense } from "react";
import EssNavBar from "app/components/EssNavBar";
import { Outlet } from "react-router-dom";
import useRequireAuthedUser, {
  isTransientAuthError,
} from "app/hooks/useRequireAuthedUser";
import TimeoutChecker from "app/TimeoutChecker";
import LoadingStatus from "app/components/LoadingStatus";
import ErrorPage from "app/views/ErrorPage";
import NotificationProvider from "app/components/NotificationProvider";
import ErrorAlert from "app/components/ErrorAlert";
import Button from "app/components/Button";

export default function EssLayout() {
  const {
    data: user,
    error,
    isPending,
    isError,
    isFetching,
    refetch,
  } = useRequireAuthedUser();

  // Only the initial load blocks rendering. Background refetches of the authed user
  // (the query goes stale after 30s and refetches on window focus) must not unmount
  // the app, which would discard all page state and re-render everything.
  if (isPending) {
    return (
      <LoadingStatus
        message="Checking your session…"
        layout="centered"
        size="lg"
        className="min-h-48 p-6"
      />
    );
  }

  if (isError && (!user || !isTransientAuthError(error))) {
    return <ErrorPage error={error} />;
  }

  return (
    <NotificationProvider>
      <div className="w-screen">
        <TimeoutChecker>
          <EssNavBar />
          <div className="mx-auto w-[1150px] pt-[70px]">
            {isError && (
              <ErrorAlert title="Trouble connecting to ESS" className="mb-5">
                <p>Keep this page open to avoid losing unsaved changes.</p>
                <Button
                  className="mt-3"
                  onPress={() => refetch()}
                  isPending={isFetching}
                >
                  Reconnect to ESS
                </Button>
              </ErrorAlert>
            )}
            <Suspense
              fallback={
                <LoadingStatus
                  message="Loading page…"
                  layout="centered"
                  size="lg"
                  className="min-h-48 p-6"
                />
              }
            >
              <Outlet />
            </Suspense>
          </div>
        </TimeoutChecker>
      </div>
    </NotificationProvider>
  );
}
