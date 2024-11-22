import React, { useState } from 'react';
import EmployeeCount from "./EmployeeCount";
import Employees from "./Employees";
import PaginationComponent from "./PaginationComponent";
import LoadingIndicator from "app/components/LoadingIndicator";
import AssignmentsTable from "app/views/myinfo/personnel/to-do-reporting/AssignmentsTable";
import { searchTaskAssignmentsQueryParams } from "app/api/searchTaskAssignmentsApi";
import { setOffset } from "app/views/myinfo/personnel/to-do-reporting/todoReportingActions";

// export default function Results({ params, onChildDataChange, finalData, loading, allTasks }) {
export default function AssignmentsSummary({ taskAssignmentQuery, state, dispatch }) {

  // const pageSize = 10; // Number of items per page
  // const [ currentPage, setCurrentPage ] = useState(1);
  // const handlePageChange = (page) => {
  //   setCurrentPage(page);
  //   const offset = (page - 1) * pageSize + 1;
  //   onChildDataChange({ offset: offset });
  // };

  // const pageCount = finalData ? Math.ceil(finalData.total / pageSize) : 1;
  // const pageCount = 1

  // const handleChildDataChange = (data) => {
  //   onChildDataChange(data);
  // };

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

//   return (
//     <div className={styles.card}>
//
//       {/*<EmployeeSearch*/}
//       {/*  params={params}*/}
//       {/*  onChildDataChange={handleChildDataChange}/>*/}
//
//       {taskAssignmentQuery.isPending ? (
//         <div className="flex items-center justify-center">
//           <LoadingIndicator/>
//         </div>
//       ) : (
//          <>
//            <EmployeeCount finalData={finalData}/>
//            <br/>
//            {/*{pageCount > 1 && (*/}
//            {/*  <PaginationComponent*/}
//            {/*    currentPage={currentPage}*/}
//            {/*    totalPages={pageCount}*/}
//            {/*    onPageChange={handlePageChange}*/}
//            {/*  />*/}
//            {/*)}*/}
//            <Employees
//              finalData={finalData}
//              allTasks={allTasks}
//              params={params}
//              onChildDataChange={handleChildDataChange}/>
//
//            {/*{pageCount > 1 && (*/}
//            {/*  <PaginationComponent*/}
//            {/*    currentPage={currentPage}*/}
//            {/*    totalPages={pageCount}*/}
//            {/*    onPageChange={handlePageChange}*/}
//            {/*  />*/}
//            {/*)}*/}
//          </>
//        )}
//       {/*TODO display error - error boundary?*/}
//       {/*<p className={"text-red-600 font-extrabold text-2xl"}>*/}
//       {/*Error Loading the Page*/}
//       {/*</p>*/}
//     </div>
//   );
//}
