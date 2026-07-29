import React, { useMemo } from "react";
import { format } from "date-fns";
import Hero from "app/components/Hero";
import LoadingIndicator from "app/components/LoadingIndicator";
import AssertPermission from "app/components/AssertPermission";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import { useAllowance } from "app/views/time/useAllowance";
import { computeRemaining } from "app/views/time/allowanceUtils";
import AllowanceBar from "app/views/time/attendance/record-entry/AllowanceBar";

/**
 * The hours a temporary employee is still allowed to report this year.
 * Ported from the legacy allowanceStatus directive
 * (assets/js/src/time/allowance/allowance-status-directive.js).
 */
export default function AllowanceStatusIndex() {
  const { data: user } = useRequireAuthedUser();

  return (
    <AssertPermission permission="time:allowance-page">
      <div>
        <Hero>Allowed Hours</Hero>
        <AllowanceStatusSection empId={user?.employeeId} />
      </div>
    </AssertPermission>
  );
}

/**
 * A single employee's current allowance. Used both by the My Attendance page and, with a title
 * naming the employee, by the Employee Allowed Hours page.
 *
 * @param empId The employee id.
 * @param title Heading placed above the bar. Omitted when the employee is the user.
 */
export function AllowanceStatusSection({ empId, title }) {
  const currentYear = new Date().getFullYear();
  const allowance = useAllowance(empId, currentYear);

  // The bar reports what is left today, so today is both ends of the range it is computed over.
  const yearAllowance = useMemo(() => {
    const today = format(new Date(), "yyyy-MM-dd");
    return computeRemaining(allowance.data, {
      beginDate: today,
      endDate: today,
    });
  }, [allowance.data]);

  if (allowance.isPending) {
    return <LoadingIndicator />;
  }

  const payType = currentPayType(allowance.data);

  return (
    <div className="bg-white">
      {title && (
        <div className="p-3 text-center">
          <h1 className="text-2xl">{title}</h1>
        </div>
      )}

      {payType !== "TE" && (
        <p className="p-3 text-center">
          Selected employee is non-temporary and does not have an allowance.
        </p>
      )}

      {/*
       * A missing pay type means no salary record covers today, which the legacy page treated as
       * unknown rather than non-temporary, and still showed the bar for.
       */}
      {(payType === "TE" || payType === null) && (
        <div className="p-3">
          <AllowanceBar allowance={yearAllowance} />
        </div>
      )}
    </div>
  );
}

/**
 * The pay type from whichever salary record covers today, or null if none does.
 * Ported from extractCurrentPayType in the legacy directive.
 */
function currentPayType(allowance) {
  const today = format(new Date(), "yyyy-MM-dd");
  const current = (allowance?.salaryRecs || []).findLast(
    (salaryRec) =>
      salaryRec.effectDate <= today &&
      today <= (salaryRec.endDate || "3000-01-01"),
  );
  return current ? current.payType : null;
}
