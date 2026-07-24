import React from "react";
import LoadingIndicator from "app/components/LoadingIndicator";
import { getAvailableHours } from "app/views/time/allowanceUtils";
import {
  HourSquare,
  HourSquareColumn,
  HoursDiff,
} from "app/views/time/attendance/record-entry/HourSquare";

/**
 * Displays a temporary employee's yearly allowance and how much of it the selected record uses.
 * Ported from the legacy allowance bar directive
 * (WEB-INF/view/template/time/allowance/allowance-bar.jsp).
 *
 * @param allowance The allowance usage for the year, with its remaining hours computed.
 * @param tempWorkHours Temporary work hours on the record being entered.
 * @param isLoading Whether the allowance is still being fetched.
 */
export default function AllowanceBar({ allowance, tempWorkHours, isLoading }) {
  if (isLoading) {
    return (
      <div className="py-2">
        <h3 className="text-center">Loading Allowance...</h3>
        <LoadingIndicator variant="sm" />
      </div>
    );
  }

  const showRecordHours = typeof tempWorkHours === "number";

  return (
    <div className="px-5 text-center">
      <HourSquare caption={`${allowance?.year} Allowance`}>
        <div className="flex">
          <HourSquareColumn caption="Total Allowed Hours">
            {allowance?.totalHours}
          </HourSquareColumn>
          <HourSquareColumn caption="Reported Hours">
            {allowance?.hoursUsed}
          </HourSquareColumn>
          {showRecordHours && (
            <HourSquareColumn caption="Current Record Hours">
              {tempWorkHours}
            </HourSquareColumn>
          )}
          <HourSquareColumn caption="Estimated Available Hours">
            <HoursDiff
              hours={getAvailableHours(
                allowance,
                showRecordHours ? tempWorkHours : 0,
              )}
            />
          </HourSquareColumn>
        </div>
      </HourSquare>
    </div>
  );
}
