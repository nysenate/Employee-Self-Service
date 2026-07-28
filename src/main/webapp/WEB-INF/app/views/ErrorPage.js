import React from "react";
import Button from "app/components/Button";
import ErrorBanner from "app/components/ErrorBanner";
import { useNavigate } from "react-router-dom";

export default function ErrorPage({ error, resetErrorBoundary }) {
  const navigate = useNavigate();
  console.error(error);
  return (
    <ErrorBanner>
      <div className="mb-3 text-3xl">Something went wrong</div>
      <p className="my-3">{error.message}</p>
      <div className="mt-3 flex justify-center gap-3">
        <Button variant="destructive" onPress={() => navigate(0)}>
          Try Again
        </Button>
        <Button
          variant="destructive"
          onPress={() => {
            resetErrorBoundary?.();
            navigate("/logout");
          }}
        >
          Logout
        </Button>
      </div>
    </ErrorBanner>
  );
}
