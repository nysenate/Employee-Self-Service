import ErrorAlert from "app/components/ErrorAlert";
import React from "react";
import LoadingStatus from "app/components/LoadingStatus";
import AssignmentsTable from "app/views/myinfo/personnel/pec/to-do-reporting/AssignmentsTable";
import { setOffset } from "app/views/myinfo/personnel/pec/to-do-reporting/todoReportingActions";
import { searchTaskAssignmentsQueryParams } from "app/views/myinfo/personnel/pec/useTaskAssignment";
import Pagination from "app/components/Pagination";

export default function AssignmentsSummary({
  taskAssignmentQuery,
  state,
  dispatch,
}) {
  const onPageChange = (offset) => {
    dispatch(setOffset(offset));
  };

  if (taskAssignmentQuery.isPending) {
    return (
      <LoadingStatus
        message="Loading assignment summary…"
        layout="centered"
        size="lg"
        className="min-h-48 p-6"
      />
    );
  }

  if (!taskAssignmentQuery.data) {
    return (
      <ErrorAlert title="Unable to load assignments">
        Please try again.
      </ErrorAlert>
    );
  }

  const displayedState = taskAssignmentQuery.data.filters ?? state;

  return (
    <div>
      {taskAssignmentQuery.isError && (
        <ErrorAlert title="Unable to refresh assignments">
          Please try again.
        </ErrorAlert>
      )}
      <div className="my-3 flex min-h-7 items-center justify-between">
        <div aria-live="polite" className="flex items-center">
          {taskAssignmentQuery.isFetching ? (
            <LoadingStatus
              message={
                taskAssignmentQuery.isPlaceholderData
                  ? "Updating results…"
                  : "Refreshing assignments…"
              }
              announce={false}
            />
          ) : (
            <TotalResults total={taskAssignmentQuery.data.total} />
          )}
        </div>
        <CsvDownload state={displayedState} />
      </div>
      <div
        aria-busy={taskAssignmentQuery.isFetching}
        className={
          taskAssignmentQuery.isPlaceholderData
            ? "opacity-60 transition-opacity"
            : "transition-opacity"
        }
      >
        {taskAssignmentQuery.data.result.length > 0 && (
          <>
            <Pagination
              limit={displayedState.limit}
              offset={displayedState.offset}
              total={taskAssignmentQuery.data.total}
              onPageChange={onPageChange}
            />
            <AssignmentsTable
              taskAssignments={taskAssignmentQuery.data.result}
            />
            <Pagination
              limit={displayedState.limit}
              offset={displayedState.offset}
              total={taskAssignmentQuery.data.total}
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

function CsvDownload({ state }) {
  return (
    <a
      href={`/api/v1/personnel/task/emp/search/report?${searchTaskAssignmentsQueryParams(state)}`}
      target="_blank"
      rel="noopener noreferrer"
    >
      Download results as CSV
    </a>
  );
}
