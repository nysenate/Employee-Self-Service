import React from "react";
import { cn } from "app/utils/cn";
import { LeaveTypeChips } from "app/views/time/accrual/timeoff/TimeOffRequestList";

const HEAD_CELL = "table__head__cell";
const CELL = "table__cell";

/**
 * A selectable table of employees' time off requests for a supervisor to review.
 * Ported from the legacy timeOffRequestApproval directive
 * (assets/js/src/time/accrual/time-off-request-approval-directive.js).
 *
 * @param requests The requests to list, each carrying the employee's name.
 * @param format "pending" or "approved", which decides the instructions and empty message.
 * @param selected A Set of selected request ids.
 * @param onSelectedChange Called with the next Set of selected ids.
 */
export default function TimeOffRequestApprovalTable({
  requests,
  format,
  selected,
  onSelectedChange,
}) {
  const pending = format === "pending";

  const toggle = (requestId) => {
    const next = new Set(selected);
    if (next.has(requestId)) {
      next.delete(requestId);
    } else {
      next.add(requestId);
    }
    onSelectedChange(next);
  };

  return (
    <div>
      <p className="p-3 text-center">
        {pending ? (
          <>
            Select pending requests in the table below and click &apos;Review
            Selected&apos;
            <br />
            at the bottom to review the record details and either approve or
            reject them.
          </>
        ) : (
          <>
            Select approved requests in the table below and click &apos;Review
            Selected&apos;
            <br />
            at the bottom to review the record details or change their status.
          </>
        )}
      </p>

      {requests.length === 0 ? (
        <p className="p-3 text-center font-semibold">
          {pending
            ? "No Pending Time Off Requests"
            : "No Approved Time Off Requests"}
        </p>
      ) : (
        <div className="overflow-x-auto py-3">
          <table className="table">
            <thead>
              <tr className="table__head__row">
                <th className={HEAD_CELL}>Employee</th>
                <th className={HEAD_CELL}>Select</th>
                <th className={HEAD_CELL}>Dates Affected</th>
                <th className={`${HEAD_CELL} cell--number`}>Total Hours</th>
                <th className={`${HEAD_CELL} cell--number`}>Leave Hours</th>
                <th className={HEAD_CELL}>
                  <span className="sr-only">Leave Types</span>
                </th>
              </tr>
            </thead>
            <tbody className="table__body table__body--striped">
              {requests.map((request) => (
                <tr
                  key={request.requestId}
                  onClick={() => toggle(request.requestId)}
                  className={cn(
                    "table__row cursor-pointer",
                    selected.has(request.requestId) && "bg-teal-50",
                  )}
                >
                  <td className={`${CELL} whitespace-nowrap`}>
                    {request.name}
                  </td>
                  <td className={`${CELL} text-center`}>
                    <input
                      type="checkbox"
                      checked={selected.has(request.requestId)}
                      onChange={() => toggle(request.requestId)}
                      onClick={(e) => e.stopPropagation()}
                      aria-label={`Select request for ${request.name}`}
                    />
                  </td>
                  <td className={`${CELL} whitespace-nowrap`}>
                    {request.startDatePrint} - {request.endDatePrint}
                  </td>
                  <td className={`${CELL} cell--number`}>
                    {request.totalHours}
                  </td>
                  <td className={`${CELL} cell--number`}>
                    {request.leaveHours}
                  </td>
                  <td className={CELL}>
                    <LeaveTypeChips request={request} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
