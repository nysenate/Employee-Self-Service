import ErrorAlert from "app/components/ErrorAlert";
import React from "react";
import LoadingStatus from "app/components/LoadingStatus";
import PotentialAssignmentsTable from "./PotentialAssignmentsTable";
import { setOffset } from "./todoAssignmentActions";
import Pagination from "app/components/Pagination";

export default function PotentialAssignmentsSummary({
  query,
  state,
  dispatch,
}) {
  const onPageChange = (offset) => {
    dispatch(setOffset(offset));
  };

  if (query.isPending) {
    return (
      <LoadingStatus
        message="Loading available assignments…"
        layout="centered"
        size="lg"
        className="min-h-48 p-6"
      />
    );
  }

  if (!query.data) {
    return (
      <ErrorAlert title="Unable to load assignments">
        Please try again.
      </ErrorAlert>
    );
  }

  const displayedState = query.data.filters ?? state;

  return (
    <div>
      {query.isError && (
        <ErrorAlert title="Unable to refresh assignments">
          Please try again.
        </ErrorAlert>
      )}
      <div className="my-3 flex min-h-7 items-center justify-between">
        <div aria-live="polite" className="flex items-center">
          {query.isFetching ? (
            <LoadingStatus
              message={
                query.isPlaceholderData
                  ? "Updating results…"
                  : "Refreshing assignments…"
              }
              announce={false}
            />
          ) : (
            <TotalResults total={query.data.total} />
          )}
        </div>
      </div>
      <div
        aria-busy={query.isFetching}
        className={
          query.isPlaceholderData
            ? "opacity-60 transition-opacity"
            : "transition-opacity"
        }
      >
        {query.data.result.length > 0 && (
          <>
            <Pagination
              limit={displayedState.limit}
              offset={displayedState.offset}
              total={query.data.total}
              onPageChange={onPageChange}
            />
            <PotentialAssignmentsTable
              potentialAssignments={query.data.result}
              state={state}
              dispatch={dispatch}
            />
            <Pagination
              limit={displayedState.limit}
              offset={displayedState.offset}
              total={query.data.total}
              onPageChange={onPageChange}
            />
          </>
        )}
      </div>
    </div>
  );
}

function TotalResults({ total }) {
  return <span className="font-semibold">{total} Matching Employees</span>;
}
