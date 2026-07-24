/**
 * Time record entry validation, ported from the entryValidators of the legacy
 * RecordEntryController (assets/js/src/time/record/record-entry-ctrl.js).
 *
 * The legacy page kept a tree of boolean error flags on the scope and set them as each entry
 * was validated. This module produces that same tree, plus the set of fields that should be
 * marked invalid, from a record and the reference data it is validated against.
 */

/** Hours are entered in half hour increments for annual pay, quarter hours for temporary pay. */
const RA_SA_INCREMENT = 0.5;
const TE_INCREMENT = 0.25;

/** The most hours that may be entered in a day for a single accrual type. */
const MAX_ACCRUAL_HOURS = 12;
const MAX_WORK_HOURS = 24;

/** Returns the error flag tree with every flag cleared. */
function getEmptyErrors() {
  return {
    // Errors on regular / special annual pay entries.
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
      noMiscType2Given: false,
      noMisc2HoursGiven: false,
      halfHourIncrements: false,
      notEnoughMiscTime: false,
      notEnoughMisc2Time: false,
    },
    // Errors on temporary pay entries.
    te: {
      workHoursInvalidRange: false,
      notEnoughWorkHours: false,
      noComment: false,
      noWorkHoursForComment: false,
      fifteenMinIncrements: false,
    },
    // Errors on the record as a whole, which do not depend on its time entries.
    record: {
      prevUnsubmittedRecord: false,
    },
  };
}

/**
 * Validates every entry of a record.
 *
 * @param record The record being entered, with daily totals already calculated.
 * @param context.totals Record wide hour totals.
 * @param context.accrual Accruals available to the employee, if the record has annual entries.
 * @param context.holidays Holidays keyed by ISO date, if they have been loaded.
 * @param context.miscLeaveGrants Misc leave grants with their hours remaining.
 * @param context.miscLeaveNames Misc leave short names keyed by type.
 * @param context.availableHours Temporary work hours still within the yearly allowance.
 * @param context.hasPrevUnsubmittedRecord Whether an earlier annual record is unsubmitted.
 *
 * @returns An object holding the error flags, the misc leave overages to describe, the set of
 *          invalid "<entryIndex>:<field>" keys, and convenience booleans.
 */
export function validateRecord(record, context) {
  const errors = getEmptyErrors();
  const miscLeaveUsageErrors = [];
  const invalidFields = new Set();

  errors.record.prevUnsubmittedRecord = Boolean(
    context.hasPrevUnsubmittedRecord,
  );

  const state = { errors, miscLeaveUsageErrors, invalidFields, ...context };
  const entries = record.timeEntries || [];

  entries.forEach((entry, index) => {
    if (entry.payType === "TE") {
      validateTempEntry(entry, index, state);
    } else {
      validateAnnualEntry(entry, index, entries, state);
    }
  });

  return {
    ...errors,
    miscLeaveUsageErrors,
    invalidFields,
    hasRaSaErrors: hasAnyError(errors.raSa),
    hasTeErrors: hasAnyError(errors.te),
    hasEntryErrors: hasAnyError(errors.raSa) || hasAnyError(errors.te),
    hasRecordErrors: hasAnyError(errors.record),
  };
}

function hasAnyError(errorGroup) {
  return Object.values(errorGroup).some(Boolean);
}

/** True if nothing was entered in a field, which is always valid. */
function isUnentered(hours) {
  return hours === null || hours === undefined || hours === 0;
}

/**
 * Checks that hours divide evenly into the given increment.
 *
 * The whole part is dropped first, the way the legacy check did, so that only the fractional
 * part is tested. This is kept as is to avoid floating point differences.
 */
function isValidIncrement(hours, increment) {
  return isNaN(hours) || (hours % 1) % increment === 0;
}

function markInvalid(state, index, field) {
  state.invalidFields.add(`${index}:${field}`);
}

/** --- Regular / Special Annual entry validation --- */

function validateAnnualEntry(entry, index, entries, state) {
  const validators = {
    workHours: validateAnnualWorkHours,
    holidayHours: validateHolidayHours,
    vacationHours: validateVacationHours,
    personalHours: validatePersonalHours,
    sickEmpHours: validateSickEmpHours,
    sickFamHours: validateSickFamHours,
    miscHours: validateMiscHours,
    miscType: validateMiscType,
    misc2Hours: validateMisc2Hours,
    miscType2: validateMiscType2,
    totalHours: validateTotalHours,
  };

  Object.entries(validators).forEach(([field, validate]) => {
    if (!validate(entry, index, entries, state)) {
      markInvalid(state, index, field);
    }
  });
}

function validateAnnualWorkHours(entry, index, entries, state) {
  const hours = entry.workHours;
  if (isUnentered(hours)) {
    return true;
  }
  let isValid = true;
  if (hours < 0 || hours > MAX_WORK_HOURS) {
    state.errors.raSa.workHoursInvalidRange = true;
    isValid = false;
  }
  return checkAnnualIncrement(hours, state) && isValid;
}

