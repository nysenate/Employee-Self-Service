import React, { useState } from "react";
import { format, parseISO } from "date-fns";
import Modal from "app/components/Modal";
import Button from "app/components/Button";
import { cn } from "app/utils/cn";
import { RecordDetails } from "app/views/time/attendance/history/RecordDetailsModal";

/**
 * The review dialog: the selected records down the left, the highlighted record's details on
 * the right, and, when the user may act on them, buttons to approve or disapprove each one.
 *
 * Nothing is sent until Submit Changes is pressed and the acknowledgement below is agreed to.
 *
 * Ported from the legacy recordReviewModal directive
 * (assets/js/src/time/record/record-review-modals.js).
 *
 * @param records The records under review, or null when the dialog is closed.
 * @param allowApproval Whether the records may be approved and disapproved, or only viewed.
 * @param onSubmit Called with { approved, disapproved } arrays once the user acknowledges.
 * @param onClose Called when the dialog is dismissed without submitting.
 */
export default function RecordReviewModal({
  records,
  allowApproval,
  onSubmit,
  onClose,
}) {
  const [iSelected, setISelected] = useState(0);
  /** Reviewed records by time record id: approved carry no remarks, disapproved must. */
  const [reviews, setReviews] = useState({});
  const [rejecting, setRejecting] = useState(null);
  const [confirming, setConfirming] = useState(false);
  const [discarding, setDiscarding] = useState(false);

  if (!records) {
    return null;
  }

  const selectedRecord = records[iSelected];
  const statusOf = (record) => reviews[record.timeRecordId]?.action;
  const isEmpty = Object.keys(reviews).length === 0;

  const setReview = (record, review) => {
    setReviews((current) => {
      const next = { ...current };
      if (review) {
        next[record.timeRecordId] = review;
      } else {
        delete next[record.timeRecordId];
      }
      return next;
    });
  };

  /**
   * After acting on a record, move to the next one still needing a decision, wrapping around.
   * Matches the legacy selectNextPendingRecord.
   */
  const selectNextPending = () => {
    for (let i = 0; i < records.length; i++) {
      const index = (i + iSelected) % records.length;
      if (!statusOf(records[index])) {
        setISelected(index);
        return;
      }
    }
  };

  const handleApprove = () => {
    setReview(selectedRecord, { action: "approved", record: selectedRecord });
    selectNextPending();
  };

  const handleReject = (remarks) => {
    setReview(rejecting, { action: "disapproved", record: rejecting, remarks });
    setRejecting(null);
    selectNextPending();
  };

  const handleConfirm = () => {
    const reviewed = Object.values(reviews);
    setConfirming(false);
    onSubmit({
      approved: reviewed
        .filter((review) => review.action === "approved")
        .map((review) => review.record),
      disapproved: reviewed
        .filter((review) => review.action === "disapproved")
        .map((review) => ({ ...review.record, remarks: review.remarks })),
    });
  };

  // Closing with decisions still unsent would throw them away, so confirm first.
  const handleClose = () => {
    if (isEmpty) {
      onClose();
    } else {
      setDiscarding(true);
    }
  };

  return (
    <>
      <Modal
        isOpen={!!records}
        onOpenChange={handleClose}
        className="max-w-[95vw]"
      >
        <Modal.Title>Review and Approve Records</Modal.Title>
        <Modal.Body>
          <p className="mb-3 text-center">
            Click a record from the Employee Record List on the left hand side
            to review the time record. You can then either Approve or Disapprove
            the record.
          </p>
          <div className="flex flex-col gap-4 lg:flex-row">
            <RecordSelectionTable
              records={records}
              iSelected={iSelected}
              onSelect={setISelected}
              statusOf={statusOf}
            />
            <div className="min-w-0 grow">
              <RecordDetails record={selectedRecord} />
            </div>
          </div>
        </Modal.Body>

        <Modal.Buttons>
          {allowApproval ? (
            <>
              {statusOf(selectedRecord) ? (
                <Button
                  variant="secondary"
                  onPress={() => setReview(selectedRecord, null)}
                >
                  {statusOf(selectedRecord) === "approved"
                    ? "Undo Approval"
                    : "Undo Disapproval of Record"}
                </Button>
              ) : (
                <>
                  <Button onPress={handleApprove}>Approve Record</Button>
                  <Button
                    variant="destructive"
                    onPress={() => setRejecting(selectedRecord)}
                  >
                    Disapprove Record
                  </Button>
                </>
              )}
              <Button isDisabled={isEmpty} onPress={() => setConfirming(true)}>
                Submit Changes
              </Button>
              <Button variant="secondary" onPress={handleClose}>
                Cancel
              </Button>
            </>
          ) : (
            <Button variant="secondary" onPress={onClose}>
              Exit
            </Button>
          )}
        </Modal.Buttons>
      </Modal>

      <RejectRemarksModal
        record={rejecting}
        onSubmit={handleReject}
        onClose={() => setRejecting(null)}
      />

      <ApproveSubmitModal
        isOpen={confirming}
        approved={Object.values(reviews)
          .filter((review) => review.action === "approved")
          .map((review) => review.record)}
        disapproved={Object.values(reviews)
          .filter((review) => review.action === "disapproved")
          .map((review) => review.record)}
        onConfirm={handleConfirm}
        onClose={() => setConfirming(false)}
      />

      <Modal
        isOpen={discarding}
        onOpenChange={() => setDiscarding(false)}
        ariaLabel="Unsubmitted Records"
      >
        <Modal.Title>Unsubmitted Records</Modal.Title>
        <Modal.Body>
          There are one or more reviewed records that have not been submitted.
          Discard changes?
        </Modal.Body>
        <Modal.Buttons>
          <Button
            variant="destructive"
            onPress={() => {
              setDiscarding(false);
              onClose();
            }}
          >
            Discard Changes
          </Button>
          <Button onPress={() => setDiscarding(false)}>Resume Review</Button>
        </Modal.Buttons>
      </Modal>
    </>
  );
}

