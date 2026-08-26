import React, { useMemo, useState } from "react";
import { format } from "date-fns";
import { useDebounce } from "use-debounce";
import Button from "app/components/Button";
import Notification from "app/components/Notification";
import ErrorAlert from "app/components/ErrorAlert";
import { useConfig } from "app/hooks/useConfig";
import { useHolidaysDuring } from "app/views/time/useHoliday";
import { useAccruals } from "app/views/time/useAccrual";
import { useExpectedHours } from "app/views/time/useExpectedHours";
import { useAllowance } from "app/views/time/useAllowance";
import {
  computeRemaining,
  getAvailableHours,
} from "app/views/time/allowanceUtils";
import {
  getRecordTotals,
  hasAnnualEntries,
  hasTempEntries,
  withDailyTotals,
} from "app/views/time/attendance/recordUtils";
import {
  useMiscLeaveGrants,
  useMiscLeaveTypeList,
  useSaveTimeRecord,
} from "app/views/time/attendance/useTimeRecords";
import { validateRecord } from "app/views/time/attendance/record-entry/recordEntryValidation";
import useEntryFocus from "app/views/time/attendance/record-entry/useEntryFocus";
import AccrualBar from "app/views/time/attendance/record-entry/AccrualBar";
import AllowanceBar from "app/views/time/attendance/record-entry/AllowanceBar";
import AnnualEntryTable from "app/views/time/attendance/record-entry/AnnualEntryTable";
import TempEntryTable from "app/views/time/attendance/record-entry/TempEntryTable";
import {
  ExpectedHoursModal,
  FutureEndDateModal,
  PostSaveModal,
  RecordModifiedModal,
  SavingModal,
  SubmitAckModal,
  UnsubmittedTeModal,
} from "app/views/time/attendance/record-entry/RecordEntryModals";

/** Error codes returned when the record was modified outside of this page. */
const EXTERNAL_MODIFICATION_ERROR_CODES = ["2", "7"];

/**
 * How long entry has to settle before errors are reported, matching the debounceDelay of the
 * legacy record validator (assets/js/src/time/record/record-validation.js).
 */
const VALIDATION_DEBOUNCE_MS = 350;

/**
 * Time entry for a single record.
 *
 * The record fetched from the server is copied into a draft that the entry tables edit, and
 * the draft is what gets saved. Mount this with a key of the record being entered so that
 * selecting another record starts from a fresh draft.
 *
 * @param record The record to enter time for.
 * @param records Every active employee record, used to warn about earlier ones.
 * @param empId The employee entering time.
 * @param onSaved Called once the employee is done with a completed save.
 */
