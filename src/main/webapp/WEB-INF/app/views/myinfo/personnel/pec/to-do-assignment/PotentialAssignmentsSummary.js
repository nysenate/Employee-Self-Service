import React from "react";
import LoadingIndicator from "../../../../../components/LoadingIndicator";
import PaginationComponent from "../../../../../components/PaginationComponent";
import PotentialAssignmentsTable from "./PotentialAssignmentsTable";
import { setOffset } from "./todoAssignmentActions";


export default function PotentialAssignmentsSummary({ query, state, dispatch }) {

  const onPageChange = selectedPage => {
    const offset = (selectedPage - 1) * state.limit + 1
    dispatch(setOffset(offset))
  }

  if (query.isPending) {
    return <LoadingIndicator/>
  }

  const currentPage = (state.offset + state.limit - 1) / state.limit
  const totalPages = Math.ceil(query.data.total / state.limit)

  return (
    <div>
      <div className="my-3 flex justify-between items-center">
        <TotalResults total={query.data.total}/>
      </div>
      {query.data.result.length > 0 &&
        <>
          <PaginationComponent currentPage={currentPage} totalPages={totalPages} onPageChange={onPageChange}/>
          <PotentialAssignmentsTable potentialAssignments={query.data.result}
                                     state={state}
                                     dispatch={dispatch}/>
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
