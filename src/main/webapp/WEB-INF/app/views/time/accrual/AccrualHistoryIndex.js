import React, { useMemo, useState } from "react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import Notification from "app/components/Notification";
import LoadingIndicator from "app/components/LoadingIndicator";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import {
  useAccrualActiveYears,
  useAccrualHistory,
} from "app/views/time/useAccrual";
import AccrualDetailsModal from "app/views/time/accrual/AccrualDetailsModal";
import AccrualHistoryTable from "app/views/time/accrual/AccrualHistoryTable";

/**
 * Lists an employee's accrual summary for every processed pay period of the selected year.
 * Ported from the legacy accrualHistory directive
 * (assets/js/src/time/accrual/accrual-history-directive.js).
 */
export default function AccrualHistoryIndex() {
  const { data: user } = useRequireAuthedUser();

  return (
    <div>
      <Hero>Accrual History</Hero>
      <AccrualHistorySection empId={user?.employeeId} />
    </div>
  );
}

/**
 * The accrual history for a single employee: a year selector over the years the employee has
 * accrual records for, and the summary table for the selected year. Used both by the My
 * Accruals page and, for an arbitrary employee, by the Employee Search page.
 */
export function AccrualHistorySection({ empId }) {
  const activeYears = useAccrualActiveYears(empId);

  if (activeYears.isPending) {
    return <LoadingIndicator />;
  }

  if (activeYears.data.length === 0) {
    return (
      <Notification level="info" title="No Accrual History">
        <p>No accrual records exist.</p>
      </Notification>
    );
  }

  return <AccrualHistory empId={empId} activeYears={activeYears.data} />;
}

function AccrualHistory({ empId, activeYears }) {
  const [year, setYear] = useState(activeYears[0]);
  const [detailsRecord, setDetailsRecord] = useState(null);

  // The legacy directive requested the full calendar year, ending at the start of the next.
  const accruals = useAccrualHistory(
    empId,
    `${year}-01-01`,
    `${year + 1}-01-01`,
  );

  const records = useMemo(
    () => (accruals.data || []).filter(shouldDisplayRecord).reverse(),
    [accruals.data],
  );

  return (
    <div>
      <Controls>
        <label className="font-semibold text-teal-700" htmlFor="year">
          Filter By Year&nbsp;
        </label>
        <select
          id="year"
          name="year"
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

      {accruals.isError && (
        <Notification
          level="warn"
          title="Could not retrieve accrual information."
          message="If you are eligible for accruals please try again later."
        />
      )}

      {accruals.isPending ? (
        <LoadingIndicator variant="sm" />
      ) : records.length === 0 ? (
        <p className="bg-white p-3 text-center">
          No historical accrual records exist for this year. If it is early in
          the year they may not have been created yet.
        </p>
      ) : (
        <div className="bg-white">
          <p className="p-3 text-center">
            Summary of historical accrual records. Click a row to view or print
            a detailed summary of accrual hours.
          </p>
          <AccrualHistoryTable
            records={records}
            onRecordClick={setDetailsRecord}
          />
        </div>
      )}

      <AccrualDetailsModal
        accruals={detailsRecord}
        onClose={() => setDetailsRecord(null)}
      />
    </div>
  );
}

/**
 * Only periods that have actually been processed belong in the history: a computed record
 * that has not been submitted is a projection, and temporary employees do not accrue.
 *
 * Ported from shouldDisplayRecord in the legacy directive.
 */
function shouldDisplayRecord(record) {
  if (record.computed && !record.submitted) {
    return false;
  }
  return !record.empState || record.empState.payType !== "TE";
}
