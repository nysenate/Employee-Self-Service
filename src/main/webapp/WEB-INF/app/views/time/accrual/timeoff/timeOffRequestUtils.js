import { format, parseISO } from "date-fns";

/**
 * Time off request helpers, ported from the legacy TimeOffRequestListService and the
 * timeOffRequestStatus / timeOffRequestAccrualType filters
 * (assets/js/src/time/accrual/time-off-request-list-service.js, time-off-request-filters.js).
 */

/** The hour fields a requested day can carry. */
export const REQUEST_HOUR_FIELDS = [
  "workHours",
  "holidayHours",
  "vacationHours",
  "personalHours",
  "sickEmpHours",
  "sickFamHours",
  "miscHours",
  "misc2Hours",
];

const STATUS_LABELS = {
  SUBMITTED: "Submitted",
  SAVED: "Saved",
  APPROVED: "Approved",
  DISAPPROVED: "Disapproved",
  INVALIDATED: "Invalidated",
};

const ACCRUAL_TYPE_LABELS = {
  PERSONAL: "Personal",
  VACATION: "Vacation",
  SICKFAM: "Sick Family",
  SICKEMP: "Sick Employee",
};

export function getRequestStatusLabel(status) {
  return STATUS_LABELS[status] || "Unknown";
}

export function getAccrualTypeLabel(accrualType) {
  return ACCRUAL_TYPE_LABELS[accrualType] || null;
}

/** Reads an hour field as a number, treating an unentered value as zero. */
export function hours(value) {
  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : 0;
}

/** Formats an hour value for a read only table, showing "--" when nothing was entered. */
export function displayHours(value) {
  return value === null || value === undefined || value === "" ? "--" : value;
}

/**
 * Adds the display fields and totals a request list needs: printable dates, total and leave
 * hours, and the sets of accrual and misc leave types the request draws on.
 *
 * Ported from formatData in the legacy service, which annotated the API objects in place. These
 * are copies instead, so a cached query result is never mutated.
 */
export function formatRequest(request) {
  const days = (request.days || []).map((day) => ({
    ...day,
    datePrint: day.date ? format(parseISO(day.date), "EEE., MMM d, yyyy") : "",
  }));

  const miscTypes = new Set();
  const accrualTypes = new Set();
  let totalHours = 0;
  let leaveHours = 0;

  days.forEach((day) => {
    totalHours += hours(day.totalHours);
    /*
     * Everything on the day that is not work. The legacy sum left out misc2Hours while
     * including miscHours, so the Leave Hours column under-reported any day using a second
     * misc leave type.
     */
    leaveHours +=
      hours(day.vacationHours) +
      hours(day.personalHours) +
      hours(day.sickEmpHours) +
      hours(day.sickFamHours) +
      hours(day.miscHours) +
      hours(day.misc2Hours) +
      hours(day.holidayHours);

    if (day.miscType) {
      miscTypes.add(day.miscType);
    }
    if (day.miscType2) {
      miscTypes.add(day.miscType2);
    }
    if (hours(day.personalHours) > 0) {
      accrualTypes.add("PERSONAL");
    }
    if (hours(day.vacationHours) > 0) {
      accrualTypes.add("VACATION");
    }
    if (hours(day.sickFamHours) > 0) {
      accrualTypes.add("SICKFAM");
    }
    if (hours(day.sickEmpHours) > 0) {
      accrualTypes.add("SICKEMP");
    }
  });

  return {
    ...request,
    days,
    startDatePrint: printDate(request.startDate),
    endDatePrint: printDate(request.endDate),
    timestampPrint: printDate(request.timestamp),
    totalHours,
    leaveHours,
    miscTypes: [...miscTypes],
    accrualTypes: [...accrualTypes],
  };
}

export function formatRequests(requests) {
  return (requests || []).map(formatRequest);
}

function printDate(isoDate) {
  return isoDate ? format(parseISO(isoDate), "MMM do yyyy") : "";
}

/** Orders requests by the day they start, earliest first. */
export function compareRequests(a, b) {
  return (a.startDate || "").localeCompare(b.startDate || "");
}

/**
 * Counts the requests that have not yet finished, by status. These are the counts the side
 * navigation badges show.
 *
 * The legacy version compared an ISO date string against a Date object, which is always false
 * in JavaScript, so every badge sat at zero. Both sides are ISO dates here.
 */
export function countRequestStatuses(requests) {
  const today = format(new Date(), "yyyy-MM-dd");
  const counts = { pending: 0, approved: 0, rejected: 0 };

  (requests || []).forEach((request) => {
    if (!request.endDate || request.endDate < today) {
      return;
    }
    if (request.status === "SUBMITTED") {
      counts.pending++;
    } else if (request.status === "APPROVED") {
      counts.approved++;
    } else if (request.status === "DISAPPROVED") {
      counts.rejected++;
    }
  });

  return counts;
}

/**
 * Removes from the history list any request already shown as active. A request spanning
 * yesterday and today comes back from both queries.
 *
 * The legacy version matched on start and end date, which also dropped a distinct request that
 * happened to cover the same span. Matching on request id is exact.
 */
export function withoutActiveRequests(historyRequests, activeRequests) {
  const activeIds = new Set(
    (activeRequests || []).map((request) => request.requestId),
  );
  return (historyRequests || []).filter(
    (request) => !activeIds.has(request.requestId),
  );
}

/** The sum of every hour field on a day. */
export function dayTotal(day) {
  return REQUEST_HOUR_FIELDS.reduce(
    (total, field) => total + hours(day[field]),
    0,
  );
}

/** The accruals a request would consume, by type. */
export function accrualsUsed(days) {
  return (days || []).reduce(
    (used, day) => ({
      vacation: used.vacation + hours(day.vacationHours),
      personal: used.personal + hours(day.personalHours),
      sick: used.sick + hours(day.sickEmpHours) + hours(day.sickFamHours),
    }),
    { vacation: 0, personal: 0, sick: 0 },
  );
}

/** A request can only be edited before it goes to the supervisor. */
export function isEditable(request) {
  return request?.status !== "APPROVED" && request?.status !== "SUBMITTED";
}