export default function RecordEntryForm({ record, records, empId, onSaved }) {
  const [draft, setDraft] = useState(record);
  const [isDirty, setIsDirty] = useState(false);
  // Set once a save has been attempted, which opens the tab order up to every entered field.
  const [recordFocused, setRecordFocused] = useState(false);
  const [dialogQueue, setDialogQueue] = useState([]);
  const [postSave, setPostSave] = useState(null);
  const [saveError, setSaveError] = useState(null);
  const [isExternallyModified, setIsExternallyModified] = useState(false);

  const focus = useEntryFocus();
  const { data: config } = useConfig();
  const saveRecord = useSaveTimeRecord();

  const annualEntries = hasAnnualEntries(record);
  const tempEntries = hasTempEntries(record);

  const accruals = useAccruals(
    annualEntries ? empId : null,
    record.payPeriod?.startDate,
  );
  const expectedHours = useExpectedHours(
    annualEntries ? empId : null,
    record.beginDate,
    record.endDate,
  );
  const allowance = useAllowance(
    tempEntries ? empId : null,
    parseInt(record.beginDate.slice(0, 4)),
  );
  const holidays = useHolidaysDuring(record.beginDate, record.endDate);
  const miscLeaveGrants = useMiscLeaveGrants(empId, record.beginDate);
  const miscLeaveTypes = useMiscLeaveTypeList();

  const { entries, totals } = useMemo(() => tallyDraft(draft), [draft]);

  /*
   * Errors are reported against the entries as they stood a moment ago, so that a value on its
   * way to being typed is not called wrong before the employee has finished it. The legacy
   * page did this by debouncing validation on the same delay.
   */
  const [settledDraft] = useDebounce(draft, VALIDATION_DEBOUNCE_MS);
  const settled = useMemo(() => tallyDraft(settledDraft), [settledDraft]);

  const yearAllowance = useMemo(
    () => computeRemaining(allowance.data, record),
    [allowance.data, record],
  );

  const miscLeaveNames = useMemo(
    () =>
      (miscLeaveTypes.data || []).reduce((names, miscLeave) => {
        names[miscLeave.type] = miscLeave.shortName;
        return names;
      }, {}),
    [miscLeaveTypes.data],
  );

  const hasPrevUnsubmittedRecord = hasEarlierAnnualRecord(records, record);

  const validationContext = (recordTotals) => ({
    totals: recordTotals,
    accrual: accruals.data,
    holidays: holidays.data,
    miscLeaveGrants: miscLeaveGrants.data,
    miscLeaveNames,
    availableHours: getAvailableHours(
      yearAllowance,
      recordTotals.tempWorkHours,
    ),
    hasPrevUnsubmittedRecord,
  });

  /** What the employee sees: every entry validated, a beat behind the keystrokes. */
  const validation = validateRecord(
    { timeEntries: settled.entries },
    validationContext(settled.totals),
  );

  /**
   * What a save is decided on. Saving can happen inside the debounce window, so the decision
   * is made against what is in the fields right now rather than what is on screen.
   */
  const validateNow = () =>
    validateRecord({ timeEntries: entries }, validationContext(totals));

  const isRequestInProgress =
    saveRecord.isPending ||
    accruals.isFetching ||
    expectedHours.isFetching ||
    allowance.isFetching ||
    holidays.isFetching ||
    miscLeaveGrants.isFetching;

  const updateEntry = (index, changes) => {
    setDraft((current) => ({
      ...current,
      timeEntries: current.timeEntries.map((entry, i) =>
        i === index ? { ...entry, ...changes } : entry,
      ),
    }));
    setIsDirty(true);
  };

  const attemptSave = (submit) => {
    setSaveError(null);
    setRecordFocused(true);

    const saveValidation = validateNow();
    if (
      saveValidation.hasEntryErrors ||
      (submit && saveValidation.hasRecordErrors)
    ) {
      return;
    }

    if (submit) {
      setDialogQueue(getSubmitDialogs());
    } else {
      save(false);
    }
  };

  /** The confirmations shown, in order, before a record may be submitted. */
  const getSubmitDialogs = () => {
    const dialogs = [];
    if (getPrevUnsubmittedRecords(records, record).length > 0) {
      dialogs.push("unsubmittedTe");
    }
    if (
      annualEntries &&
      expectedHours.data?.periodHoursExpected > totals.raSaTotal
    ) {
      dialogs.push("expectedHours");
    }
    if (record.endDate > todayIsoDate()) {
      dialogs.push("futureEndDate");
    }
    dialogs.push("submitAck");
    return dialogs;
  };

  const advanceDialogs = () => {
    const remaining = dialogQueue.slice(1);
    setDialogQueue(remaining);
    if (remaining.length === 0) {
      save(true);
    }
  };

  const save = (submit) => {
    saveRecord.mutate(
      { record: draft, submit },
      {
        onSuccess: () => setPostSave({ submit }),
        onError: (error) => {
          if (isExternalModification(error)) {
            setIsExternallyModified(true);
          } else {
            setSaveError(error);
          }
        },
      },
    );
  };

  const isValid = !validation.hasEntryErrors;
  const isSubmittable =
    !isRequestInProgress &&
    isValid &&
    !validation.hasRecordErrors &&
    !isRecordEmpty(draft);

  return (
    <div>
      {validation.record.prevUnsubmittedRecord && (
        <Notification
          level="warn"
          className="my-5"
          title="Earlier Unsubmitted Records"
          message="This record cannot be submitted until all previous annual salary records are submitted."
        />
      )}

      {isDisapproved(record) && (
        <Notification
          level="error"
          className="mt-5"
          title="Time record requires correction"
          message={record.remarks}
        />
      )}

      <div className="mt-3 bg-white">
        <p className="p-3 text-center">
          All hours available need approval from appointing authority.
        </p>

        {annualEntries && tempEntries && (
          <Notification level="warn" title="Record with multiple pay types">
            <p>
              There was a change in pay type during the time covered by this
              record.
              <br />
              Record days have been split into two separate entry tables, one
              for Regular/Special Annual pay, another for Temporary pay
            </p>
          </Notification>
        )}

        {annualEntries && (
          <div className="p-3">
            {tempEntries && (
              <h1 className="my-2 text-center text-lg font-semibold text-teal-700">
                Regular/Special Annual Pay Entries
              </h1>
            )}
            <AccrualBar
              accruals={accruals.data}
              isLoading={accruals.isPending}
            />
            <hr className="mt-3 border-gray-200" />
            <EntryErrors
              show={validation.hasRaSaErrors}
              messages={getRaSaErrorMessages(validation)}
            />
            <AnnualEntryTable
              entries={entries.filter((entry) => entry.payType !== "TE")}
              totals={totals}
              miscEntered={totals.miscHours > 0}
              miscLeaveTypes={miscLeaveTypes.data}
              miscLeaveGrants={miscLeaveGrants.data}
              holidays={holidays.data}
              invalidFields={validation.invalidFields}
              onEntryChange={updateEntry}
              focus={focus}
              recordFocused={recordFocused}
              mixedPayTypes={tempEntries}
            />
          </div>
        )}

        {tempEntries && (
          <div className="p-3">
            {annualEntries && (
              <h1 className="my-2 text-center text-lg font-semibold text-teal-700">
                Temporary Pay Entries
              </h1>
            )}
            <AllowanceBar
              allowance={yearAllowance}
              tempWorkHours={totals.tempWorkHours}
              isLoading={allowance.isPending}
            />
            <hr className="mt-3 border-gray-200" />
            <EntryErrors
              show={validation.hasTeErrors}
              messages={getTeErrorMessages(validation)}
            />
            <TempEntryTable
              entries={entries.filter((entry) => entry.payType === "TE")}
              totals={totals}
              invalidFields={validation.invalidFields}
              onEntryChange={updateEntry}
              focus={focus}
              mixedPayTypes={annualEntries}
            />
          </div>
        )}

        {saveError && (
          <div className="p-3">
            <ErrorAlert title="Your time record could not be saved.">
              {saveError.data?.message || saveError.message}
            </ErrorAlert>
          </div>
        )}

        <div className="flex flex-wrap items-center justify-between gap-3 bg-[#f9f9f9] p-2.5">
          <div className="flex items-center gap-2.5">
            <label className="font-semibold" htmlFor="remarks-text-area">
              Notes / Remarks
            </label>
            <textarea
              id="remarks-text-area"
              className="h-[45px] w-[450px] max-w-full resize-y border border-gray-300 bg-white p-1"
              tabIndex={1}
              value={draft.remarks || ""}
              onChange={(e) => {
                setDraft((current) => ({
                  ...current,
                  remarks: e.target.value,
                }));
                setIsDirty(true);
              }}
            />
          </div>
          <div className="flex gap-3">
            <Button
              className="w-[8.5em]"
              isDisabled={!isDirty || !isValid}
              tabIndex={isDirty && isValid ? 1 : -1}
              onPress={() => attemptSave(false)}
            >
              Save Record
            </Button>
            <Button
              className="w-[8.5em]"
              isDisabled={!isSubmittable}
              tabIndex={isSubmittable ? 1 : -1}
              onPress={() => attemptSave(true)}
            >
              Submit Record
            </Button>
          </div>
        </div>
      </div>

      <UnsubmittedTeModal
        isOpen={dialogQueue[0] === "unsubmittedTe"}
        records={getPrevUnsubmittedRecords(records, record)}
        onConfirm={advanceDialogs}
        onCancel={() => setDialogQueue([])}
      />
      <ExpectedHoursModal
        isOpen={dialogQueue[0] === "expectedHours"}
        accrual={accruals.data}
        recordHrsExpected={expectedHours.data?.periodHoursExpected}
        raSaTotal={totals.raSaTotal}
        onConfirm={advanceDialogs}
        onCancel={() => setDialogQueue([])}
      />
      <FutureEndDateModal
        isOpen={dialogQueue[0] === "futureEndDate"}
        onConfirm={advanceDialogs}
        onCancel={() => setDialogQueue([])}
      />
      <SubmitAckModal
        isOpen={dialogQueue[0] === "submitAck"}
        onConfirm={advanceDialogs}
        onCancel={() => setDialogQueue([])}
      />
      <SavingModal
        isOpen={saveRecord.isPending}
        submit={saveRecord.variables?.submit}
      />
      <PostSaveModal
        isOpen={!!postSave}
        submit={postSave?.submit}
        onContinue={() => {
          setPostSave(null);
          onSaved();
        }}
      />
      <RecordModifiedModal
        isOpen={isExternallyModified}
        helplineNumber={config?.helplinePhoneNumber}
        onRefresh={() => {
          setIsExternallyModified(false);
          onSaved();
        }}
      />
    </div>
  );
}

