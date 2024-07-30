/*
* HistoryDirective.js
* replicated history-directive.jsp
* called <accrual-history> in angular section
* This component is called by AccrualHistoryIndex and AccrualEmpHistoryIndex
* It displays an employees accrual history in a grid,
*  and handles year filter if user is not the displayed employee <= for manager use in AccrualEmpHistory
* */

import React, { useEffect, useState } from "react";
import styles from "../universalStyles.module.css"
import LoadingIndicator from "app/components/LoadingIndicator";
import {
  fetchAccrualActiveYears,
  fetchAccrualSummaries,
  fetchEmployeeInfo
} from "app/views/time/accrual/time-accrual-ctrl";
import { formatDateYYYYMMDD } from "app/views/time/helpers";

const HistoryDirective = ({
                            viewDetails,
                            user,
                            empSupInfo,
                            scopeHideTitle,
                          }) => {

  // Scope Variables
  const [empId, actualSetEmpId] = useState(null);
  const [accSummaries, setAccSummaries] = useState({});
  const [activeYears, setActiveYears] = useState([]);
  const [timeRecords, setTimeRecords] = useState([]);
  const [selectedYear, setSelectedYear] = useState(null);
  const [empInfo, setEmpInfo] = useState({});
  const [isTe, setIsTe] = useState(false);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState({
    empInfo: false,
    empActiveYears: false,
    accSummaries: false,
  });
  // const [floatTheadOpts, setFloatTheadOpts] = useState({
  //   scrollingTop: 47,
  //   useAbsolutePositioning: false,
  // });
  // const [floatTheadEnabled, setFloatTheadEnabled] = useState(true);
  const hideTitle = scopeHideTitle || false;

  // Watchers
  useEffect(() => {
    setEmpId();
    clearAccSummaries();
  }, [empSupInfo]);
  useEffect(() => {
    getEmpInfo();
    getEmpActiveYears();
  }, [empId]);

  // Scope Fetch
  useEffect(() => {
      getAccSummaries();
  }, [selectedYear]);
  const getEmpInfo = async () => {
    if(!(empId && isUser())) return;

    setLoading((prev) => ({ ...prev, empInfo: true }));
    try {
      const response = await fetchEmployeeInfo({ empId: empId, detail: true });
      const empInfo = response.employee;
      setEmpInfo(empInfo);
      setIsTe(empInfo.payType === 'TE');
    } catch (error) {
      handleErrorResponse(error);
    } finally {
      setLoading((prev) => ({ ...prev, empInfo: false }));
    }
  };
  const getEmpActiveYears = async () => {
    if(!empId) return;

    setSelectedYear(null);
    setLoading((prev) => ({ ...prev, empActiveYears: true }));
    try {
      const resp = await fetchAccrualActiveYears({ empId: empId });
      handleActiveYearsResponse(resp);
    } catch (error) {
      handleErrorResponse(error);
    } finally {
      setLoading((prev) => ({ ...prev, empActiveYears: false }));
    }
  };
  const handleActiveYearsResponse = (resp) => {
    const emp = empSupInfo;
    // const isUserSup = emp && (emp?.supId || emp?.supervisorId);
    const startDate = emp.effectiveStartDate;
    const endDate = emp.effectiveEndDate;
    const supStartYear = new Date(startDate || 0).getFullYear();
    const supEndYear = new Date(endDate || Date.now()).getFullYear();
    const recordYrs = resp.years.filter((year) => year >= supStartYear && year <= supEndYear).reverse();

    setActiveYears(recordYrs);
    setSelectedYear(recordYrs.length > 0 ? `${recordYrs[0]}` : -1);
  };


  const getAccSummaries = async () => {
    const emp = empSupInfo;
    if (!selectedYear || accSummaries[selectedYear]) return;

    const year = parseInt(selectedYear, 10);
    if (!year || year < 0) {
      return;
    }

    const supStartDate = emp.effectiveStartDate;
    const supEndDate = emp.effectiveEndDate;

    const yearStart = new Date(year, 0, 1);
    const nextYearStart = new Date(year + 1, 0, 1);

    const supStartMoment = new Date(supStartDate || 0);
    const supEndMoment = new Date(supEndDate || '3000-01-01');

    const fromMoment = new Date(Math.max(yearStart, supStartMoment));
    const toMoment = new Date(Math.min(nextYearStart, supEndMoment));

    if (fromMoment > toMoment) return;

    setLoading((prev) => ({ ...prev, accSummaries: true }));

    try {
      const response = await fetchAccrualSummaries({ empId: empId, fromDate: formatDateYYYYMMDD(fromMoment), toDate: formatDateYYYYMMDD(toMoment) });
      setError(null);
      const sortedSummaries = response.result
        .filter(shouldDisplayRecord)
        .sort((a, b) => new Date(b.payPeriod.endDate) - new Date(a.payPeriod.endDate));

      setAccSummaries((prev) => ({
        ...prev,
        [selectedYear]: sortedSummaries,
      }));
    } catch (error) {
      handleErrorResponse(error);
      setError({
        title: "Could not retrieve accrual information.",
        message: "If you are eligible for accruals please try again later."
      });
    } finally {
      setLoading((prev) => ({ ...prev, accSummaries: false }));
    }
  };
  const handleErrorResponse = (error) => {
    setError({
      title: "Could not retrieve accrual information.",
      message: "If you are eligible for accruals please try again later.",
    });
    console.error(error);
  };
  // Display Methods
  const isUser = () => {
    return empSupInfo?.employeeId === user.employeeId || empSupInfo?.empId === user.employeeId;
  };

  const isLoading = () => {
    return Object.values(loading).some((status) => status);
  }
  const isEmpLoading = () => {
    return (loading.empInfo || loading.empActiveYears);
  }

  // Internal Methods
  const setEmpId = () => {
    let thisEmpId = null;
    if(empSupInfo && (empSupInfo?.empId || empSupInfo?.employeeId)) {
      thisEmpId = empSupInfo?.empId || empSupInfo?.employeeId;
    } else {
      thisEmpId = user.employeeId;
    }
    actualSetEmpId(thisEmpId);
  }
  const clearAccSummaries = () => {
    setAccSummaries({});
  }

  const shouldDisplayRecord = (accrualRecord) => {
    let displayRecord = true;
    // Record must be non-computed or be covered by submitted timesheets
    displayRecord = displayRecord && (!accrualRecord.computed || accrualRecord.submitted);
    if (accrualRecord.empState) {
      // Employee must not be temporary
      displayRecord = displayRecord && accrualRecord.empState.payType !== 'TE';
    }
    return displayRecord;
  }

  // function reflowTable (count) {
  // const enableFloatThead = () => { setFloatTheadEnabled(true); }
  // const disableFloatThead = () => { setFloatTheadEnabled(false); }


  return (
    <>
      {/*If employee is user, select year in this component (separate from grid)*/}
      {isUser() && selectedYear && (<div className={`${styles.contentContainer} ${styles.contentControls}`}>
        <p className={styles.contentInfo}> Filter By Year {'\u00A0'}
          <YearSelect years={activeYears} selectedYear={selectedYear} setYear={setSelectedYear}/>
        </p>
      </div>)}

      {/*Error message Here!!*/}

      {/*Loading Indicator Here!!*/}

      {/*Display Grid*/}
      {!isEmpLoading() && (<div className={styles.contentContainer}>
        {selectedYear ? (<div>
          {!isUser() && (<div className={`${styles.contentContainer} ${styles.contentControls}`}>
            {!(hideTitle || isUser()) && (<h1>
              {empSupInfo.empFirstName} {empSupInfo.empLastName}'s
              Accrual History
            </h1>)}
            <p className={styles.contentInfo} style={{ marginBottom: '0px' }}>
              Filter By Year {'\u00A0'}
              <YearSelect years={activeYears} selectedYear={selectedYear} setYear={setSelectedYear}/>
            </p>
          </div>)}

          {isLoading() ?
            // (<div className={`${styles.smLoader} ${styles.noCollapse}`}><LoadingIndicator/></div>)}
            (<div><LoadingIndicator/></div>) :
             (
               <>
                 {!accSummaries[selectedYear]?.length > 0 && (<p className={styles.contentInfo}>
                   No historical accrual records exist for this year.
                   If it is early in the year they may not have been created yet.
                 </p>)}

                 {accSummaries[selectedYear]?.length > 0 && (<div>
                   <p className={styles.contentInfo}>
                     Summary of historical accrual records.
                     Click a row to view or print a detailed summary of accrual hours.
                   </p>

                   {/*Might have to do something here with
                  float-thead="floatTheadOpts"
                 float-thead-enabled="floatTheadEnabled" ng-model="accSummaries[selectedYear]">
                */}
                   <table
                     className={styles.accrualTable}
                     float-thead={'floatTheadOpts'}
                     float-thead-enabled={"floatTheadEnabled"}
                   >
                     <thead>
                     <tr>
                       <th colSpan="2">Pay Period</th>
                       <th colSpan="4" className={styles.personalHours}>Personal Hours</th>
                       <th colSpan="5" className={styles.vacationHours}>Vacation Hours</th>
                       <th colSpan="5" className={styles.sickHours}>Sick Hours</th>
                     </tr>
                     <tr>
                       <th>#</th>
                       <th>End Date</th>
                       <th className={styles.personal}>Accrued</th>
                       <th className={styles.personal}>Used</th>
                       <th className={styles.personal}>Used Ytd</th>
                       <th className={styles.personal}>Avail</th>

                       <th className={styles.vacation}>Rate</th>
                       <th className={styles.vacation}>Accrued</th>
                       <th className={styles.vacation}>Used</th>
                       <th className={styles.vacation}>Used Ytd</th>
                       <th className={styles.vacation}>Avail</th>

                       <th className={styles.sick}>Rate</th>
                       <th className={styles.sick}>Accrued</th>
                       <th className={styles.sick}>Used</th>
                       <th className={styles.sick}>Used Ytd</th>
                       <th className={styles.sick}>Avail</th>
                     </tr>
                     </thead>
                     <tbody>
                     {accSummaries[selectedYear].map((record, index) => (
                       <tr
                         key={index}
                         className={record.payPeriod.current ? styles.highlighted : ''}
                         title="Open a Printable View for this Record"
                         onClick={() => viewDetails(record)}
                       >
                         <td>{record.payPeriod.payPeriodNum}</td>
                         <td>{new Date(new Date(record.payPeriod.endDate).setDate(new Date(record.payPeriod.endDate).getDate() + 1)).toLocaleDateString('en-US', { year: 'numeric', month: '2-digit', day: '2-digit' })}</td>
                         <td className={`${styles.accrualHours} ${styles.personal}`}>{record.personalAccruedYtd}</td>
                         <td className={`${styles.accrualHours} ${styles.personal}`}>{record.biweekPersonalUsed}</td>
                         <td className={`${styles.accrualHours} ${styles.personal}`}>{record.personalUsed}</td>
                         <td className={`${styles.accrualHours} ${styles.availableHours} ${styles.personal}`}>{record.personalAvailable}</td>

                         <td className={`${styles.accrualHours} ${styles.vacation}`}>{record.vacationRate}</td>
                         <td className={`${styles.accrualHours} ${styles.vacation}`}>{record.vacationAccruedYtd + record.vacationBanked}</td>
                         <td className={`${styles.accrualHours} ${styles.vacation}`}>{record.biweekVacationUsed}</td>
                         <td className={`${styles.accrualHours} ${styles.vacation}`}>{record.vacationUsed}</td>
                         <td className={`${styles.accrualHours} ${styles.availableHours} ${styles.vacation}`}>{record.vacationAvailable}</td>

                         <td className={`${styles.accrualHours} ${styles.sick}`}>{record.sickRate}</td>
                         <td className={`${styles.accrualHours} ${styles.sick}`}>{record.sickAccruedYtd}</td>
                         <td className={`${styles.accrualHours} ${styles.sick}`}>{record.biweekSickEmpUsed + record.biweekSickFamUsed + record.biweekSickDonated}</td>
                         <td className={`${styles.accrualHours} ${styles.sick}`}>{record.sickEmpUsed + record.sickFamUsed + record.sickDonated}</td>
                         <td className={`${styles.accrualHours} ${styles.availableHours} ${styles.sick}`}>{record.sickAvailable}</td>
                       </tr>
                     ))}
                     </tbody>
                   </table>
                   <hr/>
                 </div>)}
               </>
           )}
        </div>
        ) : (<div>
            <p>
              {isUser() ? (<span>You have</span>) :
                (<span>{empSupInfo.empFirstName} {empSupInfo.empLastName} has </span>)}
              no accrual records.
            </p>
            {empSupInfo?.senator && (<p>
              {empSupInfo.firstName} {empSupInfo.lastName} is a Senator and does not currently accrue time.
            </p>)}
        </div>)}
      </div>)}
    </>
  );
}


const YearSelect = ({ years, selectedYear, setYear }) => {
  const handleChange = (event) => {
    setYear(parseInt(event.target.value, 10));
  };

  return (
    <select value={selectedYear} onChange={handleChange} style={{color: 'black', fontWeight: '400'}}>
      {years.map((year, index) => (
        <option key={index} value={year}>
          {year}
        </option>
      ))}
    </select>
  );
};

export default HistoryDirective;