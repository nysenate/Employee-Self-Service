import React, { lazy, StrictMode, Suspense } from "react";
import "./app.css";
import { createRoot } from "react-dom/client";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import EssLayout from "app/views/EssLayout";
import LoginIndex from "app/views/login/LoginIndex";
import Logout from "app/views/logout/Logout";
import EssIndex from "app/views/EssIndex";
import NotFound from "app/views/NotFound";
import LoadingIndicator from "app/components/LoadingIndicator";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { ErrorBoundary } from "react-error-boundary";
import ErrorPage from "app/views/ErrorPage";
import { useConfig } from "app/hooks/useConfig";

const MyInfoRouter = lazy(() =>
  import(
    /* webpackChunkName: "myinfo" */ "app/views/myinfo/MyInfoRouter"
  ),
);
const TimeRouter = lazy(() =>
  import(/* webpackChunkName: "time" */ "app/views/time/TimeRouter"),
);
const SupplyRouter = lazy(() =>
  import(/* webpackChunkName: "supply" */ "app/views/supply/SupplyRouter"),
);
const TravelRouter = lazy(() =>
  import(/* webpackChunkName: "travel" */ "app/views/travel/TravelRouter"),
);

function App() {
  return (
    <BrowserRouter>
      <ErrorBoundary FallbackComponent={ErrorPage}>
        <Routes>
          <Route path="/" element={<EssIndex />} />
          <Route path="/" element={<EssLayout />}>
            <Route
              path="/myinfo/*"
              element={
                <ApplicationLoader>
                  <MyInfoRouter />
                </ApplicationLoader>
              }
            />
            <Route
              path="/time/*"
              element={
                <ApplicationLoader>
                  <TimeRouter />
                </ApplicationLoader>
              }
            />
            <Route
              path="/supply/*"
              element={
                <ApplicationLoader>
                  <SupplyRouter />
                </ApplicationLoader>
              }
            />
            <Route
              path="/travel/*"
              element={
                <ApplicationLoader>
                  <TravelRouter />
                </ApplicationLoader>
              }
            />
          </Route>
          <Route path="/login" element={<LoginIndex />} />
          <Route path="/logout" element={<Logout />} />
          <Route path="/error" element={<ErrorPagePreview />} />
          <Route path="/404" element={<NotFound />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </ErrorBoundary>
    </BrowserRouter>
  );
}

function ApplicationLoader({ children }) {
  return <Suspense fallback={<LoadingIndicator />}>{children}</Suspense>;
}

function ErrorPagePreview() {
  const { data: config, isPending } = useConfig();

  if (isPending) {
    return <LoadingIndicator />;
  }

  if (config?.runtimeLevel !== "dev") {
    return <Navigate to="/404" replace />;
  }

  return (
    <ErrorPage
      error={new Error("An unexpected error occurred while loading this page.")}
    />
  );
}

const queryClient = new QueryClient();
const root = createRoot(document.getElementById("app"));
root.render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <App />
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  </StrictMode>,
);