/** The list of validation failures shown above an entry table. */
function EntryErrors({ show, messages }) {
  if (!show || messages.length === 0) {
    return null;
  }
  return (
    <Notification level="error" className="mt-5" title="Time record has errors">
      <ul className="list-none p-0">
        {messages.map((message, index) => (
          <li key={index}>{message}</li>
        ))}
      </ul>
    </Notification>
  );
}

const RA_SA_ERROR_MESSAGES = {
  workHoursInvalidRange: "Work hours must be between 0 and 24.",
  holidayHoursInvalidRange:
    "Holiday hours must be at least 0 and may not exceed hours granted for the holiday",
  vacationHoursInvalidRange: "Vacation hours must be between 0 and 12.",
  personalHoursInvalidRange: "Personal hours must be between 0 and 12.",
  sickEmpHoursInvalidRange: "Employee sick hours must be between 0 and 12.",
  sickFamHoursInvalidRange: "Family sick hours must be between 0 and 12.",
  miscHoursInvalidRange: "Misc hours must be between 0 and 12.",
  totalHoursInvalidRange: "Total hours must be between 0 and 24.",
  notEnoughVacationTime: "Vacation hours recorded exceeds hours available.",
  notEnoughPersonalTime: "Personal hours recorded exceeds hours available.",
  notEnoughSickTime: "Sick hours recorded exceeds hours available.",
  noMiscTypeGiven: "A Misc type must be given when using Miscellaneous hours.",
  noMiscHoursGiven:
    "Miscellaneous hours must be present when a Misc type is selected.",
  noMiscType2Given: "A Misc type must be given when using Miscellaneous hours.",
  noMisc2HoursGiven:
    "Miscellaneous hours must be present when a Misc type is selected.",
  halfHourIncrements: "Hours must be in increments of 0.5",
};

