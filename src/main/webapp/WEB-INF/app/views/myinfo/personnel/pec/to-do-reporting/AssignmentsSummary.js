import React, { useState } from 'react';
import PaginationComponent from "./PaginationComponent";
import LoadingIndicator from "app/components/LoadingIndicator";
import AssignmentsTable from "app/views/myinfo/personnel/pec/to-do-reporting/AssignmentsTable";
import { setOffset } from "app/views/myinfo/personnel/pec/to-do-reporting/todoReportingActions";
import { searchTaskAssignmentsQueryParams } from "app/views/myinfo/personnel/pec/useTaskAssignment";

export default function AssignmentsSummary({ taskAssignmentQuery, state, dispatch }) {

  const onPageChange = selectedPage => {
    const offset = (selectedPage - 1) * state.limit + 1
    dispatch(setOffset(offset))
  }

  if (taskAssignmentQuery.isPending) {
    return <LoadingIndicator/>
  }

  const currentPage = (state.offset + state.limit - 1) / state.limit
  const totalPages = Math.ceil(taskAssignmentQuery.data.total / state.limit)

  return (
    <div>
      <div className="my-3 flex justify-between items-center">
        <TotalResults total={taskAssignmentQuery.data.total}/>
        <CsvDownload state={state}/>
      </div>
      {taskAssignmentQuery.data.result.length > 0 &&
        <>
          <PaginationComponent currentPage={currentPage} totalPages={totalPages} onPageChange={onPageChange}/>
          <AssignmentsTable taskAssignments={taskAssignmentQuery.data.result} state={state} dispatch={dispatch}/>
          <PaginationComponent currentPage={currentPage} totalPages={totalPages} onPageChange={onPageChange}/>
        </>
      }
    </div>
  )
}

function TotalResults({ total }) {
  return (
    <span className="font-semibold">
      {total} Matching Employees
    </span>
  )
}

function CsvDownload({ state }) {
  return (
    <a href={`/api/v1/personnel/task/emp/search/report?${searchTaskAssignmentsQueryParams(state)}`}
       target="_blank"
       rel="noopener noreferrer">
      Download results as CSV
    </a>
  )
}