function validateHolidayHours(entry, index, entries, state) {
  // Holiday hours are only granted to special annual employees on an actual holiday.
  if (entry.payType !== "SA" || !state.holidays || !isHoliday(entry, state)) {
    return true;
  }
  const hours = entry.holidayHours;
  if (isUnentered(hours)) {
    return true;
  }
  let isValid = true;
  if (hours < 0 || hours > getHolidayHours(entry, state)) {
    state.errors.raSa.holidayHoursInvalidRange = true;
    isValid = false;
  }
  return checkAnnualIncrement(hours, state) && isValid;
}

function validateVacationHours(entry, index, entries, state) {
  const hours = entry.vacationHours;
  if (isUnentered(hours)) {
    return true;
  }
  let isValid = true;
  if (
    state.accrual &&
    state.totals.vacationHours > state.accrual.vacationAvailable
  ) {
    state.errors.raSa.notEnoughVacationTime = true;
    isValid = false;
  }
  if (hours < 0 || hours > MAX_ACCRUAL_HOURS) {
    state.errors.raSa.vacationHoursInvalidRange = true;
    isValid = false;
  }
  return checkAnnualIncrement(hours, state) && isValid;
}

function validatePersonalHours(entry, index, entries, state) {
  const hours = entry.personalHours;
  if (isUnentered(hours)) {
    return true;
  }
  let isValid = true;
  if (
    state.accrual &&
    state.totals.personalHours > state.accrual.personalAvailable
  ) {
    state.errors.raSa.notEnoughPersonalTime = true;
    isValid = false;
  }
  if (hours < 0 || hours > MAX_ACCRUAL_HOURS) {
    state.errors.raSa.personalHoursInvalidRange = true;
    isValid = false;
  }
  return checkAnnualIncrement(hours, state) && isValid;
}

function validateSickEmpHours(entry, index, entries, state) {
  const hours = entry.sickEmpHours;
  if (isUnentered(hours)) {
    return true;
  }
  let isValid = isEnoughSickTime(state);
  if (hours < 0 || hours > MAX_ACCRUAL_HOURS) {
    state.errors.raSa.sickEmpHoursInvalidRange = true;
    isValid = false;
  }
  return checkAnnualIncrement(hours, state) && isValid;
}

function validateSickFamHours(entry, index, entries, state) {
  const hours = entry.sickFamHours;
  if (isUnentered(hours)) {
    return true;
  }
  let isValid = isEnoughSickTime(state);
  if (hours < 0 || hours > MAX_ACCRUAL_HOURS) {
    state.errors.raSa.sickFamHoursInvalidRange = true;
    isValid = false;
  }
  return checkAnnualIncrement(hours, state) && isValid;
}

function validateMiscHours(entry, index, entries, state) {
  const hours = entry.miscHours;
  if (isUnentered(hours)) {
    return true;
  }
  let isValid = true;
  if (hours < 0 || hours > MAX_ACCRUAL_HOURS) {
    state.errors.raSa.miscHoursInvalidRange = true;
    isValid = false;
  }
  isValid = checkAnnualIncrement(hours, state) && isValid;
  return (
    checkMiscLeaveGrants(entry, index, entries, state, {
      hoursField: "miscHours",
      typeField: "miscType",
      errorFlag: "notEnoughMiscTime",
    }) && isValid
  );
}

function validateMisc2Hours(entry, index, entries, state) {
  const hours = entry.misc2Hours;
  if (isUnentered(hours)) {
    return true;
  }
  let isValid = true;
  if (hours < 0 || hours > MAX_ACCRUAL_HOURS) {
    state.errors.raSa.miscHoursInvalidRange = true;
    isValid = false;
  }
  isValid = checkAnnualIncrement(hours, state) && isValid;
  return (
    checkMiscLeaveGrants(entry, index, entries, state, {
      hoursField: "misc2Hours",
      typeField: "miscType2",
      errorFlag: "notEnoughMisc2Time",
    }) && isValid
  );
}

function validateMiscType(entry, index, entries, state) {
  return checkMiscTypePairing(entry, state, {
    hoursField: "miscHours",
    typeField: "miscType",
    missingTypeFlag: "noMiscTypeGiven",
    missingHoursFlag: "noMiscHoursGiven",
  });
}

function validateMiscType2(entry, index, entries, state) {
  return checkMiscTypePairing(entry, state, {
    hoursField: "misc2Hours",
    typeField: "miscType2",
    missingTypeFlag: "noMiscType2Given",
    missingHoursFlag: "noMisc2HoursGiven",
  });
}

/** Misc hours and a misc type must always be given together. */
function checkMiscTypePairing(
  entry,
  state,
  { hoursField, typeField, missingTypeFlag, missingHoursFlag },
) {
  const typePresent = entry[typeField] !== null && entry[typeField] !== "";
  const hoursPresent = entry[hoursField] > 0;

  if (!typePresent && hoursPresent) {
    state.errors.raSa[missingTypeFlag] = true;
    return false;
  }
  if (typePresent && !hoursPresent) {
    state.errors.raSa[missingHoursFlag] = true;
    return false;
  }
  return true;
}