const TE_ERROR_MESSAGES = {
  workHoursInvalidRange: "Work hours must be between 0 and 24",
  notEnoughWorkHours: "Work hours recorded exceeds available work hours",
  fifteenMinIncrements: "Work hours must be in increments of 0.25",
  noComment:
    "Must enter start and end work times for all work blocks during the entered work hours.",
  noWorkHoursForComment:
    "Commented entries must accompany 0 or more work hours entered",
};

function getRaSaErrorMessages(validation) {
  const messages = Object.entries(RA_SA_ERROR_MESSAGES)
    .filter(([flag]) => validation.raSa[flag])
    .map(([, message]) => message);

  if (validation.raSa.notEnoughMiscTime || validation.raSa.notEnoughMisc2Time) {
    validation.miscLeaveUsageErrors.forEach((usage) => {
      messages.push(
        `Your total of ${usage.hoursUsed} ${usage.shortname} hours exceeds ` +
          `the limit of ${usage.hoursRemaining} for the period ${usage.range}`,
      );
    });
  }

  return messages;
}

function getTeErrorMessages(validation) {
  return Object.entries(TE_ERROR_MESSAGES)
    .filter(([flag]) => validation.te[flag])
    .map(([, message]) => message);
}

/** The entries and record totals a version of the draft works out to. */
function tallyDraft(draft) {
  const withTotals = withDailyTotals(draft);
  return {
    entries: withTotals.timeEntries.map((entry, index) => ({
      ...entry,
      index,
    })),
    totals: getRecordTotals(withTotals),
  };
}

function isDisapproved(record) {
  return (
    record.recordStatus === "DISAPPROVED" ||
    record.recordStatus === "DISAPPROVED_PERSONNEL"
  );
}

/** Every active record that begins before the one being entered. */
function getPrevUnsubmittedRecords(records, record) {
  return records.filter(
    (other) => other.scope === "E" && other.beginDate < record.beginDate,
  );
}

/**
 * A record may not be submitted while an earlier annual salary record is still unsubmitted,
 * because accruals are applied in order.
 */
function hasEarlierAnnualRecord(records, record) {
  return getPrevUnsubmittedRecords(records, record).some(hasAnnualEntries);
}

/**
 * True if the record holds no time at all, which the employee is not allowed to submit.
 * Ported from isRecordEmpty in the legacy controller, which counts the annual entries that
 * were left completely untouched.
 */
function isRecordEmpty(record) {
  const entries = record.timeEntries || [];
  const untouched = entries.filter(
    (entry) =>
      (entry.payType === "RA" || entry.payType === "SA") &&
      entry.totalHours === 0 &&
      entry.workHours === null &&
      entry.travelHours === null &&
      entry.holidayHours === null &&
      entry.vacationHours === null &&
      entry.personalHours === null &&
      entry.sickEmpHours === null &&
      entry.sickFamHours === null &&
      entry.miscHours === null &&
      entry.misc2Hours === null,
  );
  return untouched.length === entries.length;
}

/** True if a failed save was caused by someone else changing the record first. */
function isExternalModification(error) {
  const errorData = error?.data?.errorData?.items || {};
  return Object.keys(errorData).some((code) =>
    EXTERNAL_MODIFICATION_ERROR_CODES.includes(code),
  );
}

function todayIsoDate() {
  return format(new Date(), "yyyy-MM-dd");
}
