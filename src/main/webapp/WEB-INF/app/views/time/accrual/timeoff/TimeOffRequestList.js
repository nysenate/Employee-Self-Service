import React from "react";
import { useNavigate } from "react-router-dom";
import { cn } from "app/utils/cn";
import { useMiscLeaveTypes } from "app/views/time/attendance/useTimeRecords";
import {
  getAccrualTypeLabel,
  getRequestStatusLabel,
} from "app/views/time/accrual/timeoff/timeOffRequestUtils";

/**
 * Lists time off requests in a table, each row opening that request.
 * Ported from the legacy timeOffRequestList directive
 * (assets/js/src/time/accrual/time-off-request-list-directive.js).
 *
 * @param requests The requests to list.
 * @param emptyMessage Shown in place of the table when there are none.
 */
export default function TimeOffRequestList({
  requests,
  emptyMessage = "No Time Off Requests",
}) {
  const navigate = useNavigate();

  if (requests.length === 0) {
    return <p className="p-3 text-center font-semibold">{emptyMessage}</p>;
  }

  return (
    <div className="overflow-x-auto py-3">
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell">Date Range</th>
            <th className="table__head__cell">Status</th>
            <th className="table__head__cell cell--number">Total Hours</th>
            <th className="table__head__cell">Leave Type(s)</th>
          </tr>
        </thead>
        <tbody className="table__body table__body--striped">
          {requests.map((request) => (
            <tr
              className="table__row cursor-pointer"
              key={request.requestId}
              onClick={() =>
                navigate(`/time/accrual/time-off-request/${request.requestId}`)
              }
            >
              <td className="table__cell whitespace-nowrap">
                {request.startDatePrint} - {request.endDatePrint}
              </td>
              <td className="table__cell">
                {getRequestStatusLabel(request.status)}
              </td>
              <td className="table__cell cell--number">{request.totalHours}</td>
              <td className="table__cell">
                <LeaveTypeChips request={request} />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

/** The colored pills naming the kinds of leave a request draws on. */
export function LeaveTypeChips({ request }) {
  const miscLeaveTypes = useMiscLeaveTypes();

  return (
    <div className="flex flex-wrap gap-1">
      {request.accrualTypes.map((type) => (
        <Chip key={type} type={type}>
          {getAccrualTypeLabel(type)}
        </Chip>
      ))}
      {request.miscTypes.map((type) => (
        <Chip key={type} type="MISC">
          {miscLeaveTypes.data?.[type]?.shortName || type}
        </Chip>
      ))}
    </div>
  );
}

/*
 * The legacy chips were colored per leave type by the .md-chip modifier classes in
 * assets/css/src/time.less.
 */
const CHIP_STYLES = {
  VACATION: "bg-teal-700 text-white",
  PERSONAL: "bg-[#5c7474] text-white",
  SICKEMP: "bg-orange-600 text-white",
  SICKFAM: "bg-orange-600 text-white",
  MISC: "bg-gray-500 text-white",
};

function Chip({ type, children }) {
  return (
    <span
      className={cn(
        "inline-block rounded-full px-3 py-0.5 text-sm whitespace-nowrap",
        CHIP_STYLES[type] || CHIP_STYLES.MISC,
      )}
    >
      {children}
    </span>
  );
}
