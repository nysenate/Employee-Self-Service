import React, { useEffect, useState } from "react";
import styles from "../universalStyles.module.css"
import LoadingIndicator from "app/components/LoadingIndicator";
// import { formatDateToMMDDYYYY, formatDateYYYYMMDD, hoursDiffHighlighterCustom } from "app/views/time/helpers";
import {
  fetchEmployeeInfo,
  fetchAllowancesActiveYears,
  fetchPeriodAllowanceUsage, getTotalAllowedHours, consoleL, getAvailableHours, getExpectedHours
} from "app/views/time/allowance/time-allowance-ctrl";


// Still need popover
export default function AllowanceHistoryDirective({
                                                    user,
                                                    empSupInfo,
                                                    scopeHideTitle,
                                                  }) {
  const [empId, actualSetEmpId] = useState(null);
  const [periodAllowanceUsages, setPeriodAllowanceUsages] = useState({});
  const [activeYears, setActiveYears] = useState([]);
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

  /* --- Watches --- */
  useEffect(() => {
    setEmpId();
    setPeriodAllowanceUsages({});
  }, [empSupInfo]);
  useEffect(() => {
    getEmpInfo();
    getEmpActiveYears();
  }, [empId]);
  useEffect(() => {
    getPeriodAllowanceUsages();
  }, [selectedYear]);

  /* --- Display Methods --- */
  /* @returns {boolean} true iff the user's allowances are being displayed */
  const isUser = () => {
    return empSupInfo?.employeeId === user.employeeId || empSupInfo?.empId === user.employeeId;
  };
  /* @returns {boolean} true iff any requests are currently loading*/
  const isLoading = () => {
    return Object.values(loading).some((status) => status);
  }
  /* @returns {boolean} true iff employee data is loading*/
  const isEmpLoading = () => {
    return (loading.empInfo || loading.empActiveYears);
  }


  /* --- Internal Methods --- */

  /* Set the employee id from the passed in employee sup info if it exists
   * Otherwise set it to the user's empId
   * REQUIRES empSupInfo */
  const setEmpId = () => {
    let thisEmpId = null;
    if(empSupInfo && (empSupInfo?.empId || empSupInfo?.employeeId)) {
      thisEmpId = empSupInfo?.empId || empSupInfo?.employeeId;
    } else {
      thisEmpId = user.employeeId;
    }
    actualSetEmpId(thisEmpId);
  }
  /* Retrieves employee info from the api to determine if the employee is a temporary employee
  * REQUIRES EMPID */
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
  /* Retrieves the employee's active years
  * REQUIRES EMPID */
  const getEmpActiveYears = async () => {
    if(!empId) return;

    setSelectedYear(null);
    setLoading((prev) => ({ ...prev, empActiveYears: true }));
    try {
      const resp = await fetchAllowancesActiveYears({ empId: empId });
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

  /* Retrieve the employee's period allowance usage */
  const getPeriodAllowanceUsages = async () => {
    let year = parseInt(selectedYear, 10);;
    if(!year || periodAllowanceUsages[year]) return;
    if (!year || year < 0) {
      return;
    }

    // Found this in Angular? (allowance-history-directive)
    // todo implement date ranges when this page becomes available for supervisors
    // var fromDate = moment([year, 0, 1]);
    // var toDate = moment([year + 1, 0, 1]);
    //
    // // Restrict by start and end dates if applicable
    // if (!$scope.isUser()) {
    //     var startDateMoment = moment($scope.empSupInfo.effectiveStartDate || 0);
    //     var endDateMoment = moment($scope.empSupInfo.effectiveEndDate || '3000-01-01');
    //
    //     fromDate = moment.max(fromDate, startDateMoment);
    //     toDate = moment.min(toDate, endDateMoment);
    // }

    setLoading((prev) => ({ ...prev, periodAllowanceUsage: true }));
    try {
      const response = await fetchPeriodAllowanceUsage({ empId: empId, year: year });
      setError(null);
      const allowanceUsages = response.result
        .sort((a, b) => new Date(b.payPeriod.endDate) - new Date(a.payPeriod.endDate));

      // // Compute remaining allowance for each period usage
      // allowanceUsages.forEach(function (periodUsage) {
      //   var dateRange = {
      //     beginDate: periodUsage.payPeriod.startDate,
      //     endDate: periodUsage.payPeriod.endDate
      //   };
      //   allowanceUtils.computeRemaining(periodUsage, dateRange);
      // });

      setPeriodAllowanceUsages((prev) => ({
        ...prev,
        [selectedYear]: allowanceUsages,
      }));
    } catch (error) {
      handleErrorResponse(error);
      setError({
        title: "Could not retrieve accrual information.",
        message: "If you are eligible for accruals please try again later."
      });
    } finally {
      setLoading((prev) => ({ ...prev, periodAllowanceUsage: false }));
    }
  }
  const handleErrorResponse = (error) => {
    setError({
      title: "Could not retrieve accrual information.",
      message: "If you are eligible for accruals please try again later.",
    });
    console.error(error);
  };

  // Output methods
  const getSalaryRate = (allowance) => {
    const { salaryRecs, payPeriod } = allowance;

    // Find the active salary record for the payPeriod
    // That is: payPeriod endDate is between salary record effectDate and endDate
    let salaryRecord = salaryRecs.find((rec) => {
      const effectDate = new Date(rec.effectDate);
      const endDate = new Date(rec.endDate);
      const payPeriodStartDate = new Date(payPeriod.startDate);
      const payPeriodEndDate = new Date(payPeriod.endDate);

      return effectDate <= payPeriodEndDate && payPeriodStartDate <= endDate;
    });
    // Return the salary rate from the found salary record
    return salaryRecord ? salaryRecord.salaryRate : null;
  };

  const getTotalAllowedHours = (allowance) => {
    if (allowance) {
      const totalAllowedHours = allowance.yearlyAllowance/getSalaryRate(allowance);
      // console.log('Max salary Rate: ', getSalaryRate(allowance), "YrAllowance: ",allowance.yearlyAllowance,'TotalAlllow: ', Math.floor(totalAllowedHours*4)/4)
      return Math.floor(totalAllowedHours*4)/4;
    }
    return 0;
  };

// Like getAvailableHours but without signs and colors provided by hoursDiffHighlighterCustom
  const getExpectedHours = (allowance) => {
    let expHrs = (getTotalAllowedHours(allowance) - (allowance.hoursUsed + allowance.periodHoursUsed)).toFixed(2);
    return `${expHrs}`;
  }

  return (
    <div>
      {isUser() && selectedYear && (<div className={`${styles.contentContainer} ${styles.contentControls}`}>
        <p className={styles.contentInfo}> Filter By Year {'\u00A0'}
          <YearSelect years={activeYears} selectedYear={selectedYear} setYear={setSelectedYear}/>
        </p>
      </div>)}

      {isEmpLoading() && (<LoadingIndicator/>)}

      <div className={styles.contentContainer} style={{ display: isEmpLoading() ? 'none' : 'block' }}>
        {selectedYear && (
          <div>
            <div className={`${styles.contentContainer} ${styles.contentControls}`} style={{ display: isUser() ? 'none' : 'block' }}>
              {!hideTitle && !isUser() && (
                <h1>
                  {empSupInfo.empFirstName} {empSupInfo.empLastName}'s Allowance History
                </h1>
              )}
              <p className={styles.contentInfo} style={{ marginBottom: 0 }}>
                Filter By Year {'\u00A0'}
                <YearSelect years={activeYears} selectedYear={selectedYear} setYear={setSelectedYear}/>
              </p>
            </div>

            {isLoading() && <LoadingIndicator/>}

            {!periodAllowanceUsages[selectedYear]?.length > 0 && !isLoading() && (
              <p className={styles.contentInfo}>
                No allowance usage records exist for this year.
                If it is early in the year they may not have been created yet.
              </p>
            )}

            {periodAllowanceUsages[selectedYear]?.length > 0 && (
              <>
                <p className={styles.contentInfo}>
                  Summary of past allowance usage for each pay period.
                  Click a row to view or print a detailed summary of allowance usage.
                </p>

                <table
                  className={styles.allowanceTable}
                  /* Add floating header options if needed */
                >
                  <thead>
                  <tr>
                    <th className={styles.periodNo}>Period #</th>
                    <th className={styles.endDate}>End Date</th>
                    <th className={styles.used}>Used</th>
                    <th className={styles.usedYtd}>Used YTD</th>
                    <th className={styles.totalAllowed}>Total Allowed</th>
                    <th className={styles.estAvailable}>Est Available</th>
                  </tr>
                  </thead>
                  <tbody>
                  {periodAllowanceUsages[selectedYear].map((perUsage, index) => (
                    <tr
                      key={index}
                      title="Print period allowance usage"
                      onClick={() => selectPeriodUsage(perUsage)}
                      /*Need popover stuff
                      * className={styles.nsPopover}
                      * Add other ns-popover attributes as needed */
                    >
                      <td className={styles.periodNo}>{perUsage.payPeriod.payPeriodNum}</td>
                      <td className={styles.endDate}>{perUsage.payPeriod.endDate}</td>
                      <td className={styles.used}>{perUsage.periodHoursUsed}</td>
                      <td className={styles.usedYtd}>{perUsage.hoursUsed + perUsage.periodHoursUsed}</td>
                      <td className={styles.totalAllowed}>{getTotalAllowedHours(perUsage).toLocaleString()}</td>
                      <td className={styles.estAvailable}>{getExpectedHours(perUsage)}</td>
                    </tr>
                  ))}
                  </tbody>
                </table>
                <hr style={{margin: '6.5px 0px'}}/>
              </>
            )}
          </div>
        )}
      </div>

    </div>
  );
};

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