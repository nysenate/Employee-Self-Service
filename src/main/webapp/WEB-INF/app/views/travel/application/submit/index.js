import React from "react";
import Hero from "app/components/Hero";
import Button from "app/components/Button";
import ErrorBanner from "app/components/ErrorBanner";
import LoadingIndicator from "app/components/LoadingIndicator";
import NewTravelApplication from "./NewTravelApplication";
import { useNewTravelDraft } from "./hooks/useNewTravelDraft";

export default function SubmitApplication() {
  const draftQuery = useNewTravelDraft();

  return (
    <div className="space-y-5">
      <Hero>New Travel Application</Hero>
      {draftQuery.isPending && <InitializationLoading />}
      {draftQuery.isError && (
        <InitializationError
          error={draftQuery.error}
          retry={draftQuery.refetch}
        />
      )}
      {draftQuery.isSuccess && <NewTravelApplication draft={draftQuery.data} />}
    </div>
  );
}

function InitializationLoading() {
  return (
    <div
      className="flex min-h-48 flex-col items-center justify-center gap-3"
      role="status"
    >
      <LoadingIndicator />
      <span>Preparing your travel application…</span>
    </div>
  );
}

function InitializationError({ error, retry }) {
  const missingDepartment = error?.data?.errorCode === "MISSING_DEPARTMENT";

  return (
    <ErrorBanner>
      <h2 className="text-lg font-semibold">
        {missingDepartment
          ? "Department information is missing"
          : "We couldn’t start your travel application"}
      </h2>
      <p className="mx-auto mt-2 max-w-2xl">
        {missingDepartment
          ? "ESS could not determine your department. Contact your personnel office before starting a travel application."
          : "The application could not be initialized. Your information has not been changed. Please try again."}
      </p>
      {!missingDepartment && (
        <Button className="mt-4" onPress={retry}>
          Try again
        </Button>
      )}
    </ErrorBanner>
  );
}
