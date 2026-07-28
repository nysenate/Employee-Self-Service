import React from "react";
import ErrorBanner from "app/components/ErrorBanner";

export default function NotFound() {
  return (
    <ErrorBanner>
      <h1 className="my-1 text-2xl font-semibold">Page Not Found</h1>
      <h3>Sorry the page that you requested doesn't seem to exist.</h3>
    </ErrorBanner>
  );
}
