/**
 * Display labels and colors for time record statuses, ported from the legacy
 * "timeRecordStatus" AngularJS filter (assets/js/src/time/record/record-filters.js).
 */

const RECORD_STATUS_DISPLAY = {
  NOT_SUBMITTED: { label: "Not Submitted", color: "#444444" },
  SUBMITTED: { label: "Submitted", color: "#0e4e5a" },
  DISAPPROVED: { label: "Supervisor Disapproved", color: "#B90504" },
  APPROVED: { label: "Supervisor Approved", color: "#799933" },
  DISAPPROVED_PERSONNEL: { label: "Personnel Disapproved", color: "#B90504" },
  SUBMITTED_PERSONNEL: { label: "Submitted Personnel", color: "#808d0a" },
  APPROVED_PERSONNEL: { label: "Personnel Approved", color: "#799933" },
};

const UNKNOWN_STATUS = { label: "Unknown Status", color: "red" };

export function getRecordStatusDisplay(status) {
  return RECORD_STATUS_DISPLAY[status] || UNKNOWN_STATUS;
}

export function getRecordStatusLabel(status) {
  return getRecordStatusDisplay(status).label;
}
