import React from "react";
import EmployeeSelectPage from "app/views/time/personnel/EmployeeSelectPage";
import { AccrualHistorySection } from "app/views/time/accrual/AccrualHistoryIndex";

/**
 * Accrual history for any employee the user supervises.
 * Ported from WEB-INF/view/template/time/accrual/emp-history.jsp.
 */
export default function EmployeeAccrualHistoryIndex() {
  return (
    <EmployeeSelectPage
      heading="Employee Accrual History"
      subject="Accrual History"
    >
      {(empId) => <AccrualHistorySection empId={empId} />}
    </EmployeeSelectPage>
  );
}
