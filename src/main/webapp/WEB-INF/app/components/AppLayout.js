import React from 'react'
import { Outlet, useNavigation } from "react-router-dom";
import ErrorPage from "app/views/ErrorPage";
import { ErrorBoundary } from "react-error-boundary";
import { useNavigate } from "react-router-dom";

export default function AppLayout({ children }) {
  const navigate = useNavigate()

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
