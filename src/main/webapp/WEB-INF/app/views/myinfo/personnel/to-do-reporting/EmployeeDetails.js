import React, { useState } from 'react';
import EmployeeSearch from "./EmployeeSearch";
import styles from './EmployeeSearch.module.css';
import EmployeeCount from "./EmployeeCount";
import Employees from "./Employees";
import PaginationComponent from "./PaginationComponent";
import LoadingIndicator from "app/components/LoadingIndicator";

export default function EmployeeDetails({ params, onChildDataChange, finalData, loading, allTasks }) {

  const pageSize = 10; // Number of items per page
  const [ currentPage, setCurrentPage ] = useState(1);
  const handlePageChange = (page) => {
    setCurrentPage(page);
    const offset = (page - 1) * pageSize + 1;
    onChildDataChange({ offset: offset });
  };

  const pageCount = finalData ? Math.ceil(finalData.total / pageSize) : 1;
  const handleChildDataChange = (data) => {
    onChildDataChange(data);
  };

  return (
    <div className={styles.card}>

      <EmployeeSearch
        params={params}
        onChildDataChange={handleChildDataChange}/>

      {loading ? (
        <div className="flex items-center justify-center">
          <LoadingIndicator/>
        </div>
      ) : finalData ? (
        <>
          <EmployeeCount finalData={finalData}/>
          <br/>
          {pageCount > 1 && (
            <PaginationComponent
              currentPage={currentPage}
              totalPages={pageCount}
              onPageChange={handlePageChange}
            />
          )}
          <Employees
            finalData={finalData}
            allTasks={allTasks}
            params={params}
            onChildDataChange={handleChildDataChange}/>

          {pageCount > 1 && (
            <PaginationComponent
              currentPage={currentPage}
              totalPages={pageCount}
              onPageChange={handlePageChange}
            />
          )}
        </>
      ) : (
            <p className={"text-red-600 font-extrabold text-2xl"}>
              Error Loading the Page
            </p>
          )}
    </div>);
}
