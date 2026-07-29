import React, { useMemo, useState } from "react";
import { format, parseISO } from "date-fns";
import { Printer } from "lucide-react";
import Button from "app/components/Button";
import Controls from "app/components/Controls";
import Notification from "app/components/Notification";
import LoadingIndicator from "app/components/LoadingIndicator";
import { EssPopover } from "app/components/EssPopover";
import {
  useAllowanceActiveYears,
  usePeriodAllowanceUsage,
} from "app/views/time/useAllowance";
import {
  computeRemaining,
  getAvailableHours,
} from "app/views/time/allowanceUtils";

/**
 * The allowance history for a single temporary employee: a year selector over the years the
 * employee had an allowance, and a per pay period usage table for the selected year.
 * Ported from the legacy allowanceHistory directive
 * (assets/js/src/time/allowance/allowance-history-directive.js).
 */
export default function AllowanceHistorySection({ empId }) {
  const activeYears = useAllowanceActiveYears(empId);

  if (activeYears.isPending) {
    return <LoadingIndicator />;
  }

  if (activeYears.data.length === 0) {
    return (
      <Notification level="info" title="No Allowance History">
        <p>No allowance usage records exist.</p>
      </Notification>
    );
  }

  return <AllowanceHistory empId={empId} activeYears={activeYears.data} />;
}

function AllowanceHistory({ empId, activeYears }) {
  const [year, setYear] = useState(activeYears[0]);
  const periodUsage = usePeriodAllowanceUsage(empId, year);

  // The remaining hours a period leaves are worth the highest temporary rate in effect during
  // that period, so each usage is resolved against its own pay period date range.
  const usages = useMemo(
    () =>
      (periodUsage.data || []).map((usage) =>
        computeRemaining(usage, {
          beginDate: usage.payPeriod.startDate,
          endDate: usage.payPeriod.endDate,
        }),
      ),
    [periodUsage.data],
  );

  return (
    <div>
      <Controls>
        <label className="font-semibold text-teal-700" htmlFor="allowance-year">
          Filter By Year&nbsp;
        </label>
        <select
          id="allowance-year"
          name="allowance-year"
          className="select"
          value={year}
          onChange={(e) => setYear(parseInt(e.target.value))}
        >
          {activeYears.map((activeYear) => (
            <option value={activeYear} key={activeYear}>
              {activeYear}
            </option>
          ))}
        </select>
      </Controls>

      {periodUsage.isPending ? (
        <LoadingIndicator variant="sm" />
      ) : usages.length === 0 ? (
        <p className="bg-white p-3 text-center">
          No allowance usage records exist for this year. If it is early in the
          year they may not have been created yet.
        </p>
      ) : (
        <div className="bg-white">
          <p className="p-3 text-center">
            Summary of past allowance usage for each pay period. Use the print
            icon to open a printable summary of a period&apos;s allowance usage.
          </p>
          <AllowanceHistoryTable usages={usages} />
        </div>
      )}
    </div>
  );
}

const HEAD_CELL = "table__head__cell";
const HEAD_CELL_NUMBER = "table__head__cell cell--number";
const CELL = "table__cell";
const CELL_NUMBER = "table__cell cell--number";

function AllowanceHistoryTable({ usages }) {
  return (
    <div className="overflow-x-auto py-3">
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className={HEAD_CELL}>Period #</th>
            <th className={HEAD_CELL}>End Date</th>
            <th className={HEAD_CELL_NUMBER}>Used</th>
            <th className={HEAD_CELL_NUMBER}>Used YTD</th>
            <th className={HEAD_CELL_NUMBER}>Total Allowed</th>
            <th className={HEAD_CELL_NUMBER}>Est Available</th>
            <th className={HEAD_CELL}>
              <span className="sr-only">Print</span>
            </th>
          </tr>
        </thead>
        <tbody className="table__body table__body--striped">
          {usages.map((usage) => (
            <tr className="table__row" key={usage.payPeriod.endDate}>
              <td className={CELL}>{usage.payPeriod.payPeriodNum}</td>
              <td className={`${CELL} whitespace-nowrap`}>
                {formatDate(usage.payPeriod.endDate)}
              </td>
              <td className={CELL_NUMBER}>{round(usage.periodHoursUsed)}</td>
              <td className={CELL_NUMBER}>
                {round(usage.hoursUsed + usage.periodHoursUsed)}
              </td>
              <td className={CELL_NUMBER}>{round(usage.totalHours)}</td>
              <td className={CELL_NUMBER}>
                {round(getAvailableHours(usage, usage.periodHoursUsed))}
              </td>
              <td className={`${CELL} text-center`}>
                <PrintPopover usage={usage} />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

/**
 * The row's print action: a popover offering a printable PDF report of the period's allowance
 * usage, opened from the legacy Oracle Reports server. Ported from the print-allowance-usage
 * popover in WEB-INF/view/template/time/allowance/history-directive.jsp.
 */
function PrintPopover({ usage }) {
  const [isOpen, setIsOpen] = useState(false);

  const handlePrint = () => {
    window.open(printReportUrl(usage), "_blank", "noopener");
    setIsOpen(false);
  };

  return (
    <EssPopover
      isOpen={isOpen}
      onOpenChange={setIsOpen}
      placement="top"
      showArrow
      contentClassName="p-3 text-center"
      trigger={
        <Button
          variant="quiet"
          aria-label={`Print allowance usage for period ${usage.payPeriod.payPeriodNum}`}
          aria-haspopup="dialog"
          className="p-1 text-teal-700 hover:bg-teal-50"
        >
          <Printer className="h-4 w-4" />
        </Button>
      }
    >
      <h4 className="mb-2 font-semibold">Open printable report for period</h4>
      <Button variant="secondary" onPress={handlePrint}>
        Print
      </Button>
    </EssPopover>
  );
}

/**
 * Builds the Oracle Reports URL for a period's allowance usage report, matching the legacy
 * printSelectedPerUsage: the period end date as DD-MMM-YY and the employee id.
 */
function printReportUrl(usage) {
  const params = new URLSearchParams({
    cmdkey: "tsuser",
    report: "PRBHRS23",
    destype: "CACHE",
    desformat: "PDF",
    blankpages: "no",
    P_DTEND: format(parseISO(usage.payPeriod.endDate), "dd-MMM-yy"),
    P_NUXREFEM: usage.empId,
  });
  return `http://nysasprd.senate.state.ny.us:7778/reports/rwservlet?${params}`;
}

function formatDate(isoDate) {
  return isoDate ? format(parseISO(isoDate), "MM/dd/yyyy") : "";
}

/** Drops the floating point noise that hour arithmetic leaves behind, like Angular's number filter. */
function round(value) {
  return Math.round((value || 0) * 100) / 100;
}
