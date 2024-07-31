import React, { useEffect, useState } from 'react';
import styles from "app/views/time/universalStyles.module.css";
import LoadingIndicator from "app/components/LoadingIndicator";
import { fetchEmployeeInfo, fetchEmployeeSearchApi, getSearchParam, setSearchParam } from "./personnel-Api-ctrl";
import EmployeeList from "app/views/time/personnel/EmployeeList";
import PaginationModel from "app/views/time/personnel/PaginationModel";
import Hero from "app/components/Hero";

// appProps, modals, locationService, employeeSearchApi, empInfoApi, paginationModel, RestErrorService
export default function PersonnelSearchIndex() {
//   const [searchResults, setSearchResults] = useState(['Emp 1', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3', 'Emp 2', 'Emp 3']);
//
//   const getMore = () => {
//     // Logic to fetch more data and update the searchResults state
//     setSearchResults(prevResults => [...prevResults, 'Emp 4', 'Emp 5', 'Emp 6']);
//   };
//
//   return (
//     <div>
//       <Hero>Employee List</Hero>
//       <div className={styles.contentContainer}>
//         <div className={styles.contentInfo}>
//           <EmployeeList searchResults={searchResults} getMore={getMore} />
//         </div>
//       </div>
//     </div>
//   );
// };
  const EMP_ID_PARAM = 'empId';
  const TERM_PARAM = 'term';
  const ACTIVE_ONLY_PARAM = 'activeOnly';

  const [selectedEmp, setSelectedEmp] = useState(null);
  const [empInfo, setEmpInfo] = useState(null);
  const [activeOnly, setActiveOnly] = useState(getSearchParam(ACTIVE_ONLY_PARAM) === 'true');
  const [searchTerm, setSearchTerm] = useState(getSearchParam(TERM_PARAM) || "");
  const [searchResults, setSearchResults] = useState([]);

  let empId = parseInt(getSearchParam(EMP_ID_PARAM) || NaN);

  // let pagination = paginationModel;
  const [pagination] = useState(new PaginationModel());
  pagination.itemsPerPage = 50;

  // additional vars
  const [loadingEmps, setLoadingEmps] = useState(false); //? or should be true?
  const [loadingEmpInfo, setLoadingEmpInfo] = useState(false); //? or should be true?

  // Watches
  useEffect(() => {
    newSearch();
  }, [searchTerm, activeOnly]);

  // Display Methods
  const searchResultsExist = () => {
    return searchResults && searchResults.length > 0;
  };

  /**
   * Expands the result window
   */
  const getNextSearchResults = () => {
    if (loadingEmps || pagination.onLastPage()) {
      return;
    }
    pagination.nextPage();
    return getSearchResults();
  };

  /**
   * Sets the given employee as the selected employee
   * @param emp
   */
  const handleSelectEmp = (emp) => {
    setSelectedEmp(emp);
    getEmpInfo(emp);
    setSearchParam(EMP_ID_PARAM, emp.empId);
  };

  /**
   * Deselects the currently selected employee
   */
  const clearSelectedEmp = () => {
    setSelectedEmp(null);
    setEmpInfo(null);
    if (empId) {
      clearEmpId();
      newSearch();
    }
    clearEmpId();
  };

  // Api Methods
  async function getSearchResults() {
    const params = {
      term: searchTerm,
      empId: validEmpId() ? empId : 0,
      activeOnly: activeOnly,
      limit: pagination.getLimit(),
      offset: pagination.getOffset()
    };
    setLoadingEmps(true);
    try {
      const resp = await fetchEmployeeSearchApi(params);

      console.log('Got employee search results');
      let tempResults = [];
      resp.employees.forEach((emp) => {
        tempResults.push(emp);
      });
      setSearchResults(prevResults => [...prevResults, ...tempResults]);
      pagination.setTotalItems(resp.total);

      if (validEmpId() && tempResults.length > 0) {
        setSelectedEmp(tempResults[0]);
      } else {
        clearEmpId();
      }
    } catch(err) {
      console.error(err);
    } finally {
      setLoadingEmps(false);
    }
  }

  async function getEmpInfo(emp) {
    const params = {
      empId: emp.empId,
      detail: true
    };
    setLoadingEmpInfo(true);
    try {
      const resp = await fetchEmployeeInfo(params);
      console.log('Got employee info');
      setEmpInfo(resp.employee);
    } catch(err) {
      console.error(err);
    } finally {
      setLoadingEmpInfo(false);
    }
  }

  // Internal Methods
  /**
   * Clears current search results, resets pagination, and performs a new search
   */
  function newSearch() {
    setSearchResults([]);
    pagination.reset();
    setSearchParam(TERM_PARAM, searchTerm);
    setSearchParam(ACTIVE_ONLY_PARAM, activeOnly.toString(), activeOnly);
    return getSearchResults();
  }

  function validEmpId() {
    return !isNaN(parseInt(empId)) && empId > 0;
  }

  function clearEmpId() {
    let empId = NaN;
    setSearchParam(EMP_ID_PARAM, empId);
  }


  return (
    <div className={styles.contentContainer}>
      {!selectedEmp ? (
        // Employee Search Page
        <div className={styles.contentInfo}>
          {/*<EmployeeSearchBar/>*/}

          <label className={styles.employeeSearchCheckbox}>
            <input
              type={"checkbox"}
              onClick={() => setActiveOnly(prev => !prev)}
            />
            Show only active employees
          </label>

          {loadingEmps && (<LoadingIndicator/>)}

          <EmployeeList
            searchResults={searchResults}
            getMore={getNextSearchResults}
            handleSelectEmp={handleSelectEmp}
          />
          {!searchTerm || loadingEmps || searchResultsExist() &&
            (<p>No results found for {searchTerm}</p>)}
        </div>
      ) : (
        // Employee Page
        <div className={`${styles.contentInfo} ${styles.selectedEmployee}`}>
          <div>
            <table>
              <tr>
                <th>Selected</th>
                <td>{selectedEmp.fullName}</td>
              </tr>
              <tr>
                <th>Status</th>
                <td className={`${styles.personnelStatus}
                    ${!empInfo.personnelStatus?.employed ? styles.inactive : ''}
                    ${empInfo.personnelStatus?.description != 'ACTIVE' ? styles.special : ''}`}
                >
                  {empInfo.personnelStatus?.description
                   ? empInfo.personnelStatus.description.toLowerCase()
                   : 'empInfo.personnelStatus?.description not exist'}
                </td>
              </tr>
              <tr>
                <th>Emp. Id</th>
                <td>{empInfo.employeeId}</td>
              </tr>
              <tr>
                <th>Pay Type</th>
                <td>{empInfo.payType}</td>
              </tr>
            </table>
          </div>

          <div>
            {loadingEmpInfo ? (
              <LoadingIndicator/>
            ) : (
               <div>
                 <table>
                   <tr>
                     <th>Work Phone</th>
                     <td>{empInfo.workPhone}</td>
                   </tr>
                   <tr>
                     <th>Email</th>
                     <td>{empInfo.email}</td>
                   </tr>
                   <tr>
                     <th>Resp. Ctr.</th>
                     <td>{empInfo.respCtr.respCenterHead.name}</td>
                   </tr>
                 </table>
               </div>
             )}
          </div>

          <div className={styles.selectAnother}>
            <input type={"button"} className={styles.timeNeutralButton}
                   value={"Select Another Employee"}
                   onClick={clearSelectedEmp()}
            />
          </div>
        </div>
      )}
    </div>
  );
};