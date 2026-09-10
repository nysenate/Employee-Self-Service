import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import Hero from "app/components/Hero";
import Button from "app/components/Button";
import ErrorAlert from "app/components/ErrorAlert";
import LoadingIndicator from "app/components/LoadingIndicator";
import NewTravelApplication from "./NewTravelApplication";
import { useTravelDraft } from "./hooks/useTravelDraft";

export default function SubmitApplication() {
  const { draftId } = useParams();
  const isResuming = draftId !== undefined;
  const draftQuery = useTravelDraft(draftId);

  return (
    <div className="space-y-5">
      <Hero>
        {isResuming ? "Continue Travel Application" : "New Travel Application"}
      </Hero>
      {draftQuery.isPending && (
        <InitializationLoading isResuming={isResuming} />
      )}
      {draftQuery.isError && (
        <InitializationError
          error={draftQuery.error}
          retry={draftQuery.refetch}
          isResuming={isResuming}
        />
      )}
      {draftQuery.isSuccess && <NewTravelApplication draft={draftQuery.data} />}
    </div>
  );
}

function InitializationLoading({ isResuming }) {
  return (
    <div
      className="flex min-h-48 flex-col items-center justify-center gap-3"
      role="status"
    >
      <LoadingIndicator />
      <span>
        {isResuming
          ? "Loading your travel application…"
          : "Preparing your travel application…"}
      </span>
    </div>
  );
}

function InitializationError({ error, retry, isResuming }) {
  const navigate = useNavigate();
  const missingDepartment = error?.data?.errorCode === "MISSING_DEPARTMENT";
  const title = missingDepartment
    ? "Department information is missing"
    : isResuming
      ? "We couldn’t load this travel application"
      : "We couldn’t start your travel application";

  return (
    <ErrorAlert title={title}>
      <p className="max-w-2xl">
        {missingDepartment
          ? "ESS could not determine your department. Contact your personnel office before starting a travel application."
          : isResuming
            ? "The saved draft may no longer be available. Your information has not been changed. Please try again or return to your drafts."
            : "The application could not be initialized. Your information has not been changed. Please try again."}
      </p>
      {!missingDepartment && (
        <div className="mt-4 flex flex-wrap gap-3">
          <Button onPress={retry}>Try again</Button>
          {isResuming && (
            <Button
              variant="secondary"
              onPress={() => navigate("/travel/applications/drafts")}
            >
              Return to drafts
            </Button>
          )}
        </div>
      )}
    </ErrorAlert>
  );
}