/**
 * Checks the misc hours entered against the grants that cover them.
 *
 * Hours are counted from the start of the record through the entry being validated, so that
 * the first entry to exceed a grant is the one flagged.
 */
function checkMiscLeaveGrants(
  entry,
  index,
  entries,
  state,
  { hoursField, typeField, errorFlag },
) {
  let isValid = true;

  (state.miscLeaveGrants || []).forEach((grantInfo) => {
    // A grant with no hours remaining recorded is an unlimited grant.
    if (
      grantInfo.hoursRemaining === null ||
      !grantApplies(grantInfo.grant, entry, hoursField, typeField)
    ) {
      return;
    }

    let hoursUsed = 0;
    for (let i = 0; i <= index; i++) {
      if (grantApplies(grantInfo.grant, entries[i], hoursField, typeField)) {
        hoursUsed += entries[i][hoursField];
      }
    }

    if (hoursUsed > grantInfo.hoursRemaining) {
      state.errors.raSa[errorFlag] = true;
      state.miscLeaveUsageErrors.push({
        shortname: state.miscLeaveNames?.[entry[typeField]],
        range: `${formatGrantDate(grantInfo.grant.beginDate)} - ${formatGrantDate(grantInfo.grant.endDate)}`,
        hoursUsed,
        hoursRemaining: grantInfo.hoursRemaining,
      });
      isValid = false;
    }
  });

  return isValid;
}

/** True if the entry uses hours of the grant's leave type on a date the grant covers. */
function grantApplies(grant, entry, hoursField, typeField) {
  return Boolean(
    entry[hoursField] &&
    entry[typeField] === grant.miscLeaveType &&
    grant.beginDate <= entry.date &&
    entry.date <= grant.endDate,
  );
}

/** Turns "2024-01-04" into "1/4/2024". */
function formatGrantDate(isoDate) {
  const [year, month, day] = isoDate.split("-");
  return `${parseInt(month)}/${parseInt(day)}/${year}`;
}

function validateTotalHours(entry, index, entries, state) {
  // An unentered total shows nothing to the user, so flagging it would only be confusing.
  if (
    isNaN(entry.total) ||
    (entry.total >= 0 && entry.total <= MAX_WORK_HOURS)
  ) {
    return true;
  }
  state.errors.raSa.totalHoursInvalidRange = true;
  return false;
}

function isEnoughSickTime(state) {
  const sickTotal = state.totals.sickEmpHours + state.totals.sickFamHours;
  if (state.accrual && sickTotal > state.accrual.sickAvailable) {
    state.errors.raSa.notEnoughSickTime = true;
    return false;
  }
  return true;
}

function checkAnnualIncrement(hours, state) {
  if (isValidIncrement(hours, RA_SA_INCREMENT)) {
    return true;
  }
  state.errors.raSa.halfHourIncrements = true;
  return false;
}

/** --- Temporary entry validation --- */

function validateTempEntry(entry, index, state) {
  if (!validateTempWorkHours(entry, state)) {
    markInvalid(state, index, "workHours");
  }
  if (!validateTempComment(entry, state)) {
    markInvalid(state, index, "empComment");
  }
}

function validateTempWorkHours(entry, state) {
  const hours = entry.workHours;
  if (isUnentered(hours)) {
    return true;
  }
  let isValid = true;
  if (hours < 0 || hours > MAX_WORK_HOURS) {
    state.errors.te.workHoursInvalidRange = true;
    isValid = false;
  }
  if (state.availableHours < 0) {
    state.errors.te.notEnoughWorkHours = true;
    isValid = false;
  }
  if (!isValidIncrement(hours, TE_INCREMENT)) {
    state.errors.te.fifteenMinIncrements = true;
    isValid = false;
  }
  return isValid;
}

function validateTempComment(entry, state) {
  const hours = entry.workHours;
  const comment = entry.empComment;

  if (hours > 0 && !comment) {
    state.errors.te.noComment = true;
    return false;
  }
  // Zero hours may be commented on, but a comment with no hours at all may not.
  if ((hours === null || hours === undefined) && comment) {
    state.errors.te.noWorkHoursForComment = true;
    return false;
  }
  return true;
}

/** --- Holiday helpers, shared with the entry table --- */

export function isHoliday(entry, { holidays }) {
  return Boolean(holidays && holidays[entry.date]);
}

/**
 * Returns the hours of holiday time granted for an entry's date.
 * Falls back to a full holiday while holidays are loading, so that no error flickers.
 */
export function getHolidayHours(entry, { holidays }) {
  if (!holidays) {
    return 7;
  }
  return isHoliday(entry, { holidays }) ? holidays[entry.date].hours : 0;
}
