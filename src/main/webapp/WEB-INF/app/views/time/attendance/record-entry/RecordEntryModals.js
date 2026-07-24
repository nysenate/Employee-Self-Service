import React from "react";
import { Link } from "react-router-dom";
import Modal from "app/components/Modal";
import Button from "app/components/Button";
import LoadingIndicator from "app/components/LoadingIndicator";
import { formatRecordDate } from "app/views/time/attendance/history/RecordTable";

/**
 * The dialogs shown while saving or submitting a time record.
 * Ported from the modals of the legacy entry page
 * (WEB-INF/view/template/time/record/*.jsp).
 */

/** A confirmation the employee must accept before the record is sent on. */
function ConfirmModal({
  isOpen,
  title,
  onConfirm,
  onCancel,
  confirmLabel = "Proceed",
  cancelLabel = "Cancel",
  children,
}) {
  return (
    <Modal isOpen={isOpen} onOpenChange={(open) => !open && onCancel()}>
      <Modal.Title>{title}</Modal.Title>
      <Modal.Body>
        <div className="max-w-2xl text-center">{children}</div>
      </Modal.Body>
      <Modal.Buttons>
        <Button variant="theme" onPress={onConfirm}>
          {confirmLabel}
        </Button>
        <Button variant="secondary" onPress={onCancel}>
          {cancelLabel}
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}

/**
 * Warns that submitting this record closes out any earlier temporary pay records.
 * @param records The earlier records that will become unavailable.
 */
export function UnsubmittedTeModal({ isOpen, records, onConfirm, onCancel }) {
  return (
    <ConfirmModal
      isOpen={isOpen}
      title="Warning: Previous Records Will Be Unavailable."
      onConfirm={onConfirm}
      onCancel={onCancel}
    >
      <p>
        All un-submitted temporary pay records for pay periods before the
        submitted record will be unavailable after submission.
        <br />
        This includes records for the following periods:
      </p>
      <ul className="my-3 list-none p-0">
        {(records || []).map((record) => (
          <li className="my-1" key={record.timeRecordId}>
            {formatRecordDate(record.payPeriod?.startDate)} -{" "}
            {formatRecordDate(record.payPeriod?.endDate)}
          </li>
        ))}
      </ul>
    </ConfirmModal>
  );
}

/**
 * Warns that fewer hours were entered than the pay period requires.
 * @param accrual The employee's accruals, for their year to date hours of service.
 * @param recordHrsExpected Hours the pay period requires.
 * @param raSaTotal Hours entered on the record.
 */
export function ExpectedHoursModal({
  isOpen,
  accrual,
  recordHrsExpected,
  raSaTotal,
  onConfirm,
  onCancel,
}) {
  const serviceSurplus = accrual?.serviceYtd - accrual?.serviceYtdExpected;
  const expectedDifference = recordHrsExpected - raSaTotal;

  return (
    <ConfirmModal
      isOpen={isOpen}
      title="Hours entered are less than pay period requirement"
      onConfirm={onConfirm}
      onCancel={onCancel}
    >
      {serviceSurplus >= expectedDifference ? (
        <p>
          Warning: You are attempting to use {expectedDifference} excess hours.
        </p>
      ) : (
        <div>
          <p>
            Warning: You do not have enough hours to fulfill required pay period
            hours.
          </p>
          <div className="my-3 flex justify-around">
            <span className="font-semibold">
              Required: {recordHrsExpected} hrs.
            </span>
            <span className="font-semibold">Entered: {raSaTotal} hrs.</span>
            <span className="font-semibold">
              Year To Date {serviceSurplus < 0 ? "Deficit" : "Excess"}:{" "}
              {serviceSurplus} hrs.
            </span>
          </div>
        </div>
      )}
    </ConfirmModal>
  );
}

/** Warns that the record being submitted covers days that have not happened yet. */
export function FutureEndDateModal({ isOpen, onConfirm, onCancel }) {
  return (
    <ConfirmModal
      isOpen={isOpen}
      title="Timesheet with a future end date"
      onConfirm={onConfirm}
      onCancel={onCancel}
    >
      <p>
        Warning: You are attempting to submit a timesheet with a future end date
      </p>
    </ConfirmModal>
  );
}

/** The acknowledgement that must be agreed to before a record is submitted. */
export function SubmitAckModal({ isOpen, onConfirm, onCancel }) {
  return (
    <ConfirmModal
      isOpen={isOpen}
      title="Before submitting, you must acknowledge the following:"
      confirmLabel="I agree"
      onConfirm={onConfirm}
      onCancel={onCancel}
    >
      <p>
        1. For purposes of submitting a timesheet, the username and password is
        the electronic signature of the employee. As liability attaches to each
        timesheet, the employee should ensure that his or her username and
        password is securely kept and used.
      </p>
      <hr className="my-3 border-teal-200" />
      <p>
        2. The hours recorded on the Submitted Time and Attendance Record
        accurately reflect time actually spent by me in the performance of my
        assigned duties.
      </p>
      <hr className="my-3 border-teal-200" />
      <p>
        3. You will be saving and submitting this Time and Attendance Record to
        your T&amp;A Supervisor. Once submitted, you will no longer have the
        ability to edit this Record unless your supervisor or Personnel
        disapproves the record.
      </p>
    </ConfirmModal>
  );
}

/** Shown while the record is being sent to the server. */
export function SavingModal({ isOpen, submit }) {
  return (
    <Modal isOpen={isOpen} ariaLabel="Saving time record">
      <Modal.Title>
        {submit
          ? "Saving and submitting time record..."
          : "Saving time record..."}
      </Modal.Title>
      <Modal.Body>
        <div className="px-20 py-4">
          <LoadingIndicator />
        </div>
      </Modal.Body>
    </Modal>
  );
}

/** Confirms the save, and offers the employee somewhere to go next. */
export function PostSaveModal({ isOpen, submit, onContinue }) {
  return (
    <Modal isOpen={isOpen} ariaLabel="Time record saved">
      <Modal.Title>
        {submit
          ? "Your time record has been submitted."
          : "Your time record has been saved."}
      </Modal.Title>
      <Modal.Body>
        <p className="text-center font-semibold">
          What would you like to do next?
        </p>
      </Modal.Body>
      <Modal.Buttons>
        <Link to="/logout">
          <Button variant="destructive">Log out of ESS</Button>
        </Link>
        <Button variant="theme" onPress={onContinue}>
          Go back to ESS
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}

/**
 * Shown when a save is rejected because the record changed elsewhere, i.e. a supervisor or
 * personnel acted on it while it was open for entry.
 */
export function RecordModifiedModal({ isOpen, onRefresh, helplineNumber }) {
  return (
    <Modal isOpen={isOpen} ariaLabel="External modification detected">
      <Modal.Title>
        An external modification was detected for the submitted record
      </Modal.Title>
      <Modal.Body>
        <p className="max-w-lg text-center">
          Please hit the 'Refresh Records' button to get the latest version of
          the record
          <br />
          and resubmit if applicable.
          <br />
          If this continues to occur, please contact the STS Helpline
          {helplineNumber ? ` at ${helplineNumber}` : ""}.
        </p>
      </Modal.Body>
      <Modal.Buttons>
        <Button variant="theme" onPress={onRefresh}>
          Refresh Records
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
