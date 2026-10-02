import React from "react";
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

  // Do not mount protected routes until the user's session is initially verified.
  // Background verification must leave them mounted so local form state survives.
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
              <ErrorAlert
                title="Trouble connecting to ESS"
                className="mb-5"
              >
                <p>
                  Keep this page open to avoid losing unsaved changes.
                </p>
                <Button
                  className="mt-3"
                  onPress={() => refetch()}
                  isPending={isFetching}
                >
                  Reconnect to ESS
                </Button>
              </ErrorAlert>
            )}
            <Outlet />
          </div>
        </TimeoutChecker>
      </div>
    </NotificationProvider>
  );
}
