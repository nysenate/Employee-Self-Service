import React from "react";
import LoadingIndicator from "app/components/LoadingIndicator";
import {
  HourSquare,
  HourSquareColumn,
  HoursDiff,
} from "app/views/time/attendance/record-entry/HourSquare";

/**
 * Displays the accruals available to an employee for the selected record.
 * Ported from the legacy accrual bar directive (WEB-INF/view/template/time/accrual/accrual-bar.jsp).
 */
export default function AccrualBar({ accruals, isLoading }) {
  if (isLoading) {
    return (
      <div className="py-2">
        <h3 className="text-center">Loading Accruals...</h3>
        <LoadingIndicator variant="sm" />
      </div>
    );
  }

  return (
    <div className="flex flex-wrap items-start justify-center pt-2 text-center">
      <HourSquare
        caption="Personal Hours"
        captionClassName="bg-teal-500"
        className="w-[150px] shrink-0"
      >
        <div className="text-lg">{accruals?.personalAvailable}</div>
      </HourSquare>
      <HourSquare
        caption="Vacation Hours"
        captionClassName="bg-green-600"
        className="w-[150px] shrink-0"
      >
        <div className="text-lg">{accruals?.vacationAvailable}</div>
      </HourSquare>
      <HourSquare
        caption="Sick Hours"
        captionClassName="bg-orange-600"
        className="w-[150px] shrink-0"
      >
        <div className="text-lg">{accruals?.sickAvailable}</div>
      </HourSquare>
      {/*
       * Laid out with a zero basis so the year to date box never forces a wrap of its own:
       * it stays on the row with the three squares and grows into whatever is left, up to the
       * width it has in the legacy bar.
       */}
      <HourSquare
        caption="Year To Date Hours Of Service"
        className="max-w-[390px] min-w-[280px] grow basis-0"
      >
        <div className="flex">
          <HourSquareColumn>
            Expected: {accruals?.serviceYtdExpected}
          </HourSquareColumn>
          <HourSquareColumn>Actual: {accruals?.serviceYtd}</HourSquareColumn>
          <HourSquareColumn>
            Difference:{" "}
            <HoursDiff
              hours={accruals?.serviceYtd - accruals?.serviceYtdExpected}
            />
          </HourSquareColumn>
        </div>
      </HourSquare>
    </div>
  );
}
