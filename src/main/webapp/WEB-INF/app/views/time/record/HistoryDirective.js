import React, { useState, useEffect } from 'react';
import styles from '../universalStyles.module.css';
import { fetchAttendanceRecordApi, fetchTimeRecordApi, fetchActiveYearsTimeRecordsApi } from './time-record-ctrl';
import { formatDateYYYYMMDD } from 'app/views/time/helpers';
import {
  calculateDailyTotals,
  compareRecords,
  formatAttendRecord,
  getRecordTotals,
} from 'app/views/time/record/recordUtils';
import ActiveAttendanceRecords from 'app/views/time/record/ActiveAttendanceRecords';
import SubmittedAttendanceRecords from 'app/views/time/record/SubmittedAttendanceRecords';



// Issues:
//         timesheetMap does not get set fast enough in initTimesheetRecords() before combineRecords()
//         Need the "No Employee Records For 2019" content
//         AnnualTotals wrong numbers = only adding top row numbers and keeping a running total across all records previously selected
const HistoryDirective = ({ viewDetails, user, empSupInfo, linkToEntryPage, scopeHideTitle }) => {
  const [state, setState] = useState({
    supId: user.employeeId,
    searching: false,
    request: {
      tRecYears: false,
      records: false,
    },
    todayDate: new Date(),
    selectedEmp: {},
    recordYears: [],
    selectedRecYear: -1,
    records: {
      employee: [],
      submitted: [],
    },
    timesheetMap: {},
    timesheetRecords: [],
    attendRecords: [],
    annualTotals: {},
  });

  const hideTitle = scopeHideTitle || false;

  useEffect(() => {
    setEmpId();
  }, [empSupInfo]);

  useEffect(() => {
    // setState((prevState) => ({
    //   ...prevState,
    //     timesheetRecords: [],
    //     timesheetMap: {},
    //     records: {},
    //     attendRecords: [],
    //     annualTotals: {},
    // }));
    if (state.selectedEmp.empId) {
      getTimeRecordYears();
    }
    console.log("state.selectedEmp: ", state.selectedEmp)
  }, [state.selectedEmp]);

  useEffect(() => {
    if (state.selectedRecYear !== -1) {
      getRecords();
    }
  }, [state.selectedRecYear]);

  const setEmpId = () => {
    if (empSupInfo && empSupInfo.empId) {
      setState((prevState) => ({
        ...prevState,
        selectedEmp: empSupInfo,
      }));
    } else {
      setState((prevState) => ({
        ...prevState,
        selectedEmp: {
          empId: user.employeeId,
        },
      }));
    }
  };

  const getTimeRecordYears = async () => {
    if (!state.selectedEmp.empId) return;

    setState((prevState) => ({
      ...prevState,
      selectedRecYear: -1,
      request: {
        ...prevState.request,
        tRecYears: true,
      },
    }));

    try {
      const resp = await fetchActiveYearsTimeRecordsApi({ empId: state.selectedEmp.empId });
      handleActiveYearsResponse(resp);
    } catch (error) {
      console.error(error);
    } finally {
      setState((prevState) => ({
        ...prevState,
        request: {
          ...prevState.request,
          tRecYears: false,
        },
      }));
    }
  };

  const handleActiveYearsResponse = (resp) => {
    const emp = state.selectedEmp;
    const isUserSup = emp && emp.supId === state.supId;
    const startDate = isUserSup ? emp.supStartDate : emp.effectiveStartDate;
    const endDate = isUserSup ? emp.supEndDate : emp.effectiveEndDate;
    const supStartYear = new Date(startDate || 0).getFullYear();
    const supEndYear = new Date(endDate || Date.now()).getFullYear();
    const recordYrs = resp.years.filter((year) => year >= supStartYear && year <= supEndYear).reverse();

    setState((prevState) => ({
      ...prevState,
      recordYears: recordYrs,
      selectedRecYear: recordYrs.length > 0 ? `${recordYrs[0]}` : -1,
    }));
  };

  const getRecords = async () => {
    const emp = state.selectedEmp;
    if (!emp.empId || state.selectedRecYear < 0) return;

    const year = parseInt(state.selectedRecYear, 10);
    if (!year || year < 0) {
      return;
    }

    const isUserSup = emp.supId === state.supId;
    const supStartDate = isUserSup ? emp.supStartDate : emp.effectiveStartDate;
    const supEndDate = isUserSup ? emp.supEndDate : emp.effectiveEndDate;

    const yearStart = new Date(year, 0, 1);
    const nextYearStart = new Date(year + 1, 0, 1);

    const supStartMoment = new Date(supStartDate || 0);
    const supEndMoment = new Date(supEndDate || '3000-01-01');

    const fromMoment = new Date(Math.max(yearStart, supStartMoment));
    const toMoment = new Date(Math.min(nextYearStart, supEndMoment));

    if (fromMoment > toMoment) return;

    setState((prevState) => ({
      ...prevState,
      request: {
        ...prevState.request,
        records: true,
      },
    }));

    try {
      const [timesheetRecordsResp, attendRecordsResp] = await Promise.all([
        fetchTimeRecordApi({ empId: emp.empId, from: formatDateYYYYMMDD(yearStart), to: formatDateYYYYMMDD(nextYearStart) }),
        fetchAttendanceRecordApi({ empId: emp.empId, from: formatDateYYYYMMDD(yearStart), to: formatDateYYYYMMDD(nextYearStart) }),
      ]);

      setState((prevState) => ({
        ...prevState,
        timesheetRecords: timesheetRecordsResp.result.items[emp.empId] || [],
        attendRecords: attendRecordsResp.records,
      }));
      console.log("timesheetRecordsResp.result.items: ", timesheetRecordsResp.result.items);
      console.log("timesheetRecordsResp.result.items[emp.empId]: ", timesheetRecordsResp.result.items[emp.empId], " at emp.empId: ", emp.empId);
      initTimesheetRecords(timesheetRecordsResp.result.items[emp.empId]);
      initAttendRecords(attendRecordsResp.records);
      combineRecords(timesheetRecordsResp.result.items[emp.empId], attendRecordsResp.records);
    } catch (error) {
      console.error(error);
    } finally {
      setState((prevState) => ({
        ...prevState,
        request: {
          ...prevState.request,
          records: false,
        },
      }));
    }
    console.log("state", state);
  };

  const initTimesheetRecords = (records) => {
    const timesheetMap = {};
    if(records) {
      records.forEach((record) => {
        calculateDailyTotals(record);
        record.totals = getRecordTotals(record);
        timesheetMap[record.timeRecordId] = record;
      });
    }

    setState((prevState) => ({
      ...prevState,
      timesheetMap,
    }));
  };

  const initAttendRecords = (records) => {
    if(records.length !== 0) {
      records.forEach(formatAttendRecord);
    }
  };

  const combineRecords = (timesheetRecords, attendRecords) => {
    const records = {
      employee: [],
      submitted: [],
    };
    let paperTimesheetsDisplayed = false;
    let attendEnd = new Date('1970-01-01T00:00:00');

    if(attendRecords.length !== 0) {
      attendRecords.forEach((attendRecord) => {
        if (new Date(attendRecord.endDate) > attendEnd) {
          attendEnd = new Date(attendRecord.endDate);
        }
        if (attendRecord.timesheetIds.length === 0) {
          paperTimesheetsDisplayed = true;
          records.submitted.push(attendRecord);
          return;
        }
        attendRecord.timesheetIds.forEach((tsId) => {
          const record = timesheetRecords.find((r) => r.timeRecordId === tsId);
          if (!record) {
            console.error('Could not find timesheet with id:', tsId);
            return;
          }
          records.submitted.push(record);
        });
      });
    }

    if(timesheetRecords) {
      timesheetRecords.forEach((timesheet) => {
        if (new Date(timesheet.endDate) > attendEnd) {
          if (timesheet.scope === 'E') {
            records.employee.push(timesheet);
          } else {
            records.submitted.push(timesheet);
          }
        }
      });
    }

    // This is only adding the submitted records (records.submitted array)
    records.submitted.forEach(addToAnnualTotals);

    setState((prevState) => ({
      ...prevState,
      records: {
        employee: records.employee.sort(compareRecords).reverse(),
        submitted: records.submitted.sort(compareRecords).reverse(),
      },
      paperTimesheetsDisplayed,
    }));
  };

  const addToAnnualTotals = (record) => {
    const annualTotals = { ...state.annualTotals };
    Object.keys(record.totals).forEach((field) => {
      if (!annualTotals[field]) {
        annualTotals[field] = 0;
      }
      annualTotals[field] += record.totals[field];
    });

    setState((prevState) => ({
      ...prevState,
      annualTotals,
    }));
  };

  const isLoading = () => {
    return Object.values(state.request).some((req) => req === true);
  };

  const isUser = () => { return state.selectedEmp.empId === user.employeeId; };

  const showDetails = (record) => {
    if (record.paperTimesheet) return;
    if (record.scope === 'E' && linkToEntryPage) return;
    viewDetails(record);
  };

  return (
    <div>
      {isLoading() && <div className={styles.loader}></div>}

      {!isLoading() && state.recordYears.length > 0 && (<div className={styles.contentContainer}>
        {/*FIX THIS*/}
        {/*<div className={hideTitle || isUser() ? styles.contentControls : ''}>*/}
        <div className={false ? styles.contentControls : ''}>
          {/*{!(hideTitle || isUser()) && (*/}
          {!(false) && (
            <h1 className={styles.contentInfo}>
              {state.selectedEmp.empFirstName} {state.selectedEmp.empLastName}'s Attendance Records
            </h1>
          )}
          <p className={styles.contentInfo} style={{ marginBottom: 0, backgroundColor: 'white' }}>
            View attendance records for year &nbsp;
            <select
              style={{ color: 'black', fontWeight: '400' }}
              value={state.selectedRecYear}
              onChange={(e) => setState({ ...state, selectedRecYear: e.target.value })}
            >
              {state.recordYears.map((year) => (
                <option key={year} value={year}>
                  {year}
                </option>
              ))}
            </select>
          </p>
        </div>
      </div>)}

      {!isLoading() && state.records.employee.length === 0 && state.records.submitted.length === 0 && (
        <div className={styles.contentContainer}>
          <ess-notification level="warn" title={`No Employee Records For ${state.selectedRecYear}`}>
            <p>
              It appears as if the employee has no records for the selected year.
              <br />
              Please contact Senate Personnel at (518) 455-3376 if you require any assistance.
            </p>
          </ess-notification>
        </div>
      )}

      {!isLoading() && state.records.employee.length > 0 && (
        <ActiveAttendanceRecords state={state} linkToEntryPage={linkToEntryPage} showDetails={showDetails} />
      )}

      {!isLoading() && state.records.submitted.length > 0 && (
        <SubmittedAttendanceRecords state={state} showDetails={showDetails} />
      )}

      {!isLoading() && state.recordYears.length === 0 && (
        <div className={styles.contentContainer}>
          <ess-notification level="info" title="No Time Record History">
            <p>
              {isUser() ? 'You have' : `${empSupInfo?.fullName} has`} no time records.
            </p>
            {empSupInfo?.senator && (
              <p>
                {empSupInfo.fullName} is a Senator and does not currently enter time.
              </p>
            )}
          </ess-notification>
        </div>
      )}
    </div>
  );
};

export default HistoryDirective;
