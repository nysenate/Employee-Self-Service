import React from "react";
import ErrorAlert from "app/components/ErrorAlert";

export default function NotFound() {
  return (
    <ErrorAlert headingAs="h1" title="Page not found">
      <p>Sorry, the page you requested doesn’t seem to exist.</p>
    </ErrorAlert>
  );
}
