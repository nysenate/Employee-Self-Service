import React from "react";
import useCheckPermission from "app/hooks/useCheckPermission";
import ErrorAlert from "app/components/ErrorAlert";
import Button from "app/components/Button";
import LoadingIndicator from "app/components/LoadingIndicator";

// Gate mounting, not just display: denied pages must not run queries or effects.
// Resource ownership and mutation authorization must still be enforced by the API.
export default function RequirePermission({ permission, children }) {
  const { data, isChecking, isAllowed, isSuccess, isError, refetch } =
    useCheckPermission(permission);

  if (isChecking) return <LoadingIndicator />;
  if (!isAllowed) {
    return (
      <ErrorAlert title="Access is unavailable">
        {isSuccess && data?.isPermitted === false
          ? "You do not have permission to view this page."
          : "Your access could not be verified. Please try again later."}
        {isError && (
          <div className="mt-3">
            <Button onPress={() => refetch()}>Try again</Button>
          </div>
        )}
      </ErrorAlert>
    );
  }

  return children;
}
