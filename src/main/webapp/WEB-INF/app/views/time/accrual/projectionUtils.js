/**
 * The projected accrual arithmetic behind the Accrual Projections page.
 *
 * Ported from the legacy accrualProjections directive
 * (assets/js/src/time/accrual/accrual-projections-directive.js). Nothing here is saved: the
 * employee enters hours they expect to use, and every later pay period is re-totalled so they
 * can see what would be left. Where the legacy code mutated the records in place, these
 * functions return new objects so React sees the change.
 */

const MAX_VACATION_BANKED = 210;
const MAX_SICK_BANKED = 1400;

/** The most hours a projection may use in a single day of the pay period. */
const MAX_HOURS_PER_DAY = 12;

/** The usage fields the employee types into. */
export const DELTA_FIELDS = [
  "biweekPersonalUsed",
  "biweekVacationUsed",
  "biweekSickEmpUsed",
  "biweekSickFamUsed",
  "biweekSickDonated",
];

/**
 * True for accrual records that are projections the employee can still plan against: computed
 * but not yet submitted, for a period they were accruing in and not temporary.
 */
export function isValidProjection(acc) {
  return (
    acc.computed &&
    !acc.submitted &&
    acc.empState?.payType !== "TE" &&
    acc.empState?.employeeAccruing
  );
}

/** True for accrual records covering periods that have already been processed. */
export function isSubmittedSummary(acc) {
  return !acc.computed || acc.submitted;
}

/** Prepares a fetched projection for display and entry. */
export function initializeProjection(projection) {
  const initialized = { ...projection };
  // Zeroed usage fields start empty so the employee can type straight into them.
  DELTA_FIELDS.forEach((field) => {
    if (initialized[field] === 0) {
      initialized[field] = null;
    }
  });
  initialized.changed = {};
  initialized.maxHours = projection.payPeriod.numDays * MAX_HOURS_PER_DAY;
  initialized.validation = cleanValidation();
  initialized.valid = true;
  return initialized;
}

/**
 * Re-totals every projection from the most recent processed record forward.
 *
 * @param projections The projections, in chronological order.
 * @param accSummaries The processed records, most recent first. Their first entry is the
 *                     starting point for the year's running usage.
 * @returns A new array of projections with their used, available and validation fields set.
 */
export function recalculateProjections(projections, accSummaries) {
  const baseRec = accSummaries.length > 0 ? accSummaries[0] : null;
  const accState = getInitialAccState(baseRec);
  const recalculated = [];
  let multiYear = false;

  for (let i = 0; i < projections.length; i++) {
    const rec = { ...projections[i] };
    const lastRec = i === 0 ? baseRec : recalculated[i - 1];

    // Once a rollover has happened the banked hours are ours to carry, not the server's.
    if (multiYear) {
      rec.vacationBanked = lastRec.vacationBanked;
      rec.sickBanked = lastRec.sickBanked;
    }

    if (lastRec && isFirstRecordOfYear(rec)) {
      multiYear = true;
      applyRollover(rec, lastRec, accState);
    }

    updateAccrualState(rec, accState);
    setRecordUsedHours(rec, accState);
    calculateAvailableHours(rec);
    validateRecord(rec, accState);

    recalculated.push(rec);
  }

  return recalculated;
}

/**
 * Flags the changed accrual type on the given record and every record after it, so their
 * available hours can flash the color of the type that moved.
 */
export function setChangedFlags(projections, changedIndex, type) {
  return projections.map((projection, index) =>
    index < changedIndex
      ? projection
      : { ...projection, changed: { ...projection.changed, [type]: true } },
  );
}

/** Clears the flags set by setChangedFlags, letting the flashed cells fade back. */
export function clearChangedFlags(projections) {
  return projections.map((projection) => ({ ...projection, changed: {} }));
}

/* --- Per field validation, also used to shade the inputs --- */

export function isPerValid(record) {
  return isValidValue(record.biweekPersonalUsed, record.personalAvailable);
}

