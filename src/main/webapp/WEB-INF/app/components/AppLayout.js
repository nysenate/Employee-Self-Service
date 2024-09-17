import React from 'react'
import { Outlet } from "react-router-dom";
import ErrorPage from "app/views/ErrorPage";
import { ErrorBoundary } from "react-error-boundary";

export default function AppLayout({ children }) {
  return (
    <div className="flex flex-row justify-between">
      {children}
      <main className="w-[880px] flex-none">
        <ErrorBoundary FallbackComponent={ErrorPage}>
          <Outlet/>
        </ErrorBoundary>
      </main>
    </div>
  )
}
