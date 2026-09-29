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

  return (
    <div>
      <div className="my-3 flex items-center justify-between">
        <TotalResults total={query.data.total} />
      </div>
      {query.data.result.length > 0 && (
        <>
          <Pagination
            limit={state.limit}
            offset={state.offset}
            total={query.data.total}
            onPageChange={onPageChange}
          />
          <PotentialAssignmentsTable
            potentialAssignments={query.data.result}
            state={state}
            dispatch={dispatch}
          />
          <Pagination
            limit={state.limit}
            offset={state.offset}
            total={query.data.total}
            onPageChange={onPageChange}
          />
        </>
      )}
    </div>
  );
}

function TotalResults({ total }) {
  return <span className="font-semibold">{total} Matching Employees</span>;
}
