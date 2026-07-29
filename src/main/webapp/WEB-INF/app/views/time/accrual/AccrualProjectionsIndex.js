import React, { useEffect, useMemo, useState } from "react";
import Hero from "app/components/Hero";
import Notification from "app/components/Notification";
import LoadingIndicator from "app/components/LoadingIndicator";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import { useEmployee } from "app/views/useEmployee";
import { useAccrualHistory } from "app/views/time/useAccrual";
import AccrualDetailsModal from "app/views/time/accrual/AccrualDetailsModal";
import AccrualProjectionsTable from "app/views/time/accrual/AccrualProjectionsTable";
import {
  clearChangedFlags,
  initializeProjection,
  isSubmittedSummary,
  isValidProjection,
  recalculateProjections,
  setChangedFlags,
} from "app/views/time/accrual/projectionUtils";

/**
 * Lets an employee try out accrual usage for the pay periods still ahead of them, and see
 * what each one would leave available. Nothing entered here is saved.
 *
 * Ported from the legacy accrualProjections directive
 * (assets/js/src/time/accrual/accrual-projections-directive.js).
 */
export default function AccrualProjectionsIndex() {
  const { data: user } = useRequireAuthedUser();

  return (
    <div>
      <Hero>Accrual Projections</Hero>
      <AccrualProjectionsSection empId={user?.employeeId} />
    </div>
  );
}

/**
 * The accrual projections for a single employee. Used both by the My Accruals page and, for
 * an arbitrary employee, by the Employee Search page.
 */
export function AccrualProjectionsSection({ empId }) {
  const employee = useEmployee(empId);

  /*
   * The legacy page asked for this year plus the previous six months: the projections are all
   * in this year, but the running usage they start from comes from the last processed record,
   * which may sit in the previous year.
   */
  const year = new Date().getFullYear();
  const accruals = useAccrualHistory(
    empId,
    `${year - 1}-07-01`,
    `${year + 1}-01-01`,
  );

  const isLoading = employee.isPending || accruals.isPending;

  return (
    <div>
      {accruals.isError && (
        <Notification
          level="warn"
          title="Could not retrieve accrual information."
          message="If you are eligible for accruals please try again later."
        />
      )}

      {employee.data?.payType === "TE" && <TemporaryEmployeeNotice />}

      {isLoading ? (
        <LoadingIndicator />
      ) : (
        <AccrualProjections key={empId} accruals={accruals.data || []} />
      )}
    </div>
  );
}

function AccrualProjections({ accruals }) {
  const [detailsRecord, setDetailsRecord] = useState(null);

  /*
   * Processed records, most recent first. Only their first entry is used, as the base the
   * year's running usage is carried forward from.
   */
  const accSummaries = useMemo(
    () => accruals.filter(isSubmittedSummary).reverse(),
    [accruals],
  );

  /*
   * Entered hours are held locally: the server has nothing to save them to, and re-reading
   * the query would throw away what the employee is in the middle of trying out.
   */
  const [projections, setProjections] = useState(() =>
    accruals.filter(isValidProjection).map(initializeProjection),
  );
  const [flashing, setFlashing] = useState(false);

  // Let the flashed available hours paint, then clear the flags so they fade back.
  useEffect(() => {
    if (!flashing) {
      return;
    }
    const timeout = setTimeout(() => {
      setProjections(clearChangedFlags);
      setFlashing(false);
    }, 0);
    return () => clearTimeout(timeout);
  }, [flashing]);

  const handleUsageChange = (index, field, type, hours) => {
    setProjections((current) => {
      const edited = current.map((projection, i) =>
        i === index ? { ...projection, [field]: hours } : projection,
      );
      return setChangedFlags(
        recalculateProjections(edited, accSummaries),
        index,
        type,
      );
    });
    setFlashing(true);
  };

  if (projections.length === 0) {
    return (
      <div className="mt-5 bg-white">
        <p className="p-3 text-center">No projections exist for this year.</p>
      </div>
    );
  }

  return (
    <div className="mt-5 bg-white">
      <p className="p-3 text-center">
        The following hours are projected and can be adjusted as time records
        are processed.
        <br />
        Enter hours into the 'Use' column to view projected available hours. No
        changes will be saved.
        <br />
        Click a row to view or print a detailed summary of projected accrual
        hours.
      </p>
      <AccrualProjectionsTable
        projections={projections}
        onUsageChange={handleUsageChange}
        onRecordClick={setDetailsRecord}
      />
      <AccrualDetailsModal
        accruals={detailsRecord}
        onClose={() => setDetailsRecord(null)}
      />
    </div>
  );
}

/** Ported from WEB-INF/view/template/time/accrual/te-accruals.jsp. */
function TemporaryEmployeeNotice() {
  return (
    <Notification level="warn" title="Temporary Employee Accruals">
      <p>
        Records indicate that you are currently a temporary employee.
        <br />
        Temporary employees do not accrue personal, vacation or sick time.
        <br />
        Temporary employees will retain accrued time from previous Regular or
        Special Annual employment.
        <br />
        This time cannot be used until the employee returns to Regular or
        Sepecial Annual employment.
      </p>
    </Notification>
  );
}