function RecordSelectionTable({ records, iSelected, onSelect, statusOf }) {
  return (
    <div className="max-h-[60vh] shrink-0 overflow-y-auto lg:w-80">
      <h3 className="bg-teal-700 px-3 py-1 font-semibold text-white">
        Employee Record List
      </h3>
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell">Employee</th>
            <th className="table__head__cell">Pay Period</th>
            <th className="table__head__cell">Action</th>
          </tr>
        </thead>
        <tbody className="table__body">
          {records.map((record, index) => (
            <tr
              key={record.timeRecordId}
              onClick={() => onSelect(index)}
              title="Select Record for Review"
              className={cn(
                "table__row cursor-pointer",
                index === iSelected && "bg-teal-50 font-semibold",
                statusOf(record) === "approved" && "text-green-700",
                statusOf(record) === "disapproved" && "text-[#B90504]",
              )}
            >
              <td className="table__cell">{record.employee?.lastName}</td>
              <td className="table__cell whitespace-nowrap">
                {formatShortDate(record.beginDate)} -{" "}
                {formatShortDate(record.endDate)}
              </td>
              <td className="table__cell">{actionLabel(statusOf(record))}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function actionLabel(status) {
  if (status === "approved") {
    return "Approve";
  }
  if (status === "disapproved") {
    return "Disapprove";
  }
  return "--";
}

/** Collects the reason a record is being sent back. A reason is required. */
function RejectRemarksModal({ record, onSubmit, onClose }) {
  const [remarks, setRemarks] = useState("");
  const [showError, setShowError] = useState(false);

  if (!record) {
    return null;
  }

  const handleSubmit = () => {
    if (remarks.trim()) {
      onSubmit(remarks);
      setRemarks("");
      setShowError(false);
    } else {
      setShowError(true);
    }
  };

  return (
    <Modal isOpen={!!record} onOpenChange={onClose}>
      <Modal.Title>
        Explain the reason for disapproving the time record.
      </Modal.Title>
      <Modal.Body>
        {showError && (
          <p className="mb-2 font-semibold text-[#e64727]">
            You must provide a reason for disapproval
          </p>
        )}
        <textarea
          className="input h-32 w-full"
          placeholder="Reason for disapproval"
          maxLength={150}
          autoFocus
          value={remarks}
          onChange={(e) => setRemarks(e.target.value)}
        />
      </Modal.Body>
      <Modal.Buttons>
        <Button variant="secondary" onPress={onClose}>
          Cancel
        </Button>
        <Button variant="destructive" onPress={handleSubmit}>
          Disapprove Record
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}

/**
 * The acknowledgement a supervisor signs off on before their decisions are sent.
 * Ported from WEB-INF/view/template/time/record/record-approve-submit-modal.jsp.
 */
export function ApproveSubmitModal({
  isOpen,
  approved = [],
  disapproved = [],
  onConfirm,
  onClose,
}) {
  return (
    <Modal
      isOpen={isOpen}
      onOpenChange={onClose}
      className="max-w-2xl"
      ariaLabel="Acknowledge record submission"
    >
      <Modal.Title>
        Before submitting, you must acknowledge the following
      </Modal.Title>
      <Modal.Body>
        <p className="mb-3">
          For purposes of submitting a timesheet, the username and password is
          the electronic signature of the employee. As liability attaches to
          each timesheet, the employee should ensure that his or her username or
          password is securely kept and used.
        </p>

        {approved.length > 0 && (
          <div className="mb-3">
            <p>
              You are <strong className="text-green-700">approving</strong> the
              following {approved.length} record(s):
            </p>
            <RecordList records={approved} />
          </div>
        )}

        {approved.length > 0 && (
          <p className="mb-3">
            To the best of my knowledge, the above listed employees were
            employed by the office and have performed the proper duties assigned
            to them during the reported period(s), and all supplied information
            is correct. This Time and Attendance Record will be sent to the
            Personnel Office electronically.
          </p>
        )}

        {disapproved.length > 0 && (
          <div className="mb-3">
            <p>
              You are <strong className="text-[#B90504]">disapproving</strong>{" "}
              the following {disapproved.length} record(s):
            </p>
            <RecordList records={disapproved} />
          </div>
        )}
      </Modal.Body>
      <Modal.Buttons>
        <Button onPress={onConfirm}>I agree</Button>
        <Button variant="destructive" onPress={onClose}>
          Cancel
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}

function RecordList({ records }) {
  return (
    <ul className="my-2 list-disc pl-6">
      {records.map((record) => (
        <li key={record.timeRecordId}>
          {record.employee?.fullName} ({formatShortDate(record.beginDate)} -{" "}
          {formatShortDate(record.endDate)})
        </li>
      ))}
    </ul>
  );
}

/** "MM/DD", as the legacy record listings showed a pay period. */
export function formatShortDate(isoDate) {
  return isoDate ? format(parseISO(isoDate), "MM/dd") : "";
}
