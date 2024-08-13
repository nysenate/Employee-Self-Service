import React, { useEffect, useState } from "react";
import useAuth from "app/contexts/Auth/useAuth";
import { fetchUniversal, formatDateYYYYMMDD } from "app/views/time/helpers";
import {
  computeRemaining,
  getAvailableHours as getAvailableHoursFromAllowance
} from "app/views/time/allowance/time-allowance-ctrl";
import { calculateDailyTotals, getRecordTotals, getTimeEntryFields } from "app/views/time/record/recordUtils";

// For MiscLeave look at MiscLeaveType.java src/main/java/gov/nysenate/ess/time/model/payrolll
// Needs:
//    -
// Issues:
//    -Period end should not be 0 => fixing dueFromNowStr calculation in record-entry-ctrl.js: getRecords()
//    -
export const useRecordEntryCtrl = () => {
  const { userData } = useAuth()
  const [state, setState] = useState(getInitialState());

  const [recordsLoading, setRecordsLoading] = useState(false);
  const [holidaysLoading, setHolidaysLoading] = useState(false);
  const [accrualsLoading, setAccrualsLoading] = useState(false);
  const [allowancesLoading, setAllowancesLoading] = useState(false);
  const [expectedHrsLoading, setExpectedHrsLoading] = useState(false);
  const [saveLoading, setSaveLoading] = useState(false);

  function getInitialState() {
    function shortnameMap(miscLeaves) {
      let map = {};
      miscLeaves.map((value, index) => {
        let miscLeave = miscLeaves[index];
        map[miscLeave.type] = miscLeave.shortname;
      });
      return map;
    }

    return {
      empId: userData().employee.employeeId,    // Employee Id
      // miscLeaves: appProps.miscLeaves,    // Listing of misc leave types
      miscLeaveGrantInfoList: null,       // List of info about grants of a misc leave type for the currently selected record
      miscLeaveUsageErrors: [],           // Data on misc leave that too much is being used of
      accrual: null,                      // Accrual info for selected record
      expectedHrs: null,                  // An object containing expected hour data for the selected record
      allowances: {},                     // A map that stores yearly temp employee allowances
      selectedYear: 0,                    // The year of the selected record (makes it easy to get the selected record's allowance)
      records: [],                        // All active employee records
      iSelectedRecord: 0,                 // Index of the currently selected record,
      salaryRecs: [],                     // A list of salary recs that are active during the selected record's date range
      iSelSalRec: 0,                      // Index of the selected salary rec (used when there is a salary change mid record)
      tempEntries: false,                 // True if the selected record contains TE pay entries
      annualEntries: false,               // True if the selected record contains RA or SA entries
      totals: {},                         // Stores record wide totals for time entry fields of the selected record
      holidays: null,                     // Stores a map of holidays

      request: {                          // Flags indicating if ajax requests are in progress
        records: false,                 //  Get active records
        accruals: false,                //  Get accruals for selected record
        allowances: false,              //  Get allowances for selected record
        save: false                     //  Save selected record
      },
      // A map for displaying convenience.
      // miscLeavesShortnameMap: shortnameMap(appProps.miscLeaves),
    }
  }
  function resetLoading() {
    setRecordsLoading(false);
    setHolidaysLoading(false);
    setAccrualsLoading(false);
    setAllowancesLoading(false);
    setExpectedHrsLoading(false);
    setSaveLoading(false);
  }

  let modifiedErrorCodes = [2, 7];

  async function init() {
    resetLoading();
    let tempState = getInitialState();
    tempState = await getRecords(tempState);
    tempState = await getHolidays(tempState);
    setState(tempState);
  }

  useEffect(() => {
    init();
  }, []);

  useEffect(() => {
    const updateState = async () => {
      if (state.records && state.records[state.iSelectedRecord]) {
        let updatedState = detectPayTypes(state);
        // console.log("updatedState after detectPayTypes ", updatedState);

        try {
          // Fetch accruals and pass the updated state to the next function
          updatedState = await getAccrualForSelectedRecord(updatedState);
          // console.log("updatedState after getAccrualForSelectedRecord ", updatedState);

          // Fetch allowances using the updated state and pass it to the next function
          updatedState = await getAllowanceForSelRecord(updatedState);
          // console.log("updatedState after getAllowanceForSelRecord ", updatedState);

          // Fetch expected hours using the further updated state
          updatedState = await getExpectedHoursForSelRecord(updatedState);
          // console.log("updatedState after getExpectedHoursForSelRecord ", updatedState);

          // Call additional functions after state is updated
          updatedState = getSelectedSalaryRecs(updatedState);
          // console.log("updatedState after getSelectedSalaryRecs ", updatedState);

          updatedState = onRecordChange(updatedState);
          // console.log("updatedState after onRecordChange ", updatedState);

          setRecordSearchParams();

          updatedState = await getMiscLeaveTypeGrants(updatedState);
          // console.log("updatedState after getMiscLeaveTypeGrants ", updatedState);


          fullValidationCheck(updatedState);
          // console.log("updatedState after fullValidationCheck ", updatedState);

          // Finally, set the state once with all the updates
          setState(updatedState);
        } catch (error) {
          console.error('Error updating state:', error);
        }
      } else {
        console.log("Failed: state.records && state.records[state.iSelectedRecord]");
      }
    };

    updateState();
  }, [state?.records, state?.iSelectedRecord]);

  /** --- API Methods --- */
  /* Fetches the employee's active records from the server, auto-selecting a record
   * if it's end date is supplied in the query params. */
  const getRecords = async (prevState) => {
    //   state.request.records = true;
    // if(JSON.stringify(prevState) === JSON.stringify(getInitialState())) {
    //   console.log("prevState",prevState);
    //   console.log("getInitialState",getInitialState());
    //   console.log('prevState != getInitialState()');
    //   return;
    // }
    const params = { empId: prevState.empId }
    setRecordsLoading(true);
    try {
      const response = await fetchUniversal("/timerecords/active", params);
      if (response.result.items[prevState.empId]) {
        const allRecords = response.result.items[prevState.empId];
        const records = allRecords.filter(record => record.scope === 'E');

        records.forEach((record, index) => {
          const endDate = new Date(record.endDate);
          const dueDate = new Date(endDate);
          dueDate.setDate(endDate.getDate() + 1);
          dueDate.setHours(0, 0, 0, 0);

          const now = new Date();

          // Calculate the difference in milliseconds
          const differenceInMillis = dueDate - now;

          // Convert the difference into various units
          const differenceInSeconds = Math.floor(differenceInMillis / 1000);
          const differenceInMinutes = Math.floor(differenceInSeconds / 60);
          const differenceInHours = Math.floor(differenceInMinutes / 60);
          const differenceInDays = Math.floor(differenceInHours / 24);
          // console.log(`Difference: ${differenceInDays} days, ${differenceInHours} hours, ${differenceInMinutes} minutes, ${differenceInSeconds} seconds`);
          record.dueFromNowStr = differenceInDays;
          record.isDue = dueDate < now;
          record.index = index;

          record.timeEntries.forEach((entry, i) => {
            entry.index = i;
          });

          record.initialRemarks = record.remarks;
          // linkRecordFromQueryParam();
        });
        return { ...prevState, records: records, allRecords: allRecords, };
      }
    } catch(err) { console.error(err); } finally {
      setRecordsLoading(false);
    }
  }

  /* Saves or submits the currently selected record.
   * This assumes any necessary validation has already been
   * made on this record.
   * @param submit - true if the record is to be submitted */
  const saveRecord = async () => { console.log('implement saveRecord'); }

  /**
   * Fetches the accruals for the currently selected time record from the server.
   * Returns updatedState
   * @param prevState
   * @returns updatedState
   */
  const getAccrualForSelectedRecord = async (prevState) => {
    if(prevState.annualEntries) {
      let empId = prevState.empId;
      let record = prevState.records[prevState.iSelectedRecord];
      let periodStartMoment = new Date(record.payPeriod.startDate);
      const params = {
        empId: empId,
        beforeDate: formatDateYYYYMMDD(periodStartMoment),
      };
      setAccrualsLoading(true);
      try {
        const response = await fetchUniversal('/accruals', params);
        return {...prevState, accrual: response.result};
      } catch(err) { console.error(err); } finally {
        setAccrualsLoading(false);
      }
    }
    return prevState;
  }

  /**
   * Fetches the expected hours for the currently selected record
   * Returns updatedState
   * @param prevState
   * @returns updatedState
   */
  const getExpectedHoursForSelRecord = async (prevState) => {
    if(!prevState.annualEntries) return prevState;
    let empId = prevState.empId;
    let record = prevState.records[prevState.iSelectedRecord]
    const params = {
      empId: empId,
      beginDate: record.beginDate,
      endDate: record.endDate,
    };
    setExpectedHrsLoading(true);
    try {
      const response = await fetchUniversal('/expectedhrs', params);
      return {...prevState, expectedHrs: response.result};
    } catch(err) { console.error(err); } finally {
      setExpectedHrsLoading(false);
    }
  }

  /**
   * Gets the allowance state for the year of the selected record, if it hasn't already been retrieved
   * Returns updatedState
   * @param prevState
   * @returns updatedState
   */
  const getAllowanceForSelRecord = async (prevState) => {
    let record = prevState.records[prevState.iSelectedRecord]
    let newState = { ...prevState, selectedYear: new Date(record.beginDate).getFullYear() };
    if (prevState.tempEntries && !prevState.allowances.hasOwnProperty(prevState.selectedYear)) {
      const params = {
        empId: prevState.empId,
        year: prevState.selectedYear
      };
      setAllowancesLoading(true);
      try {
        const response = await fetchUniversal('/allowances', params);
        response.result.forEach((allowance) => {
          newState.allowances[allowance.year] = allowance;
        });
        return { ...newState, };
      } catch(err) { console.error(err); } finally {
        setAllowancesLoading(false);
      }
    }
    return newState
  }

  /**
   * Gets getMiscLeaveTypeGrants
   * Returns updatedState
   * @param prevState
   * @returns updatedState
   */
  const getMiscLeaveTypeGrants = async (prevState) => {
    const params = {
      empId: prevState.empId,
      endDateStr: prevState.records[prevState.iSelectedRecord].beginDate
    };
    try {
      const response = await fetchUniversal('/miscleave/grantsWithRemainingHours', params);
      console.log("miscLeaveGrantInfoList: response.result", response.result);
      console.log("returning: ", { ...prevState, miscLeaveGrantInfoList: response.result});

      return { ...prevState, miscLeaveGrantInfoList: response.result, };
    } catch(err) { console.error(err); } finally {
    }
  }

  /**
   * Gets the holidays
   * Returns updatedState
   * @param prevState
   * @returns updatedState
   */
  const getHolidays = async (prevState) => {
    const fromDateObj = new Date();
    fromDateObj.setFullYear(fromDateObj.getFullYear() - 1);
    const fromDate = fromDateObj.toISOString().split('T')[0];

    const toDateObj = new Date();
    toDateObj.setFullYear(toDateObj.getFullYear() + 1);
    const toDate = toDateObj.toISOString().split('T')[0];

    const params = {
      fromDate: fromDate,
      toDate: toDate
    };
    setHolidaysLoading(true);
    try {
      const response = await fetchUniversal("/holidays", params);
      const holidays = {};
      response.holidays.forEach(holiday => {
        if (!holiday.unofficial) {
          holidays[holiday.date] = holiday;
        }
      });
      return { ...prevState, holidays: holidays}
    } catch(err) { console.error(err); } finally {
      setHolidaysLoading(false);
    }
  }
  function createNextRecord() {
    if(!canCreateNextRecord()) return;
    let latestRecord = getLatestRecord();
    let nextRecBeginDate = new Date(latestRecord.endDate);
    nextRecBeginDate.setDate(nextRecBeginDate.getDate() + 1);
    const params = {
      empId: state.empId,
      date: formatDateYYYYMMDD(nextRecBeginDate),
    };
    setRecordsLoading(true);
    try {
      console.log('Save recordCreationApi = /timerecords/new');
      init();
    } catch(err) {
      console.error(err);
      setRecordsLoading(false);
    }
  }

  /** --- Display Methods --- */
  /* Returns the currently selected record.
   * @returns timeRecord object */
  function getSelectedRecord(thisState) { return thisState?.records[thisState?.iSelectedRecord]; }

  /* Closes any open modals by resolving them.*/
  function resolveModal() { console.log('Resolve: Implement close all modals/popups'); }

  /* Closes any open modals by rejecting them.*/
  function rejectModal() { console.log('Reject: Implement close all modals/popups'); }

  /* Returns true if the given date falls on a weekend.
   * @param date - ISO, JS, new Date, or Moment Date
   * @returns {boolean} - true if weekend, false otherwise.*/
  function isWeekend(date) {
    // Convert the input to a Date object if it's not already one
    const parsedDate = new Date(date);

    // Get the day of the week: 0 for Sunday, 6 for Saturday
    const dayOfWeek = parsedDate.getDay();

    // Return true if the day is Saturday (6) or Sunday (0)
    return dayOfWeek === 0 || dayOfWeek === 6;
  }


  /* This method is called every time a field is modified on the currently selected record. */
  function setDirty(entry, index) {
    let updatedState = { ...state };
    if(Number.isInteger(index) && index >= 0) console.log('Index must be an integer greater than or equal to 0');
    if(state.records[state.iSelectedRecord].timeEntries.filter(entry => entry.payType !== 'TE')[index] === entry)
      console.log("state's index not correlate to entry");
    if(updatedState.records[state.iSelectedRecord].timeEntries.filter(entry => entry.payType !== 'TE')[index] === entry)
      console.log("updatedState's index not correlate to entry");
    updatedState.records[state.iSelectedRecord].dirty = true;
    if (entry) {
      updatedState.records[state.iSelectedRecord].timeEntries.filter(entry => entry.payType !== 'TE')[index].dirty = true;
    }
    updatedState = onRecordChange(updatedState);
    setState({ ...updatedState });
  }

  /* Return true if the selected record is valid, i.e. it exists, and all entries are valid
   * @returns {boolean} */
  function recordValid() {
    let record = getSelectedRecord(state);
    return !(record == null || selRecordHasEntryErrors() );
  }

  function isRecordEmpty(record) {
    const timeEntries = record.timeEntries;
    let nullRecordCount = 0;

    for (let i = 0; i < timeEntries.length; i++) {
      const entry = timeEntries[i];
      const isRASA = entry.payType === "RA" || entry.payType === "SA";
      const hasNoHours = entry.totalHours === 0;
      const hasSomeHours = entry.workHours !== null || entry.travelHours !== null || entry.holidayHours !== null ||
        entry.vacationHours !== null || entry.personalHours !== null || entry.sickEmpHours !== null ||
        entry.sickFamHours !== null || entry.miscHours !== null;

      if (isRASA && hasNoHours) {
        nullRecordCount++;
        if (hasSomeHours) {
          nullRecordCount--;
        }
      }
    }

    // Return true if all entries are null records, false otherwise
    return nullRecordCount === timeEntries.length;
  }


  /* Returns true if the record is submittable, i.e. it exists, passes all validations, and has ended or will end
   * today.
   * @returns {boolean}*/
  function recordSubmittable() {
    return !requestInProgress() &&
      recordValid() &&
      !selRecordHasRecordErrors() &&
      !isRecordEmpty(getSelectedRecord(state));
  }

  /* Get the number of available work hours at the selected salary rate
   *  such that the record cost does not exceed the employee's annual allowance
   * @returns {number} */
  function getAvailableHours() {
    let allowance = state.allowances[$scope.state.selectedYear];
    let tempWorkHours = state.totals.tempWorkHours;
    return getAvailableHoursFromAllowance(allowance, tempWorkHours)
  }

  /*
   * @param salaryRec
   * @returns {string} */
  function getSalRecDateRange(salaryRec) {
    const record = getSelectedRecord(state);

    // Parse dates
    const effectDate = new Date(salaryRec.effectDate);
    const recordBeginDate = new Date(record.beginDate);
    const recordEndDate = new Date(record.endDate);
    const salaryEndDate = new Date(salaryRec.endDate);

    // Determine the beginDate and endDate
    const beginDate = effectDate > recordBeginDate ? effectDate : recordBeginDate;
    const endDate = salaryEndDate > recordEndDate ? recordEndDate : salaryEndDate;

    // Format the dates as M/D
    const formatDate = (date) => `${date.getMonth() + 1}/${date.getDate()}`;

    // Return the formatted date range
    return `${formatDate(beginDate)} - ${formatDate(endDate)}`;
  }


  /* Get the start date of the given salary rec with respect to the selected record
   * @param salaryRec
   * @returns {Date} */
  function getSalRecStartDate(salaryRec) {
    const record = getSelectedRecord(state);
    const effectDate = new Date(salaryRec.effectDate);
    const recordBeginDate = new Date(record.beginDate);
    return effectDate > recordBeginDate ? effectDate : recordBeginDate;
  }

  /* Get the start date of the given salary rec with respect to the selected record
   * @param salaryRec
   * @returns {Date} */
  function getSalRecEndDate(salaryRec) {
    const record = getSelectedRecord(state);
    const salaryEndDate = new Date(salaryRec.endDate);
    const recordEndDate = new Date(record.endDate);
    return salaryEndDate > recordEndDate ? recordEndDate : salaryEndDate;
  }

  /* Returns a formatted string displaying the date range of the given record
   * @param record
   * @returns {string} */
  function getRecordRangeDisplay(record) {
    const beginDate = new Date(record.beginDate);
    const endDate = new Date(record.endDate);
    const formatDate = (date) => `${date.getMonth() + 1}/${date.getDate()}/${date.getFullYear()}`;
    return `${formatDate(beginDate)} - ${formatDate(endDate)}`;
  }

  // /**
  //  * Set the selected record as having been focused when an entry validate event is caught
  //  */
  // $rootScope.$on('validateRecordEntries', function() {
  //   var record = $scope.getSelectedRecord();
  //   record.focused = true;
  // });
  //
  // $scope.isFieldSelected = function (entry, fieldName) {
  //   var fieldIdSelector = '#' + entry.date + '-' + fieldName;
  //   return angular.element(fieldIdSelector).is(':focus');
  // };

  let accrualTabIndex = {
    holiday: getAccrualTabIndexFn('holidayHours'),
    vacation: getAccrualTabIndexFn('vacationHours'),
    personal: getAccrualTabIndexFn('personalHours'),
    sickEmp: getAccrualTabIndexFn('sickEmpHours'),
    sickFam: getAccrualTabIndexFn('sickFamHours'),
    misc: getAccrualTabIndexFn('miscHours')
  };

  function getAccrualTabIndexFn(propName) {}

  /* Checks if the hour total of the annual entries for the selected record
   * is greater than or equal to the biweekly expected hours for the selected pay period
   * @returns {boolean} */
  function expectedHoursEntered() {
    if (!state.annualEntries) {
      return true;
    }
    return state.expectedHrs.periodHoursExpected <= state.totals.raSaTotal;
  }

  /* Checks if the hour total of the annual entries for the selected record
   * is greater than or equal to the biweekly expected hours for the selected pay period
   * @returns {boolean} */
  function futureEndDate() {
    const record = getSelectedRecord(state);
    const today = new Date();
    const endDate = new Date(record.endDate);
    return endDate > today;
  }


  /* Return a misc leave predicate function that will determine if a misc leave can be used on the given date
   * @param date
   * @returns {Function} */
  function getMiscLeavePredicate(date) {
    const dateObj = new Date(date);

    return function(miscLeave) {
      // Return true if the misc leave is not restricted
      if (!miscLeave.restricted) {
        return true;
      }

      const grantInfoList = state.miscLeaveGrantInfoList;

      for (let grantInfo of grantInfoList) {
        const grant = grantInfo.grant;
        const beginDate = new Date(grant.beginDate);
        const endDate = new Date(grant.endDate);

        // Return true if the date falls within the grant date range and is of the same leave type
        if (dateObj < beginDate || dateObj > endDate) {
          continue;
        }
        if (miscLeave.type === grant.miscLeaveType) {
          return true;
        }
      }

      return false;
    };
  }


  /* Returns true iff the given entry is a holiday
   * @param entry
   * @returns {boolean} */
  function isHoliday(entry) {
    return state.holidays && state.holidays.hasOwnProperty(entry.date);
  }


  /**
   * Return the number of holiday hours allotted for the given date
   * Return 7 if the holidays have not yet been loaded to prevent error flickering
   * @param entry
   * @returns {number}
   */
  function getHolidayHours(entry) {
    if (!state.holidays) { // Return the max holiday hours if holidays have not yet loaded
      return 7;
    }
    return state.holidays.hasOwnProperty(entry.date) ? state.holidays[entry.date].hours : 0;
  }


  /* Return true if the employee is eligible to create a new time record for the next period
   * @returns {boolean} */
  function canCreateNextRecord() {
    if (state.records.length > 0) return false;

    // Return false if any existing record has a begin date past the current date
    state.allRecords?.forEach(record => {
      const beginDate = new Date(record.beginDate);
      const currentDate = new Date();

      if (beginDate > currentDate) {
        return false;
      }
    });

    const latestRecord = getLatestRecord();
    const currentDate = new Date();

    // Do not allow next record creation if the latest record does not cover the current day
    if (latestRecord === null || currentDate > new Date(latestRecord.endDate)) {
      return false;
    }

    return true;
  }


  /* Return true if a request is in progress */
  function requestInProgress() {
    return Object.values(state.request).some(value => value === true);
  }

  /* Refreshes totals and validates a record when a change occurs on a record. */
  function onRecordChange(prevState) {
    let record = prevState.records[state.iSelectedRecord];
    // Old file: Todo delay sanitation to allow entry of .25 and .75
    // Old file: sanitizeEntries(record);
    calculateDailyTotals(record);
    return { ...prevState, totals: getRecordTotals(record), }; //this could be a scoping issue, don't think so tho
  }

  /* Ensure that all time entered is in multiples of 0.25 or 0.5 for Temporary and Annual entries respectively
   * @param record */
  function sanitizeEntries(record) {
    const timeEntryFields = getTimeEntryFields();

    record.timeEntries.forEach(entry => {
      const validInterval = isTemporaryEmployee(entry) ? 0.25 : 0.5;
      const inverse = 1 / validInterval;

      timeEntryFields.forEach(fieldName => {
        const value = entry[fieldName];
        if (value) {
          entry[fieldName] = Math.round(value * inverse) / inverse;
        }
      });
    });
  }


  /* Iterates through the entries of the currently selected record,
   * setting the state to indicate if the record has TE pay entries, RA/SA entries or both
   * @param record */
  function detectPayTypes(prevState) {
    let tempEntries = false;
    let annualEntries = false;

    if (prevState.records.length > 0) {
      const record = getSelectedRecord(prevState);
      record.timeEntries.forEach(entry => {
        if (isTemporaryEmployee(entry)) {
          tempEntries = true;
        } else if (isSalariedEmployee(entry)) {
          annualEntries = true;
        }
      });
    }
    return { ...prevState, tempEntries: tempEntries, annualEntries: annualEntries,};
  }


  /* Adds all salaryRecs relevant to the selected record to the salaryRecs state object */
  function getSelectedSalaryRecs(prevState) {
    let updatedState = { ...prevState };
    let salaryRecs = [];
    updatedState.salaryRecs = salaryRecs;
    updatedState.iSelSalRec = 0;

    if (!updatedState.tempEntries) return prevState;

    const allowance = updatedState.allowances[updatedState.selectedYear];
    const record = getSelectedRecord(updatedState);
    let highestRate = 0;

    allowance.salaryRecs.forEach(salaryRec => {
      // Convert dates to Date objects
      const effectDate = new Date(salaryRec.effectDate);
      const endDate = new Date(record.endDate);
      const beginDate = new Date(record.beginDate);
      const salaryEndDate = new Date(salaryRec.endDate);

      // Select only temporary salaries that are effective during the record date range
      if (
        salaryRec.payType === 'TE' &&
        effectDate <= endDate &&
        beginDate <= salaryEndDate
      ) {
        salaryRecs.push(salaryRec);
        if (salaryRec.salaryRate > highestRate) {
          highestRate = salaryRec.salaryRate;
          updatedState.iSelSalRec = allowance.salaryRecs.indexOf(salaryRec);
        }
      }
    });

    // Call the imported function to compute remaining allowance
    computeRemaining(allowance, record);
    return { ...updatedState, };
  }


  function getLatestRecord() {
    let latestRecord = null;
    state.allRecords?.forEach(record => {
      if (!latestRecord || new Date(record.beginDate) > new Date(latestRecord.beginDate)) {
        latestRecord = record;
      }
    });
    return latestRecord;
  }


  /* Recursively ensures that all boolean fields are false within the given object.
   * @param object
   * @returns {boolean} */
  function allFalse(object) {
    if (typeof object === 'boolean') {
      return !object;  // Return true if the boolean is false
    }
    for (const prop in object) {
      if (Object.prototype.hasOwnProperty.call(object, prop) && !allFalse(object[prop])) return false;
    }
    return true;
  }


  /* Sets the search params to indicate the currently active record. */
  function setRecordSearchParams() {
    const record = state.records[state.iSelectedRecord];
    // setSearchParam('record', record.beginDate);
    console.log('implement setSearchParam');
  }


  /* Checks for a 'record' search param in the url and if it exists, the record with a start date that matches
   * the given date will be set as the selected record. */
  function linkRecordFromQueryParam() {
    // const recordParam = getSearchParam('record');
    //
    // if (recordParam) {
    //   // Iterate through the records and find the one with a matching beginDate
    //   for (let i = 0; i < state.records.length; i++) {
    //     const record = state.records[i];
    //     if (record.beginDate === recordParam) {
    //       state.iSelectedRecord = i;
    //       break;
    //     }
    //   }
    // }
    console.log('implement getSearchParam(record)');

  }
  function isTemporaryEmployee(entry) { return entry.payType === 'TE'; }

  function isSalariedEmployee(entry) { return entry.payType === 'RA' || entry.payType === 'SA'; }

  // function getSubmitDialogs() {
  //   const submitDialogs = [];
  //
  //   const prevUnsubmittedTe = getPrevUnsubmittedTe();
  //   if (prevUnsubmittedTe && prevUnsubmittedTe.length > 0) {
  //     submitDialogs.push(() => {
  //       return modals.open("unsubmitted-te-warning", { records: prevUnsubmittedTe }, true);
  //     });
  //   }
  //
  //   if (!expectedHoursEntered(state)) {
  //     submitDialogs.push(() => {
  //       return modals.open("expectedhrs-dialog", {
  //         serviceYtd: state.accrual.serviceYtd,
  //         serviceYtdExpected: state.accrual.serviceYtdExpected,
  //         recordHrsExpected: state.expectedHrs.periodHoursExpected,
  //         raSaTotal: state.totals.raSaTotal
  //       }, true);
  //     });
  //   }
  //
  //   if (futureEndDate(state)) {
  //     submitDialogs.push(() => {
  //       return modals.open("futureenddt-dialog", {}, true);
  //     });
  //   }
  //
  //   return submitDialogs;
  // }



  /** --- Validation --- **/

  /* Runs a full validation check on the selected record
   * Setting error flags as it goes
   * @returns {boolean} true iff the record is valid */
  function fullValidationCheck(updatedState) {
    // Run pre-validation tasks
    preValidation(updatedState);

    const record = getSelectedRecord(updatedState);
    let recordValid = true;

    if (record && record.timeEntries) {
      record.timeEntries.forEach(entry => {
        // Perform validation check on each entry
        recordValid = recordValid && checkEntry(entry);
      });
    }

    return recordValid;
  }


  /* Runs validation checks on the given entry
   * @param entry
   * @returns {boolean} */
  function checkEntry(entry) {
    const validationType = isSalariedEmployee(entry) ? 'raSa' : 'te';
    let entryValid = true;

    // Iterate over the validators using a for...in loop
    for (let key in entryValidators[validationType]) {
      if (entryValidators[validationType].hasOwnProperty(key)) {
        // Call each validator function with necessary arguments
        entryValid = entryValid && entryValidators[validationType][key](entry);
      }
    }

    return entryValid;
  }


  /* This function is called before time entries are validated
   * this resets any error flags (they will be restored if errors are detected during validation)
   * and also does any validations on the record scope */
  function preValidation(updatedState) {
    const record = getSelectedRecord(updatedState);
    errorTypes.reset();
    updatedState = {...state, miscLeaveUsageErrors: [],};
    setState(updatedState);
    checkForPrevUnsubmittedRaSa(record, updatedState);
  }


  /* Check for any unsubmitted salaried records before the given record
   * @param record */
  function checkForPrevUnsubmittedRaSa(record, updatedState) {
    updatedState.records.forEach(otherRecord => {
      if (new Date(otherRecord.beginDate) < new Date(record.beginDate)) {
        otherRecord.timeEntries.forEach(entry => {
          if (isSalariedEmployee(entry)) {
            errorTypes.record.prevUnsubmittedRecord = true;
            return;
          }
        });
      }
    });
  }

  /* Search for and return any unsubmitted temporary time records before the given record. */
  function getPrevUnsubmittedTe() {
    const currentRec = getSelectedRecord(state);
    return state.records.filter(rec => {
      return rec.scope === 'E' &&
        new Date(rec.beginDate) < new Date(currentRec.beginDate);
    });
  }


  // FIX
  let errorTypes = {
    // Error flags for regular / special annual pay time entries
    raSa: {
      workHoursInvalidRange: false,
      holidayHoursInvalidRange: false,
      vacationHoursInvalidRange: false,
      personalHoursInvalidRange: false,
      sickEmpHoursInvalidRange: false,
      sickFamHoursInvalidRange: false,
      miscHoursInvalidRange: false,
      totalHoursInvalidRange: false,
      notEnoughVacationTime: false,
      notEnoughPersonalTime: false,
      notEnoughSickTime: false,
      noMiscTypeGiven: false,
      noMiscHoursGiven: false,
      halfHourIncrements: false,
      notEnoughMiscTime: false
    },
    // Error messages for temporary pay time entries
    te: {
      workHoursInvalidRange: false,
      notEnoughWorkHours: false,
      noComment: false,
      noWorkHoursForComment: false,
      fifteenMinIncrements: false
    },
    // Record scope errors that do not depend on time entries
    record: {
      prevUnsubmittedRecord: false
    },
    // Recursively set all boolean error properties to false
    reset: function(object = this) {
      for (let key in object) {
        if (object.hasOwnProperty(key)) {
          if (typeof object[key] === 'boolean') {
            object[key] = false;
          } else if (typeof object[key] === 'object') {
            this.reset(object[key]);
          }
        }
      }
    }
  };


  /**
   *  --- Error Indication Methods ---
   *  These methods check the 'errorTypes' object for various types of errors
   */
  function selRecordHasEntryErrors() {
    return selRecordHasRaSaErrors() || selRecordHasTeErrors();
  }

  function selRecordHasRaSaErrors() {
    return !allFalse(errorTypes.raSa);
  }

  function selRecordHasTeErrors() {
    return !allFalse(errorTypes.te);
  }

  function selRecordHasRecordErrors() {
    return !allFalse(errorTypes.record);
  }

  /** --- Validation Helper Methods --- */

  /* A helper function that checks if entered sick time exceeds available sick time
   * If available sick time is exceeded, an error flag is set
   * @returns {boolean} indicating if available sick time is enough to cover entered sick time */
  function isEnoughSickTime() {
    let sickTotal = state.totals.sickEmpHours + state.totals.sickFamHours;
    if (state.accrual && sickTotal > state.accrual.sickAvailable) {
      errorTypes.raSa.notEnoughSickTime = true;
      return false;
    }
    return true;
  }

  /* Checks that the given hours are divisible by 0.5
   * according to the standard for regular / special annual time entry
   * @param hours
   * @returns {boolean} */
  // Todo Why are the hours modulated by 1 before 0.5/0.25 ?
  // Seems harmless so I am leaving it in for now in case it is necessary for fp precision etc.
  function checkRaSaHourIncrements(hours) {
    if (isNaN(hours) || hours % 1 % 0.5 === 0) {
      return true;
    }
    errorTypes.raSa.halfHourIncrements = true;
    return false;
  }

  function grantApplies(grant, entry) {
    let entryDate = new Date(entry.date);
    return entry.miscHours && entry.miscType === grant.miscLeaveType
      && new Date(grant.beginDate) <= entryDate && entryDate <= new Date(grant.endDate);
  }

  // e.g. turns "2024-01-04" into "1/4/24"
  function dateToStr(date) {
    let year = date.substring(0, 4)
    let month = parseInt(date.substring(5, 7));
    let dayOfMonth = parseInt(date.substring(8, 10));
    return month + "/" + dayOfMonth + "/" + year;
  }

  /* Checks that the given hours are divisible by 0.25
   * according to the standard for temporary employee time entry
   * @param hours
   * @returns {boolean} */
  function checkTeHourIncrements(hours) {
    if (isNaN(hours) || hours % 1 % 0.25 === 0) {
      return true;
    }
    errorTypes.te.fifteenMinIncrements = true;
    return false;
  }

  /** --- Time Entry Validation Methods --- */

  const entryValidators = {

    /** --- Regular / Special Annual time entry validators --- */

    raSa: {
      workHours: function (entry) {
        const hrs = entry.workHours;
        if (hrs === 0 || hrs === null) {
          return true;
        }
        let isValid = true;
        if (typeof hrs === 'undefined') {
          errorTypes.raSa.workHoursInvalidRange = true;
          isValid = false;
        }
        isValid = isValid && checkRaSaHourIncrements(hrs);
        return isValid;
      },

      holidayHours: function (entry) {
        if (entry.payType !== 'SA' || !state.holidays || !isHoliday(entry, state)) {
          return true;
        }
        const hrs = entry.holidayHours;
        let isValid = true;
        if (hrs === 0 || hrs === null) {
          return true;
        }
        if (typeof hrs === 'undefined') {
          errorTypes.raSa.holidayHoursInvalidRange = true;
          isValid = false;
        }
        isValid = isValid && checkRaSaHourIncrements(hrs);
        return isValid;
      },

      vacationHours: function (entry) {
        const hrs = entry.vacationHours;
        let isValid = true;
        if (hrs === 0 || hrs === null) {
          return true;
        }
        if (state.accrual && state.totals.vacationHours > state.accrual.vacationAvailable) {
          errorTypes.raSa.notEnoughVacationTime = true;
          isValid = false;
        }
        if (typeof hrs === 'undefined') {
          errorTypes.raSa.vacationHoursInvalidRange = true;
          isValid = false;
        }
        isValid = isValid && checkRaSaHourIncrements(hrs);
        return isValid;
      },

      personalHours: function (entry) {
        const hrs = entry.personalHours;
        let isValid = true;
        if (hrs === 0 || hrs === null) {
          return true;
        }
        if (state.accrual && state.totals.personalHours > state.accrual.personalAvailable) {
          errorTypes.raSa.notEnoughPersonalTime = true;
          isValid = false;
        }
        if (typeof hrs === 'undefined') {
          errorTypes.raSa.personalHoursInvalidRange = true;
          isValid = false;
        }
        isValid = isValid && checkRaSaHourIncrements(hrs);
        return isValid;
      },

      sickEmpHours: function (entry) {
        const hrs = entry.sickEmpHours;
        let isValid = true;
        if (hrs === 0 || hrs === null) {
          return true;
        }
        isValid = isValid && isEnoughSickTime();
        if (typeof hrs === 'undefined') {
          errorTypes.raSa.sickEmpHoursInvalidRange = true;
          isValid = false;
        }
        isValid = isValid && checkRaSaHourIncrements(hrs);
        return isValid;
      },

      sickFamHours: function (entry) {
        const hrs = entry.sickFamHours;
        let isValid = true;
        if (hrs === 0 || hrs === null) {
          return true;
        }
        isValid = isValid && isEnoughSickTime();
        if (typeof hrs === 'undefined') {
          errorTypes.raSa.sickFamHoursInvalidRange = true;
          isValid = false;
        }
        isValid = isValid && checkRaSaHourIncrements(hrs);
        return isValid;
      },

      miscHours: function (entry) {
        const hrs = entry.miscHours;
        let isValid = true;
        if (hrs === 0 || hrs === null) {
          return true;
        }
        if (typeof hrs === 'undefined') {
          errorTypes.raSa.miscHoursInvalidRange = true;
          isValid = false;
        }
        isValid = isValid && checkRaSaHourIncrements(hrs);

        const entries = state.records[state.iSelectedRecord].timeEntries;
        const grantInfoList = state.miscLeaveGrantInfoList;

        for (let grantIndex = 0; grantIndex < grantInfoList.length; grantIndex++) {
          const grantInfo = grantInfoList[grantIndex];
          if (grantInfo.hoursRemaining === null || !grantApplies(grantInfo.grant, entry)) {
            continue;
          }
          let hoursUsed = 0.0;

          for (let entryIndex = 0; entryIndex <= entry.index; entryIndex++) {
            const currEntry = entries[entryIndex];
            if (grantApplies(grantInfo.grant, currEntry)) {
              hoursUsed += currEntry.miscHours;
            }
          }
          if (hoursUsed > grantInfo.hoursRemaining) {
            errorTypes.raSa.notEnoughMiscTime = true;
            const shortname = state.miscLeavesShortnameMap[entry.miscType];
            const range = dateToStr(grantInfo.grant.beginDate) + " - " + dateToStr(grantInfo.grant.endDate);
            const data = { shortname: shortname, range: range, hoursUsed: hoursUsed, hoursRemaining: grantInfo.hoursRemaining };
            state.miscLeaveUsageErrors.push(data);
            isValid = false;
          }
        }
        return isValid;
      },

      miscType: function (entry) {
        const miscTypePresent = entry.miscType !== null;
        const miscHoursPresent = entry.miscHours > 0;
        const isActiveRow = entry.index === getActiveRow();
        if (!isActiveRow && !miscTypePresent && miscHoursPresent) {
          errorTypes.raSa.noMiscTypeGiven = true;
          return false;
        }
        if (miscTypePresent && !miscHoursPresent) {
          errorTypes.raSa.noMiscHoursGiven = true;
          return false;
        }
        return true;
      },

      totalHours: function (entry, errorTypes) {
        if (isNaN(entry.total) || (entry.total >= 0 && entry.total <= 24)) {
          return true;
        }
        errorTypes.raSa.totalHoursInvalidRange = true;
        return false;
      }
    },

    /** --- Temporary Time Entry Validators --- */

    te: {
      workHours: function (entry) {
        const hrs = entry.workHours;
        if (hrs === 0 || hrs === null) {
          return true;
        }
        let isValid = true;
        if (typeof hrs === 'undefined') {
          errorTypes.te.workHoursInvalidRange = true;
          isValid = false;
        }
        if (getAvailableHours(state) < 0) {
          errorTypes.te.notEnoughWorkHours = true;
          isValid = false;
        }
        isValid = isValid && checkTeHourIncrements(hrs);
        return isValid;
      },
      comment: function (entry) {
        const hrs = entry.workHours;
        const comment = entry.empComment;
        const isActiveRow = entry.index === getActiveRow();
        if (hrs > 0 && !comment && !isActiveRow) {
          errorTypes.te.noComment = true;
          return false;
        }
        if (hrs === null && comment) {
          errorTypes.te.noWorkHoursForComment = true;
          return false;
        }
        return true;
      }
    }
  };

  let activeRow = null
  const getActiveRow = () => {return activeRow;}
  const setActiveRow = (rowIndex) => {activeRow = rowIndex;}

  return {
    state,
    setState,
    recordsLoading,
    accrualsLoading,
    allowancesLoading,
    canCreateNextRecord,
    createNextRecord,
    errorTypes,
    setDirty,
    saveRecord,
    recordValid,
    recordSubmittable,
    getRecordRangeDisplay,
  };
}










