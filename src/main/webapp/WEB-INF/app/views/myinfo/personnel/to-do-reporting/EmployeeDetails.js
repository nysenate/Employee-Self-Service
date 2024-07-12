import React, { useState } from 'react';
import EmployeeSearch from "app/views/myinfo/personnel/to-do-reporting/EmployeeSearch";
import styles from './EmployeeSearch.module.css';
import EmployeeCount from "app/views/myinfo/personnel/to-do-reporting/EmployeeCount";
import Employees from "app/views/myinfo/personnel/to-do-reporting/Employees";
import { CircularProgress } from "@mui/material";
import PaginationComponent from "app/views/myinfo/personnel/to-do-reporting/PaginationComponent";

export default function EmployeeDetails({ params, onChildDataChange, finalData, loading }) {

  // const [offset, setOffset] = useState(0);
  const pageSize = 10; // Number of items per page
  const [ currentPage, setCurrentPage ] = useState(1);
  const handlePageChange = (page) => {
    console.log("Current Page:", page);
    console.log("Page Size:", pageSize);
    setCurrentPage(page);
    const offset = (page - 1) * pageSize + 1;
    console.log("Offset:", offset);
    onChildDataChange({ offset: offset });
  };

  // const handlePageChange = (event, page) => {
  //
  //   setCurrentPage(page);
  //   const offset = (page - 1) * pageSize + 1;
  //   onChildDataChange({ offset: offset }); // Send offset to parent component
  // };

  // Calculate page count
  const pageCount = finalData ? Math.ceil(finalData.total / pageSize) : 1;
  const handleChildDataChange = (data) => {
    onChildDataChange(data);
  };

  // const handleOffsetChange = (newOffset) => {
  //   setOffset(newOffset);
  //   // Update params with new offset
  //   onChildDataChange({ offset: newOffset });
  // };

  return <div className={styles.card}>
    <EmployeeSearch params={params} onChildDataChange={handleChildDataChange}/>
    &nbsp; &nbsp;
    {loading ? (<div className="flex items-center justify-center">
        <CircularProgress color="success"/>
      </div>) : finalData ? (<>
        <EmployeeCount finalData={finalData}/>
        <br/>
        {/*<PaginationComponent finalData={finalData} params = {params} onOffsetChange = {handleOffsetChange}/>*/}
        <PaginationComponent
          currentPage={currentPage}
          totalPages={pageCount}
          onPageChange={handlePageChange}
        />
        <Employees finalData={finalData}/>
        <PaginationComponent
          currentPage={currentPage}
          totalPages={pageCount}
          onPageChange={handlePageChange}
        />
        {/*<PaginationRange finalData={finalData} params = {params} onOffsetChange = {handleOffsetChange}/>*/}
      </>) : (<p> OKOK</p>)}
  </div>
}
