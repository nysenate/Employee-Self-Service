import React from "react";
import { Outlet } from "react-router-dom";
import ErrorPage from "app/views/ErrorPage";
import { ErrorBoundary } from "react-error-boundary";

export default function AppLayout({ children }) {
  return (
    <>
      <div className="fixed">{children}</div>
      <main className="mb-[20px] ml-[270px] w-[880px]">
        <ErrorBoundary FallbackComponent={ErrorPage}>
          <Outlet />
        </ErrorBoundary>
      </main>
    </>
  );
}
