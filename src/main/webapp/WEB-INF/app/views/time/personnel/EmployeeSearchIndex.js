import React, { useMemo } from "react";
import { format } from "date-fns";
import { useSearchParams } from "react-router-dom";
import Hero from "app/components/Hero";
import Accordion from "app/components/Accordion";
import Notification from "app/components/Notification";
import { useEmployee } from "app/views/useEmployee";
import { useAccruals, useAccrualActiveYears } from "app/views/time/useAccrual";
import {
  useAllowance,
  useAllowanceActiveYears,
} from "app/views/time/useAllowance";
import { computeRemaining } from "app/views/time/allowanceUtils";
import AccrualBar from "app/views/time/attendance/record-entry/AccrualBar";
import AllowanceBar from "app/views/time/attendance/record-entry/AllowanceBar";
import { AttendanceHistorySection } from "app/views/time/attendance/history/AttendanceHistoryIndex";
import { AccrualHistorySection } from "app/views/time/accrual/AccrualHistoryIndex";
import { AccrualProjectionsSection } from "app/views/time/accrual/AccrualProjectionsIndex";
import AllowanceHistorySection from "app/views/time/allowance/AllowanceHistorySection";
import EmployeeSearch from "app/views/time/personnel/EmployeeSearch";

/** Pay types whose employees accrue personal, vacation and sick time. */
const ACCRUAL_PAY_TYPES = ["RA", "SA"];

/**
 * The Personnel section's Employee Search page: find an employee and review their attendance,
 * accruals and allowance the same way the owning employee would.
 * Ported from the legacy EmployeeSearchCtrl and search.jsp
 * (WEB-INF/view/template/time/personnel/search.jsp).
 */
export default function EmployeeSearchIndex() {
  const [searchParams, setSearchParams] = useSearchParams();

  const empIdParam = searchParams.get("empId");
  const selectedEmpId = empIdParam ? parseInt(empIdParam) : null;
  const term = searchParams.get("term") ?? "";
  const activeOnly = searchParams.get("activeOnly") === "true";

  const updateParams = (updates, opts) => {
    setSearchParams((prev) => {
      const next = new URLSearchParams(prev);
      Object.entries(updates).forEach(([key, value]) => {
        if (value === null || value === "" || value === false) {
          next.delete(key);
        } else {
          next.set(key, String(value));
        }
      });
      return next;
    }, opts);
  };

  return (
    <div>
      <Hero>Employee Search</Hero>

      <EmployeeSearch
        selectedEmpId={selectedEmpId}
        term={term}
        activeOnly={activeOnly}
        onTermChange={(value) =>
          updateParams({ term: value }, { replace: true })
        }
        onActiveOnlyChange={(value) =>
          updateParams({ activeOnly: value }, { replace: true })
        }
        onSelect={(empId) => updateParams({ empId })}
        onClear={() => updateParams({ empId: null })}
      />

      {selectedEmpId && <EmployeeReview empId={selectedEmpId} />}
    </div>
  );
}

function EmployeeReview({ empId }) {
  const { data: employee } = useEmployee(empId);
  const accrualYears = useAccrualActiveYears(empId);
  const allowanceYears = useAllowanceActiveYears(empId);

  if (!employee) {
    return null;
  }

  const isTemporary = employee.payType === "TE";
  const { senator, active, fullName } = employee;
  const currentYear = new Date().getFullYear();

  const showAccrualHistory = (accrualYears.data?.length ?? 0) > 0;
  const showAccruals =
    showAccrualHistory &&
    accrualYears.data.includes(currentYear) &&
    !isTemporary &&
    !senator;
  const showAllowanceHistory = (allowanceYears.data?.length ?? 0) > 0;

  return (
    <div>
      {!active && (
        <Notification
          level="info"
          title={`${fullName} is not a current Senate employee.`}
        />
      )}

      {senator && (
        <Notification level="info" title={`${fullName} is a Senator`}>
          <p>
            They cannot use or project accruals.
            <br />
            They will not have any attendance or accrual history unless they
            were a non-senator employee in the past.
          </p>
        </Notification>
      )}

      {active && (
        <div className="mt-3">
          {showAccruals && <AccrualBarSection employee={employee} />}
          {isTemporary && <AllowanceBarSection empId={empId} />}
        </div>
      )}

      <Accordion allowsMultipleExpanded className="mt-5">
        <Accordion.Item id="attendance" title="Attendance History">
          <Accordion.Panel>
            <AttendanceHistorySection empId={empId} linkActiveToEntry={false} />
          </Accordion.Panel>
        </Accordion.Item>

        {showAccrualHistory && (
          <Accordion.Item id="accrual-history" title="Accrual History">
            <Accordion.Panel>
              <AccrualHistorySection empId={empId} />
            </Accordion.Panel>
          </Accordion.Item>
        )}

        {!isTemporary && !senator && (
          <Accordion.Item id="accrual-projections" title="Accrual Projections">
            <Accordion.Panel>
              <AccrualProjectionsSection empId={empId} />
            </Accordion.Panel>
          </Accordion.Item>
        )}

        {showAllowanceHistory && (
          <Accordion.Item id="allowance-history" title="Allowance History">
            <Accordion.Panel>
              <AllowanceHistorySection empId={empId} />
            </Accordion.Panel>
          </Accordion.Item>
        )}
      </Accordion>
    </div>
  );
}

/** The accruals available bar, shown for an active annual employee. */
function AccrualBarSection({ employee }) {
  // Only annual employees accrue, so a non-annual employee's bar stays blank, as in the legacy page.
  const eligible =
    employee.active &&
    !employee.senator &&
    ACCRUAL_PAY_TYPES.includes(employee.payType);
  const beforeDate = format(new Date(), "yyyy-MM-dd");
  const accruals = useAccruals(
    eligible ? employee.employeeId : null,
    beforeDate,
  );

  return (
    <AccrualBar
      accruals={accruals.data}
      isLoading={eligible && accruals.isPending}
    />
  );
}

/** The yearly allowance bar, shown for an active temporary employee. */
function AllowanceBarSection({ empId }) {
  const currentYear = new Date().getFullYear();
  const allowance = useAllowance(empId, currentYear);

  const yearAllowance = useMemo(
    () =>
      computeRemaining(allowance.data, {
        beginDate: format(new Date(), "yyyy-MM-dd"),
        endDate: "3000-01-01",
      }),
    [allowance.data],
  );

  return (
    <AllowanceBar allowance={yearAllowance} isLoading={allowance.isPending} />
  );
}
