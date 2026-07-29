import React from "react";
import Modal from "app/components/Modal";
import Button from "app/components/Button";
import LoadingIndicator from "app/components/LoadingIndicator";
import { formatShortDate } from "app/views/time/attendance/manage/RecordReviewModal";

/**
 * Confirms which employees are about to be emailed about which records.
 * Ported from WEB-INF/view/template/time/record/record-reminder-modal.jsp.
 *
 * @param records The records the reminders cover, or null when the dialog is closed.
 */
export function RecordReminderPromptModal({ records, onConfirm, onClose }) {
  if (!records) {
    return null;
  }

  return (
    <Modal isOpen={!!records} onOpenChange={onClose}>
      <Modal.Title>Send Email Reminders</Modal.Title>
      <Modal.Body>
        <p className="mb-3 text-center">
          The following employees will have an email reminder sent for the
          listed records.
        </p>
        <ReminderRecordTable records={records} />
      </Modal.Body>
      <Modal.Buttons>
        <Button onPress={onConfirm}>Send</Button>
        <Button variant="destructive" onPress={onClose}>
          Cancel
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}

/** Shown while the reminders are being sent. */
export function RecordReminderSendingModal({ isOpen }) {
  return (
    <Modal isOpen={isOpen} ariaLabel="Sending Email Reminders">
      <Modal.Title>Sending Email Reminders</Modal.Title>
      <Modal.Body>
        <LoadingIndicator />
      </Modal.Body>
    </Modal>
  );
}

/**
 * Reports whether each employee's reminder went out.
 * Ported from WEB-INF/view/template/time/record/record-reminder-posted-modal.jsp.
 *
 * @param reminders One result per employee, each with its records and whether it was sent.
 */
export function RecordReminderPostedModal({ reminders, onClose }) {
  if (!reminders) {
    return null;
  }

  const records = reminders.flatMap((reminder) =>
    reminder.timeRecords.map((record) => ({
      ...record,
      wasReminderSent: reminder.wasReminderSent,
    })),
  );

  return (
    <Modal isOpen={!!reminders} onOpenChange={onClose}>
      <Modal.Title>Email Reminder Results</Modal.Title>
      <Modal.Body>
        <p className="mb-3 text-center">Email reminder results:</p>
        <ReminderRecordTable records={records} showResult />
      </Modal.Body>
      <Modal.Buttons>
        <Button onPress={onClose}>Ok</Button>
      </Modal.Buttons>
    </Modal>
  );
}

/** Records grouped under each employee, whose name is shown once above their first record. */
function ReminderRecordTable({ records, showResult }) {
  const sorted = [...records].sort(
    (a, b) =>
      (a.employee?.fullName || "").localeCompare(b.employee?.fullName || "") ||
      a.beginDate.localeCompare(b.beginDate),
  );

  return (
    <table className="mx-auto">
      <tbody>
        {sorted.map((record, index) => {
          const showName =
            index === 0 || sorted[index - 1].employeeId !== record.employeeId;
          return (
            <tr
              key={record.timeRecordId}
              className={showName ? "border-t border-gray-300" : ""}
            >
              <td className="py-1 pr-3 whitespace-nowrap">
                {showName ? record.employee?.fullName : ""}
              </td>
              <td className="py-1 whitespace-nowrap">
                ({formatShortDate(record.beginDate)} -{" "}
                {formatShortDate(record.endDate)})
              </td>
              {showResult && (
                <td
                  className={`py-1 pr-5 pl-2 font-semibold ${
                    record.wasReminderSent ? "text-green-700" : "text-[#e64727]"
                  }`}
                >
                  {record.wasReminderSent ? "Success" : "Failure!"}
                </td>
              )}
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}
