import React from "react";

/**
 * Format a date string to a readable format.
 *
 * @param {String} dateString The date string to format.
 * @returns {String} The formatted date string.
 */
export function formatDate(dateString) {
  const options = {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: true
  };
  return new Date(dateString).toLocaleString('en-US', options);
}

/**
 * Format a date string to MM/DD/YYYY.
 *
 * @param {String} dateString The date string to format (in YYYY-MM-DD format).
 * @returns {String} The formatted date string.
 */
export function formatDateToMMDDYYYY(dateString){
  const [year, month, day] = dateString.split('-');
  return `${month}/${day}/${year}`;
};

export function formatDateYYYYMMDD(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

export function formatDayShort(dateString) {
  const date = new Date(`${dateString}T00:00:00Z`);
  const options = { weekday: 'short', timeZone: 'UTC' };
  return new Intl.DateTimeFormat('en-US', options).format(date);
}

export function formatDateStandard(dateString) {
  const dateParts = dateString.split('-');
  const date = new Date(Date.UTC(dateParts[0], dateParts[1] - 1, dateParts[2]));
  console.log("dateString", dateString, "date", date);
  return `${date.getUTCMonth() + 1}/${date.getUTCDate()}/${date.getUTCFullYear()}`;
}



export function timeRecordStatus(status, showColor) {
  const statusDispMap = {
    NOT_SUBMITTED: ["Not Submitted", "#444444"],
    SUBMITTED: ["Submitted", "#0e4e5a"],
    DISAPPROVED: ["Supervisor Disapproved", "#B90504"],
    APPROVED: ["Supervisor Approved", "#799933"],
    DISAPPROVED_PERSONNEL: ["Personnel Disapproved", "#B90504"],
    SUBMITTED_PERSONNEL: ["Submitted Personnel", "#808d0a"],
    APPROVED_PERSONNEL: ["Personnel Approved", "#799933"]
  };
  const [statusText, color] = statusDispMap.hasOwnProperty(status)
                              ? statusDispMap[status]
                              : ["Unknown Status", "red"];
  if(!showColor) return statusText;
  return `<span style='color:${color}'>${statusText}</span>`;
}

/**
 * Colors a number based on whether it's positive or negative to provide a
 * visual cue.
 *
 * Example,
 * given 7 -> +7 (green)
 * given -3 -> -3 (red)
 * given 0 -> 0 (default color)
 */
export function hoursDiffHighlighter(accruals) {
  let color = "#0e4e5a";
  let sign = "";
  let hours = (accruals.serviceYtd - accruals.serviceYtdExpected).toFixed(2);
  hours > 0 ? (color = "#09BB05", sign = "+") : hours < 0 && (color = "#BB0505");
  return <span style={{ color: color }}>{sign} {hours}</span>;
};

/**
 * Colors a number based on whether it's positive or negative to provide a
 * visual cue.
 *
 * Example,
 * given 7 -> +7 (green)
 * given -3 -> -3 (red)
 * given 0 -> 0 (default color)
 *
 * @param Int positive The variable being subtracted from
 * @param Int negative The variable subtracting
 *
 */
export function hoursDiffHighlighterCustom(positive, negative) {
  let color = "#0e4e5a";
  let sign = "";
  let hours = (positive - negative).toFixed(2);
  hours > 0 ? (color = "#09BB05", sign = "+") : hours < 0 && (color = "#BB0505");
  return <span style={{ color: color }}>{sign} {hours}</span>;
};

// Helper functions to determine entry types
function isTemporaryEmployee(entry) {
  return entry.payType === 'TE';
}
function isSalariedEmployee(entry) {
  return entry.payType === 'RA' || entry.payType === 'SA';
}
// Function to check if there are temporary or salaried entries
export function checkEntryTypes(entries) {
  let tempEntries = false;
  let annualEntries = false;

  for (const entry of entries) {
    if (isTemporaryEmployee(entry)) {
      tempEntries = true;
    } else if (isSalariedEmployee(entry)) {
      annualEntries = true;
    }
  }
  return { tempEntries, annualEntries };
}

export function entryHoursFilter(value) {
    return isNaN(parseInt(value)) ? "--" : value;
}

// Returns a display label for the given misc leave id

let miscLeaveMap = {
  EXTRAORDINARY_LEAVE:{
    shortName: 'Extraordinary Leave', //This is as much as I could guess from examples
  },
};
export function miscLeave(miscLeaveType, defaultLabel) {
  // Cannot do this angular implementation bc have not yet globally defined miscLeaves Array
  // appProps.miscLeaves.forEach((miscLeave) => {
  //   //Populate map with miscLeaves array given from global variable
  //   // key: miscLeave.type, value: miscLeave object
  //   miscLeaveMap[miscLeave.type] = miscLeave;
  // });

  // if miscLeave exists in map from globally set miscLeave array, return the attached shortName
  if (miscLeaveMap.hasOwnProperty(miscLeaveType)) {
    return miscLeaveMap[miscLeaveType].shortName;
  }
  // if empty, return provided defaultLabel or '--' if none is provided
  if (!miscLeaveType) {
    return defaultLabel ? defaultLabel : '--';
  }

  // default unknown print
  return miscLeaveType + "?!";
}