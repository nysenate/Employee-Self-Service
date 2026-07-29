import React, { useMemo, useState } from "react";
import { format, parseISO } from "date-fns";
import Modal from "app/components/Modal";
import Button from "app/components/Button";
import LoadingIndicator from "app/components/LoadingIndicator";
import { cn } from "app/utils/cn";
import { useAccruals } from "app/views/time/useAccrual";
import {
  accrualsUsed,
  displayHours,
  hours,
} from "app/views/time/accrual/timeoff/timeOffRequestUtils";

/**
 * The supervisor's review dialog: the selected requests down the left, the highlighted one's
 * days and comment thread on the right, and buttons to approve or disapprove each.
 *
 * Ported from the legacy timeOffRequestReviewModal directive
 * (assets/js/src/time/accrual/time-off-request-review-modals.js).
 *
 * @param requests The requests under review, or null when the dialog is closed.
 * @param alreadyApproved True when reviewing requests that are already approved, which start
 *                        out in the approved column so only a change has to be made.
 * @param onSubmit Called with an array of { requestId, action, comment } once acknowledged.
 * @param onClose Called when the dialog is dismissed without submitting.
 */
export default function TimeOffRequestReviewModal({
  requests,
  alreadyApproved,
  onSubmit,
  onClose,
}) {
  const [iSelected, setISelected] = useState(0);
  const [decisions, setDecisions] = useState({});
  const [comments, setComments] = useState({});
  const [confirming, setConfirming] = useState(false);
  const [discarding, setDiscarding] = useState(false);

  if (!requests) {
    return null;
  }

  const selectedRequest = requests[iSelected];
  const statusOf = (request) =>
    decisions[request.requestId] ?? (alreadyApproved ? "approved" : undefined);

  const isEmpty = Object.keys(decisions).length === 0 && !alreadyApproved;

  const decide = (request, decision) =>
    setDecisions((current) => {
      const next = { ...current };
      if (decision) {
        next[request.requestId] = decision;
      } else if (alreadyApproved) {
        // Undoing on an already approved request means leaving it as it was.
        next[request.requestId] = "approved";
      } else {
        delete next[request.requestId];
      }
      return next;
    });

  /** Moves to the next request still needing a decision, wrapping around. */
  const selectNextPending = () => {
    for (let i = 0; i < requests.length; i++) {
      const index = (i + iSelected) % requests.length;
      if (!decisions[requests[index].requestId]) {
        setISelected(index);
        return;
      }
    }
  };

  const decided = (decision) =>
    requests.filter((request) => statusOf(request) === decision);

  const handleConfirm = () => {
    setConfirming(false);
    onSubmit([
      ...decided("approved").map((request) => ({
        requestId: request.requestId,
        action: "APPROVE",
        comment: comments[request.requestId],
      })),
      ...decided("disapproved").map((request) => ({
        requestId: request.requestId,
        action: "DISAPPROVE",
        comment: comments[request.requestId],
      })),
    ]);
  };

  const handleClose = () => (isEmpty ? onClose() : setDiscarding(true));

  return (
    <>
      <Modal
        isOpen={!!requests}
        onOpenChange={handleClose}
        className="max-w-[95vw]"
      >
        <Modal.Title>Review and Approve Time Off Requests</Modal.Title>
        <Modal.Body>
          <p className="mb-3 text-center">
            Click a time-off request from the Time-Off Request List on the left
            hand side to review the request. You can then either Approve or
            Disapprove the request.
          </p>

          <div className="flex flex-col gap-4 lg:flex-row">
            <RequestSelectionTable
              requests={requests}
              iSelected={iSelected}
              onSelect={setISelected}
              statusOf={statusOf}
            />

            <div className="min-w-0 grow">
              <h3 className="mb-2 text-lg font-semibold">
                Time-Off Request for <strong>{selectedRequest?.name}</strong>,
                submitted on {selectedRequest?.timestampPrint}
              </h3>
              <RequestDetails request={selectedRequest} />
              <CommentThread
                request={selectedRequest}
                comment={comments[selectedRequest?.requestId] || ""}
                onCommentChange={(text) =>
                  setComments((current) => ({
                    ...current,
                    [selectedRequest.requestId]: text,
                  }))
                }
              />
            </div>
          </div>
        </Modal.Body>

        <Modal.Buttons>
          {statusOf(selectedRequest) ? (
            <Button
              variant="secondary"
              onPress={() => decide(selectedRequest, null)}
            >
              {statusOf(selectedRequest) === "approved"
                ? "Undo Approval"
                : "Undo Disapproval"}
            </Button>
          ) : null}
          {statusOf(selectedRequest) !== "approved" && (
            <Button
              onPress={() => {
                decide(selectedRequest, "approved");
                selectNextPending();
              }}
            >
              Approve Request
            </Button>
          )}
          {statusOf(selectedRequest) !== "disapproved" && (
            <Button
              variant="destructive"
              onPress={() => {
                decide(selectedRequest, "disapproved");
                selectNextPending();
              }}
            >
              Disapprove Request
            </Button>
          )}
          <Button isDisabled={isEmpty} onPress={() => setConfirming(true)}>
            Submit Changes
          </Button>
          <Button variant="secondary" onPress={handleClose}>
            Cancel
          </Button>
        </Modal.Buttons>
      </Modal>

      <ApproveSubmitModal
        isOpen={confirming}
        approved={decided("approved")}
        disapproved={decided("disapproved")}
        onConfirm={handleConfirm}
        onClose={() => setConfirming(false)}
      />

      <Modal
        isOpen={discarding}
        onOpenChange={() => setDiscarding(false)}
        ariaLabel="Unsubmitted Requests"
      >
        <Modal.Title>Unsubmitted Requests</Modal.Title>
        <Modal.Body>
          There are one or more reviewed requests that have not been submitted.
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

function RequestSelectionTable({ requests, iSelected, onSelect, statusOf }) {
  return (
    <div className="max-h-[60vh] shrink-0 overflow-y-auto lg:w-72">
      <h3 className="bg-teal-700 px-3 py-1 font-semibold text-white">
        Employee Time-Off Request List
      </h3>
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell">Employee</th>
            <th className="table__head__cell">Action</th>
          </tr>
        </thead>
        <tbody className="table__body">
          {requests.map((request, index) => (
            <tr
              key={request.requestId}
              onClick={() => onSelect(index)}
              title="Select Time-Off Request for Review"
              className={cn(
                "table__row cursor-pointer",
                index === iSelected && "bg-teal-50 font-semibold",
                statusOf(request) === "approved" && "text-green-700",
                statusOf(request) === "disapproved" && "text-[#B90504]",
              )}
            >
              <td className="table__cell">{request.name}</td>
              <td className="table__cell">
                {statusOf(request) === "approved"
                  ? "Approve"
                  : statusOf(request) === "disapproved"
                    ? "Disapprove"
                    : "--"}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

/** The requested days, with the employee's accruals before and after the request. */
function RequestDetails({ request }) {
  const today = format(new Date(), "yyyy-MM-dd");
  /*
   * A supervisor may not be permitted to read every employee's accruals. The legacy modal
   * showed zeroes rather than failing, so the balances are best effort here too.
   */
  const accrualQuery = useAccruals(request?.employeeId, today, false);

  const accruals = useMemo(() => {
    const data = accrualQuery.data;
    return {
      vacation: hours(data?.vacationAvailable),
      personal: hours(data?.personalAvailable),
      sick: hours(data?.sickAvailable),
    };
  }, [accrualQuery.data]);

  if (!request) {
    return null;
  }

  const used = accrualsUsed(request.days);

  return (
    <div>
      {accrualQuery.isPending ? (
        <LoadingIndicator variant="sm" />
      ) : (
        <AccrualLine label="Available Hours" accruals={accruals} />
      )}

      <div className="overflow-x-auto py-2">
        <table className="table">
          <thead>
            <tr className="table__head__row">
              <th className="table__head__cell">Date</th>
              <th className="table__head__cell cell--number">Work</th>
              <th className="table__head__cell cell--number">Holiday</th>
              <th className="table__head__cell cell--number">Vacation</th>
              <th className="table__head__cell cell--number">Personal</th>
              <th className="table__head__cell cell--number">Sick Emp</th>
              <th className="table__head__cell cell--number">Sick Fam</th>
              <th className="table__head__cell cell--number">Misc</th>
              <th className="table__head__cell">Misc Leave Type</th>
              {/*
                The legacy modal stopped at the first misc slot, so a day using a second misc
                leave type showed hours in its Total that appeared nowhere in the row.
              */}
              <th className="table__head__cell cell--number">Misc 2</th>
              <th className="table__head__cell">Misc 2 Type</th>
              <th className="table__head__cell cell--number">Total</th>
            </tr>
          </thead>
          <tbody className="table__body table__body--striped">
            {request.days.map((day) => (
              <tr className="table__row" key={day.date}>
                <td className="table__cell whitespace-nowrap">
                  {day.datePrint}
                </td>
                <td className="table__cell cell--number">
                  {displayHours(day.workHours)}
                </td>
                <td className="table__cell cell--number">
                  {displayHours(day.holidayHours)}
                </td>
                <td className="table__cell cell--number">
                  {displayHours(day.vacationHours)}
                </td>
                <td className="table__cell cell--number">
                  {displayHours(day.personalHours)}
                </td>
                <td className="table__cell cell--number">
                  {displayHours(day.sickEmpHours)}
                </td>
                <td className="table__cell cell--number">
                  {displayHours(day.sickFamHours)}
                </td>
                <td className="table__cell cell--number">
                  {displayHours(day.miscHours)}
                </td>
                <td className="table__cell">{day.miscType || "--"}</td>
                <td className="table__cell cell--number">
                  {displayHours(day.misc2Hours)}
                </td>
                <td className="table__cell">{day.miscType2 || "--"}</td>
                <td className="table__cell cell--number">{day.totalHours}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <AccrualLine
        label="Hours After Request"
        accruals={{
          vacation: accruals.vacation - used.vacation,
          personal: accruals.personal - used.personal,
          sick: accruals.sick - used.sick,
        }}
      />
    </div>
  );
}

function AccrualLine({ label, accruals }) {
  return (
    <p className="my-1">
      <span className="font-semibold">{label}:</span>{" "}
      <span className="text-teal-700">Vacation: {accruals.vacation}</span>{" "}
      <span className="text-[#5c7474]">Personal: {accruals.personal}</span>{" "}
      <span className="text-orange-600">Sick: {accruals.sick}</span>
    </p>
  );
}

function CommentThread({ request, comment, onCommentChange }) {
  if (!request) {
    return null;
  }

  return (
    <div className="mt-2">
      <h3 className="font-semibold text-teal-700">Comments:</h3>
      {(request.comments || []).map((existing, index) => (
        <p key={index} className="my-1">
          <strong>
            {existing.authorId === request.employeeId ? request.name : "Me"}
            :&nbsp;
          </strong>
          {existing.text}
        </p>
      ))}
      <p className="mt-2">
        <strong>Me:</strong>
      </p>
      <textarea
        className="input h-20 w-full max-w-2xl"
        value={comment}
        aria-label="Add a comment"
        onChange={(e) => onCommentChange(e.target.value)}
      />
    </div>
  );
}

/**
 * The acknowledgement a supervisor signs off on before their decisions are sent.
 * Ported from WEB-INF/view/template/time/accrual/time-off-request-approve-submit-modal.jsp.
 */
function ApproveSubmitModal({
  isOpen,
  approved,
  disapproved,
  onConfirm,
  onClose,
}) {
  return (
    <Modal
      isOpen={isOpen}
      onOpenChange={onClose}
      className="max-w-2xl"
      ariaLabel="Acknowledge time off request submission"
    >
      <Modal.Title>
        Before submitting, you must acknowledge the following
      </Modal.Title>
      <Modal.Body>
        <p className="mb-3">
          For purposes of submitting a timesheet, the employee should ensure
          that his or her timesheet reflects what is contained in this request.
        </p>

        {approved.length > 0 && (
          <>
            <p>
              You are <strong className="text-green-700">approving</strong> the
              following {approved.length} time-off request(s):
            </p>
            <RequestList requests={approved} />
            <p className="mb-3">
              To the best of my knowledge, the above listed employees are using
              accrued time from previous service in past reported period(s), and
              all supplied information is correct.
            </p>
          </>
        )}

        {disapproved.length > 0 && (
          <>
            <p>
              You are <strong className="text-[#B90504]">disapproving</strong>{" "}
              the following {disapproved.length} time-off request(s):
            </p>
            <RequestList requests={disapproved} />
          </>
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

function RequestList({ requests }) {
  return (
    <ul className="my-2 list-disc pl-6">
      {requests.map((request) => (
        <li key={request.requestId}>
          {request.name} ({shortDate(request.startDate)} -{" "}
          {shortDate(request.endDate)})
        </li>
      ))}
    </ul>
  );
}

function shortDate(isoDate) {
  return isoDate ? format(parseISO(isoDate), "MM/dd") : "";
}
