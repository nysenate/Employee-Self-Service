import styles from "../universalStyles.module.css"
import React, { useEffect, useState } from "react";
import LoadingIndicator from "app/components/LoadingIndicator";
import EssNotification from "app/components/EssNotification";
import { fetchAccrualSummaries, fetchEmployeeInfo } from "app/views/time/accrual/time-accrual-ctrl";
import ComponentA, { formatDateToMMDDYYYY, formatDateYYYYMMDD } from "app/views/time/helpers";



// Abisha Vijayashanthar 14160
// Issues
//    -Popup error onClick
//    - an avail as '--' will make all above '--', but should only be its respective row all below
export default function AccrualProjectionsDirective({
                                                      viewDetails,
                                                      user,
                                                      empSupInfo,
                                                      scopeHideTitle,
                                                    }) {

  let maxVacationBanked = 210;
  let maxSickBanked = 1400;

  const [empId, actualSetEmpId] = useState(null);

  const [projections, setProjections] = useState([]);
  const [accSummaries, setAccSummaries] = useState({});
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
  }, [empSupInfo]);
  useEffect(() => {
    getEmpInfo();
    getAccSummaries()
  }, [empId]);


  /* --- Request Methods --- */

  const getAccSummaries = async () => {
    // const emp = empSupInfo;
    if(!empId) return;

    const year = new Date().getFullYear();
    let fromMoment = new Date(year, 0, 1);
    fromMoment.setMonth(fromMoment.getMonth() - 6);
    let toMoment = new Date(year + 1, 0, 1);

    if(!isUser) {
      const supStartMoment = new Date(supStartDate || 0);
      const supEndMoment = new Date(supEndDate || '3000-01-01');

      fromMoment = new Date(Math.max(yearStart, supStartMoment));
      toMoment = new Date(Math.min(nextYearStart, supEndMoment));
    }

    if (fromMoment > toMoment) return;

    const params = {
      empId: empId,
      fromDate: formatDateYYYYMMDD(fromMoment),
      toDate: formatDateYYYYMMDD(toMoment)
    }
    setError(null);
    setLoading((prev) => ({ ...prev, accSummaries: true }));
    try {
      const response = await fetchAccrualSummaries(params);

      // Store summaries for submitted records in reverse chron. order
      const sortedSummaries = response.result
        .filter((acc) => { return !acc.computed || acc.submitted;})
        .reverse();
      setAccSummaries(sortedSummaries);
      // Set and initialize projected records
      let sortedProjections = response.result
        .filter(isValidProjection)
        .map(initializeProjection);
      setProjections(sortedProjections);
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


  /* Retrieves employee info from the api to determine if the employee is a temporary employee */
  const getEmpInfo = async () => {
    if(!(empId && isUser())) return;

    setLoading((prev) => ({ ...prev, empInfo: true }));
    try {
      const response = await fetchEmployeeInfo({ empId: empId, detail: true });
      const empInfoResp = response.employee;
      setEmpInfo(empInfoResp);
      setIsTe(empInfoResp.payType === 'TE');
    } catch (error) {
      handleErrorResponse(error);
    } finally {
      setLoading((prev) => ({ ...prev, empInfo: false }));
    }
  };


  /* --- Display Methods --- */
  /* @returns {boolean} true iff the user's accruals are being displayed */
  const isUser = () => {
    // empSupId especially can either have employeeId or empId format
    const userId = user.employeeId ? user.employeeId : user.empId;
    const empSupId = empSupInfo.employeeId ? empSupInfo.employeeId : empSupInfo.empId;
    return empSupId === userId;
  };
  /* @returns {boolean} true iff any requests are currently loading*/
  const isLoading = () => {
    return Object.values(loading).some((status) => status);
  }

  const onAccUsageChange = (fieldName, type, index, e) => {
    // recalculateProjectionTotals();
    // setChangedFlags(accrualRecord, type);
    // console.log(projections[index]); //works
    // console.log(projections[index][fieldName]); //works
    const { value } = e.target; //works
    // console.log(value);

    const numericValue = parseFloat(value); // Convert the value to a number

    let updatedProjections = projections.map((record, i) => {
      if (i === index) {
        // Create a new record with the updated value
        return {
          ...record,
          [fieldName]: numericValue,
        };
      }
      return record;
    });

    recalculateProjectionTotals(updatedProjections);
    setChangedFlags(updatedProjections, index, type);

    // Recalculate projections after updating the value
    setProjections(updatedProjections);
  };

  /* --- Internal Methods --- */
  const setEmpId = () => {
    let thisEmpId = null;
    if(empSupInfo && (empSupInfo?.empId || empSupInfo?.employeeId)) {
      thisEmpId = empSupInfo?.empId || empSupInfo?.employeeId;
    }
    // else {
    //   thisEmpId = user.employeeId;
    // }
    actualSetEmpId(thisEmpId);
  }

  /* @param acc Accrual record
   * @returns {*|boolean} - True iff the record is a computed projection
   *                          and the employee is able to accrue/use accruals */
  const isValidProjection = (acc) => {
    return acc.computed && !acc.submitted && acc.empState.payType !== 'TE' && acc.empState.employeeAccruing;
  }

  /** Indicates delta fields that are used for input, used to init projection */
  const deltaFields = ['biweekPersonalUsed', 'biweekVacationUsed', 'biweekSickEmpUsed', 'biweekSickFamUsed', "biweekSickDonated"];

  /* Initialize the given projection for display
   * @param projection - Accrual projection record */
  const initializeProjection = (projection) => {
    // Set all 0 fields as null to facilitate initial entry
    deltaFields.forEach(function (fieldName) {
      if (projection[fieldName] === 0) {
        projection[fieldName] = null;
      }
    });
    // Add a changed field for storing change flags
    projection.changed = {};
    // Calculate a maximum usage of 12 hours / day for the pay period
    projection.maxHours = projection.payPeriod.numDays * 12;
    // Set initial validation status
    projection.validation = getCleanValidation();
    projection.valid = true;

    return projection;
  }

  /* When a user enters in hours in the projections table, the totals need to be re-computed for
   * the projected accrual records. */
  const recalculateProjectionTotals = (updatedProjections) => {
    let accSum = accSummaries;
    let baseRec = accSummaries.length > 0 ? accSummaries[0] : null;
    let multiYear = false;

    let accState = getInitialAccState(baseRec);

    updatedProjections.forEach((projection, index) => {
      let rec = projection;
      let lastRec = index === 0 ? baseRec : updatedProjections[index];

      // If multiple years are present, banked hours will be dynamic and need to be reset
      if (multiYear) {
        rec.vacationBanked = lastRec.vacationBanked;
        rec.sickBanked = lastRec.sickBanked;
      }

      // Apply rollover if record is the first of the year and a preceding record is available
      if (lastRec && isFirstRecordOfYear(rec)) {
        multiYear = true;
        applyRollover(rec, lastRec, accState);
      }

      updateAccrualState(rec, accState);
      setRecordUsedHours(rec, accState);
      calculateAvailableHours(rec);
      validateRecord(rec, accState);
    });
  }

  /* Get a new validation object where everything is valid
   * @returns {{per: boolean, vac: boolean, sick: boolean}} */
  const getCleanValidation = () => {
    return {
      per: true,
      vac: true,
      sick: true
    };
  }

  /* Get the initial accrual state based on the base record,
   * or set everything to 0 if no base record exists
   * @param baseRec
   * @returns {{per: (number), vac: (number), sickEmp: (number), sickFam: (number), sickDon: (number)}} */
  const getInitialAccState = (baseRec) => {
    baseRec = baseRec || {};
    return {
      per: baseRec.personalUsed || 0,
      vac: baseRec.vacationUsed || 0,
      sickEmp: baseRec.sickEmpUsed || 0,
      sickFam: baseRec.sickFamUsed || 0,
      sickDon: baseRec.sickDonated || 0,
      validation: getCleanValidation()
    }
  }

  /* Apply an annual rollover from 'lastRecord' to 'record'.
   * Truncate sick and vacation banked hours if they exceed maximums.
   * Reset accrual state to 0 used hours.
   *
   * @param record
   * @param lastRecord
   * @param accState */
  const applyRollover = (record, lastRecord, accState) => {
    record.vacationBanked = Math.min(lastRecord.vacationAvailable, maxVacationBanked);
    record.sickBanked = Math.min(lastRecord.sickAvailable, maxSickBanked);

    accState.per = accState.vac = accState.sickEmp = accState.sickFam = accState.sickDon = 0;
  }

  /* Update the give accrual state with the biweek used values from the given record
   * @param rec
   * @param accState */
  const updateAccrualState = (rec, accState) => {
    accState.per += rec.biweekPersonalUsed || 0;
    accState.vac += rec.biweekVacationUsed || 0;
    accState.sickEmp += rec.biweekSickEmpUsed || 0;
    accState.sickFam += rec.biweekSickFamUsed || 0;
    accState.sickDon += rec.biweekSickDonated || 0;
  }

  /* Set annual usage totals on the given record with the values from the given accrual state
   * @param rec
   * @param accState */
  const setRecordUsedHours = (rec, accState) => {
    rec.personalUsed =  accState.per;
    rec.vacationUsed =  accState.vac;
    rec.sickEmpUsed = accState.sickEmp;
    rec.sickFamUsed = accState.sickFam;
    rec.sickDonated = accState.sickDon;
    rec.holidayUsed = rec.holidayUsed || 0;
  }

  /* Calculate the available hours for the given record
   * @param rec */
  const calculateAvailableHours = (rec) => {
    rec.personalAvailable = rec.personalAccruedYtd - rec.personalUsed;
    rec.vacationAvailable = rec.vacationAccruedYtd + rec.vacationBanked - rec.vacationUsed;
    rec.sickAvailable = rec.sickAccruedYtd + rec.sickBanked - rec.sickEmpUsed - rec.sickFamUsed - rec.sickDonated;
  }

  /* Validate the record based on the accrual values present and the validation status of previous records
   * Set the validation results to the running validation on the accrual state
   * If a value is invalid for one record, then the same value type is invalid for all remaining records
   * @param record
   * @param accState */
  const validateRecord = (record, accState) => {
    var validation = accState.validation;

    validation.per = validation.per && isPerValid(record);
    validation.vac = validation.vac && isVacValid(record);
    validation.sick = validation.sick && isSickEmpValid(record) && isSickFamValid(record) && isSickDonationValid(record);

    // Store a snapshot of the running validation to this record
    record.validation = validation;

    // Mark the full record as valid iff all fields are valid
    record.valid = validation.per && validation.vac && validation.sick;
  }


  // Validation functions for each accrual usage type
  const isPerValid = (record) => {
    return isValidValue(record.biweekPersonalUsed, record.personalAvailable);
  }
  const isVacValid = (record) => {
    return isValidValue(record.biweekVacationUsed, record.vacationAvailable);
  }
  const isSickEmpValid =(record) => {
    return isValidValue(record.biweekSickEmpUsed, record.sickAvailable);
  }
  const isSickFamValid = (record) => {
    return isValidValue(record.biweekSickFamUsed, record.sickAvailable);
  }
  const isSickDonationValid = (record) => {
    return isValidValue(record.biweekSickDonated, record.sickAvailable)
  }

  /* Generic validation function for an accrual value
   *
   * Ensure that the value is..
   * null or numeric
   * divisible by 0.5
   * not using more hours than available
   * @param value
   * @param available
   * @returns {boolean} */
  const isValidValue = (value, available) => {
    return value === null ||
      value !== undefined && available >= 0 && value % 0.5 === 0;
  }
  const setChangedFlags = (updatedProjections, index, type) => {
    for (let i = index; i < updatedProjections.length; i++) {
      updatedProjections[i].changed[type] = true;
    }

    // setTimeout(() => resetChangedFlags(updatedProjections), 300);
    // Delay the reset of changed flags by 300 ms and pass updatedProjections
    setTimeout(() => resetChangedFlags(updatedProjections), 200);
  }
  const resetChangedFlags = (updatedProjections) => {
    const resetProjections = updatedProjections.map((record) => {
      return {
        ...record,
        changed: {},
      };
    });
    setProjections(resetProjections);
  };

  // accrual-utils
  /* Returns true iff the given record is the first record of its year
 * @param record
 * @returns {boolean} */
  const isFirstRecordOfYear = (record) => {
    let beginDate = new Date(record.payPeriod.startDate);
    return beginDate.getMonth() === 0 && beginDate.getDate() === 1;
  };

  return (
    <>
      {isLoading() && (<LoadingIndicator/>)}

      {!isLoading() && error && (
        <EssNotification
          level="warn"
          title={error.title}
          message={error.message}
        />
      )}

      {/*{isTe && (*/}
      {/*  <div styles={{ marginTop: '10px' }}>*/}
      {/*    <jsp:include page="te-accruals.jsp" />*/}
      {/*  </div>*/}
      {/*)}*/}

      {!isLoading() && (
        <div className={styles.contentContainer}>
          {!hideTitle && empSupInfo && (
            <h1 className={styles.contentInfo}>
              {empSupInfo.empFirstName} {empSupInfo.empLastName} Accrual Projections
            </h1>
          )}
          {projections.length === 0 ? (
            <p className={styles.contentInfo}>No projections exist for this year.</p>
          ) : (
             <div>
               <p className={styles.contentInfo}>
                 The following hours are projected and can be adjusted as time records are processed.<br />
                 Enter hours into the 'Use' column to view projected available hours. No changes will be saved.<br />
                 Click a row to view or print a detailed summary of projected accrual hours.
               </p>
               <table className={`${styles.accrualTable} ${styles.projections}`}
                      // float-thead-enabled="floatTheadEnabled" float-thead="floatTheadOpts"
               >
                 <thead>
                 <tr>
                   <th colSpan="3">Pay Period</th>
                   <th colSpan="2" className="">Personal Hours</th>
                   <th colSpan="3" className="">Vacation Hours</th>
                   <th colSpan="4" className="">Sick Hours</th>
                 </tr>
                 <tr>
                   <th className={styles.payPeriod}>#</th>
                   <th className={styles.date}>Start Date</th>
                   <th className={styles.date}>End Date</th>
                   <th className={`${styles.personal} ${styles.usedHours}`}>Use</th>
                   <th className={`${styles.personal} ${styles.availableHours}`}>Avail</th>
                   <th className={`${styles.vacation} ${styles.rate}`}>Rate</th>
                   <th className={`${styles.vacation} ${styles.usedHours}`}>Use</th>
                   <th className={`${styles.vacation} ${styles.availableHours}`}>Avail</th>
                   <th className={`${styles.sick} ${styles.rate}`}>Rate</th>
                   <th className={`${styles.sick} ${styles.usedHours}`}>Emp Use</th>
                   <th className={`${styles.sick} ${styles.usedHours}`}>Fam Use</th>
                   <th className={`${styles.sick} ${styles.usedHours}`}>Donated</th>
                   <th className={`${styles.sick} ${styles.availableHours}`}>Avail</th>
                 </tr>
                 </thead>
                 <tbody>
                 {projections.map((record, index) => (
                   <tr key={index}
                       className={`${record.payPeriod.current ? styles.highlighted : ''} ${!record.valid ? styles.invalid : ''}`}
                       id={index === projections.length - 1 ? 'earliest-projection' : undefined}
                       title={record.valid ? 'Open a Detail View of this Record' : ''}>
                     <td className={styles.payPeriod} onClick={() => viewDetails(record)}>
                       {record.payPeriod.payPeriodNum}
                     </td>
                     <td className={styles.date} onClick={() => viewDetails(record)}>
                       {formatDateToMMDDYYYY(record.payPeriod.startDate)}
                     </td>
                     <td className={styles.date} onClick={() => viewDetails(record)}>
                       {formatDateToMMDDYYYY(record.payPeriod.endDate)}
                     </td>
                     <td className={`${styles.accrualHours} ${styles.personal} ${styles.usedHours}`}
                         title="Project Personal Hour Usage">
                       <input type="number" min="0" max={record.maxHours} step=".5" placeholder="0"
                              value={projections[index].biweekPersonalUsed}
                              onChange={(e) => onAccUsageChange('biweekPersonalUsed', 'personal', index, e)}
                              className={!isPerValid(record) ? styles.invalid : ''} />
                       {/*<ComponentA fieldName={'biweekPersonalUsed'} record={record} />*/}
                     </td>
                     <td className={`${styles.accrualHours} ${styles.personal} ${styles.availableHours} ${record.changed.personal ? styles.changed : ''}`}
                         style={{ fontWeight: '600' }}
                         onClick={() => viewDetails(record)}>
                       {record.validation.per ? record.personalAvailable : '--'}
                     </td>
                     <td className={`${styles.accrualHours} ${styles.vacation} ${styles.rate}`} onClick={() => viewDetails(record)}>
                       {record.vacationRate}
                     </td>
                     <td className={`${styles.accrualHours} ${styles.vacation} ${styles.usedHours}`}
                         title="Project Vacation Hour Usage">
                       <input type="number" min="0" max={record.maxHours} step=".5" placeholder="0"
                              value={projections[index].biweekVacationUsed}
                              onChange={(e) => onAccUsageChange('biweekVacationUsed', 'vacation', index, e)}
                              className={!isVacValid(record) ? styles.invalid : ''} />
                     </td>
                     <td className={`${styles.accrualHours} ${styles.vacation} ${styles.availableHours} ${record.changed.vacation ? styles.changed : ''}`}
                         style={{ fontWeight: '600' }}
                         onClick={() => viewDetails(record)}>
                       {record.validation.vac ? record.vacationAvailable : '--'}
                     </td>
                     <td className={`${styles.accrualHours} ${styles.sick} ${styles.rate}`} onClick={() => viewDetails(record)}>
                       {record.sickRate}
                     </td>
                     <td className={`${styles.accrualHours} ${styles.sick} ${styles.usedHours}`}
                         title="Project Employee Sick Hour Usage">
                       <input type="number" min="0" max={record.maxHours} step=".5" placeholder="0"
                              value={projections[index].biweekSickEmpUsed}
                              onChange={(e) => onAccUsageChange('biweekSickEmpUsed', 'sick', index, e)}
                              className={!isSickEmpValid(record) ? styles.invalid : ''} />
                     </td>
                     <td className={`${styles.accrualHours} ${styles.sick} ${styles.usedHours}`}
                         title="Project Family Sick Hour Usage">
                       <input type="number" min="0" max={record.maxHours} step=".5" placeholder="0"
                              value={projections[index].biweekSickFamUsed}
                              onChange={(e) => onAccUsageChange('biweekSickFamUsed', 'sick', index, e)}
                              className={!isSickFamValid(record) ? styles.invalid : ''} />
                     </td>
                     <td className={`${styles.accrualHours} ${styles.sick} ${styles.usedHours}`}
                         title="Project Sick Hour Donations">
                       <input type="number" min="0" max={record.maxHours} step=".5" placeholder={projections[index].biweekSickDonated || 0}
                              value={projections[index].biweekSickDonated}
                              onChange={(e) => onAccUsageChange('biweekSickDonated', 'sick', index, e)}
                              className={!isSickDonationValid(record) ? styles.invalid : ''} />
                     </td>
                     <td className={`${styles.accrualHours} ${styles.sick} ${styles.availableHours} ${record.changed.sick ? styles.changed : ''}`}
                         style={{ fontWeight: '600' }}
                         onClick={() => viewDetails(record)}>
                       {record.validation.sick ? record.sickAvailable : '--'}
                     </td>
                   </tr>
                 ))}
                 </tbody>
               </table>
             </div>
           )}
        </div>
      )}
    </>
  );
}