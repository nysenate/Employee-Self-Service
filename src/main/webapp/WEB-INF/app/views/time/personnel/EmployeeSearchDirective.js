import React, { useEffect, useState } from 'react';
import styles from "app/views/time/universalStyles.module.css";
import LoadingIndicator from "app/components/LoadingIndicator";
import { fetchEmployeeInfo, fetchEmployeeSearchApi, getSearchParam, setSearchParam } from "./personnel-Api-ctrl";
import EmployeeList from "app/views/time/personnel/EmployeeList";
import PaginationModel from "app/views/time/personnel/PaginationModel";
import Hero from "app/components/Hero";

export default function EmployeeSearchDirective({ selectedEmp, setSelectedEmp}) {
  const EMP_ID_PARAM = 'empId';
  const TERM_PARAM = 'term';
  const ACTIVE_ONLY_PARAM = 'activeOnly';

  // const [selectedEmp, setSelectedEmp] = useState(null);
  const [empInfo, setEmpInfo] = useState(null);
  const [activeOnly, setActiveOnly] = useState(getSearchParam(ACTIVE_ONLY_PARAM) === 'true');
  const [searchTerm, setSearchTerm] = useState(getSearchParam(TERM_PARAM) || "");
  const [searchResults, setSearchResults] = useState([]);

  let empId = parseInt(getSearchParam(EMP_ID_PARAM) || NaN);

  const [pagination] = useState(new PaginationModel());
  pagination.itemsPerPage = 50;

  const [loadingEmps, setLoadingEmps] = useState(false);
  const [loadingEmpInfo, setLoadingEmpInfo] = useState(false);

  useEffect(() => {
    newSearch();
  }, [searchTerm, activeOnly]);

  useEffect(() => {
    console.log("S: ", selectedEmp);
  }, [selectedEmp]);

  useEffect(() => {
    console.log("E: ", empInfo);
  }, [empInfo]);

  const searchResultsExist = () => {
    return searchResults && searchResults.length > 0;
  };

  const getNextSearchResults = () => {
    if (loadingEmps || pagination.onLastPage()) {
      return;
    }
    pagination.nextPage();
    return getSearchResults();
  };

  const handleSelectEmp = async (emp) => {
    setSelectedEmp(emp);
    console.log("hello?");
    await getEmpInfo(emp);
    setSearchParam(EMP_ID_PARAM, emp.empId);
  };

  const clearSelectedEmp = () => {
    setSelectedEmp(null);
    setEmpInfo(null);
    if (empId) {
      clearEmpId();
      newSearch();
    }
    clearEmpId();
  };

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
      console.log('Got employee info: ', resp);
      setEmpInfo(resp.employee);
    } catch(err) {
      console.error(err);
    } finally {
      setLoadingEmpInfo(false);
    }
  }

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
    empId = NaN;
    setSearchParam(EMP_ID_PARAM, empId, false);
  }

  const [debouncedSearchTerm, setDebouncedSearchTerm] = useState(getSearchParam(TERM_PARAM) || "");
  useEffect(() => {
    const handler = setTimeout(() => {
      setSearchTerm(debouncedSearchTerm);
    }, 300);

    return () => {
      clearTimeout(handler);
    };
  }, [debouncedSearchTerm]);

  const handleSearchChange = (event) => {
    setDebouncedSearchTerm(event.target.value);
  };

  return (
    <div className={styles.contentContainer}>
      {!selectedEmp || !empInfo ? (
        <div className={styles.contentInfo}>
          <input type={"search"} className={styles.employeeSearchBar}
                 tabIndex="1"
                 autoFocus
                 value={debouncedSearchTerm}
                 onChange={handleSearchChange}
                 placeholder="Search for an employee"
          />

          <label className={styles.employeeSearchCheckbox}>
            <input
              type={"checkbox"}
              checked={activeOnly}
              onChange={() => setActiveOnly(prev => !prev)}
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
                  ${!empInfo.personnelStatus.employed ? styles.inactive : ''}
                  ${empInfo.personnelStatus.description !== 'ACTIVE' ? styles.special : ''}`}
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
                    </tr>
                  </table>
                </div>
              )}
           </div>

           <div className={styles.selectAnother}>
             <input type={"button"} className={styles.timeNeutralButton}
                    value={"Select Another Employee"}
                    onClick={clearSelectedEmp}
             />
           </div>
         </div>
       )}
    </div>
  );
};
