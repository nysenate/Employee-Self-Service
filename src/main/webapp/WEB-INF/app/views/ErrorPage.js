import React from 'react';
import { Button } from "app/components/Button";
import ErrorBanner from "app/components/ErrorBanner";
import { useNavigate } from "react-router-dom";

export default function ErrorPage({ error, resetErrorBoundary }) {
  const navigate = useNavigate();
  console.error(error);
  return (
    <ErrorBanner>
      <div className="text-3xl mb-3">
        Something went wrong
      </div>
      <p className="my-3">
        {error.message}
      </p>
      <div className="mt-3 flex justify-center gap-3">
        <Button color="error" onClick={() => navigate(0)}>
          Try Again
        </Button>
        <Button color="error" onClick={() => navigate("/logout")}>
          Logout
        </Button>
      </div>
    </ErrorBanner>
  )
}