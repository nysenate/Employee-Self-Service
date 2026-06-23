import React from "react";
import LoadingIndicator from "app/components/LoadingIndicator";
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
    return <LoadingIndicator />;
  }

  return (
    <div>
      <div className="my-3 flex items-center justify-between">
        <TotalResults total={taskAssignmentQuery.data.total} />
        <CsvDownload state={state} />
      </div>
      {taskAssignmentQuery.data.result.length > 0 && (
        <>
          <Pagination
            limit={state.limit}
            offset={state.offset}
            total={taskAssignmentQuery.data.total}
            onPageChange={onPageChange}
          />
          <AssignmentsTable taskAssignments={taskAssignmentQuery.data.result} />
          <Pagination
            limit={state.limit}
            offset={state.offset}
            total={taskAssignmentQuery.data.total}
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