export function isVacValid(record) {
  return isValidValue(record.biweekVacationUsed, record.vacationAvailable);
}

export function isSickEmpValid(record) {
  return isValidValue(record.biweekSickEmpUsed, record.sickAvailable);
}

export function isSickFamValid(record) {
  return isValidValue(record.biweekSickFamUsed, record.sickAvailable);
}

export function isSickDonationValid(record) {
  return isValidValue(record.biweekSickDonated, record.sickAvailable);
}

/* --- Internals --- */

function cleanValidation() {
  return { per: true, vac: true, sick: true };
}

function getInitialAccState(baseRec) {
  const base = baseRec || {};
  return {
    per: base.personalUsed || 0,
    vac: base.vacationUsed || 0,
    sickEmp: base.sickEmpUsed || 0,
    sickFam: base.sickFamUsed || 0,
    sickDon: base.sickDonated || 0,
    validation: cleanValidation(),
  };
}

function isFirstRecordOfYear(record) {
  return /-01-01$/.test(record.payPeriod?.startDate || "");
}

/**
 * Rolls the previous year's remaining hours into the new year's banked hours, truncated at
 * the banking maximums, and starts the year's usage over.
 */
function applyRollover(record, lastRecord, accState) {
  record.vacationBanked = Math.min(
    lastRecord.vacationAvailable,
    MAX_VACATION_BANKED,
  );
  record.sickBanked = Math.min(lastRecord.sickAvailable, MAX_SICK_BANKED);

  accState.per = 0;
  accState.vac = 0;
  accState.sickEmp = 0;
  accState.sickFam = 0;
  accState.sickDon = 0;
}

function updateAccrualState(rec, accState) {
  accState.per = round(accState.per + (rec.biweekPersonalUsed || 0));
  accState.vac = round(accState.vac + (rec.biweekVacationUsed || 0));
  accState.sickEmp = round(accState.sickEmp + (rec.biweekSickEmpUsed || 0));
  accState.sickFam = round(accState.sickFam + (rec.biweekSickFamUsed || 0));
  accState.sickDon = round(accState.sickDon + (rec.biweekSickDonated || 0));
}

function setRecordUsedHours(rec, accState) {
  rec.personalUsed = accState.per;
  rec.vacationUsed = accState.vac;
  rec.sickEmpUsed = accState.sickEmp;
  rec.sickFamUsed = accState.sickFam;
  rec.sickDonated = accState.sickDon;
  rec.holidayUsed = rec.holidayUsed || 0;
}

function calculateAvailableHours(rec) {
  rec.personalAvailable = round(rec.personalAccruedYtd - rec.personalUsed);
  rec.vacationAvailable = round(
    rec.vacationAccruedYtd + rec.vacationBanked - rec.vacationUsed,
  );
  rec.sickAvailable = round(
    rec.sickAccruedYtd +
      rec.sickBanked -
      rec.sickEmpUsed -
      rec.sickFamUsed -
      rec.sickDonated,
  );
}

/**
 * Once a value type is invalid it stays invalid for every remaining record, since the hours
 * they are drawn from can no longer be trusted.
 */
function validateRecord(record, accState) {
  const validation = accState.validation;

  validation.per = validation.per && isPerValid(record);
  validation.vac = validation.vac && isVacValid(record);
  validation.sick =
    validation.sick &&
    isSickEmpValid(record) &&
    isSickFamValid(record) &&
    isSickDonationValid(record);

  record.validation = { ...validation };
  record.valid = validation.per && validation.vac && validation.sick;
}

/** An entered value has to be empty, or a half hour multiple drawn from hours that exist. */
function isValidValue(value, available) {
  return (
    value === null ||
    (value !== undefined && available >= 0 && value % 0.5 === 0)
  );
}

/** Hours are half hour multiples, so two decimals is enough to drop float noise. */
function round(hours) {
  return Math.round(hours * 100) / 100;
}
