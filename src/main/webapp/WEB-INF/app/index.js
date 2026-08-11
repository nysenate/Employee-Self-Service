import React, { lazy, StrictMode } from "react";
import "./app.css";
import { createRoot } from "react-dom/client";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import EssLayout from "app/views/EssLayout";
import LoginIndex from "app/views/login/LoginIndex";
import Logout from "app/views/logout/Logout";
import EssIndex from "app/views/EssIndex";
import NotFound from "app/views/NotFound";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";

// Each module is a separate chunk so that, for example, a user on Time does not
// download Supply, Travel and MyInfo up front. EssLayout provides the Suspense
// boundary these resolve into.
const TimeRouter = lazy(() => import("app/views/time/TimeRouter"));
const MyInfoRouter = lazy(() => import("app/views/myinfo/MyInfoRouter"));
const SupplyRouter = lazy(() => import("app/views/supply/SupplyRouter"));
const TravelRouter = lazy(() => import("app/views/travel/TravelRouter"));

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<EssIndex />} />
        <Route path="/" element={<EssLayout />}>
          <Route path="/myinfo/*" element={<MyInfoRouter />} />
          <Route path="/time/*" element={<TimeRouter />} />
          <Route path="/supply/*" element={<SupplyRouter />} />
          <Route path="/travel/*" element={<TravelRouter />} />
        </Route>
        <Route path="/login" element={<LoginIndex />} />
        <Route path="/logout" element={<Logout />} />
        <Route path="/404" element={<NotFound />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
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
