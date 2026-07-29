import React from "react";
import EmployeeSelectPage from "app/views/time/personnel/EmployeeSelectPage";
import { AccrualProjectionsSection } from "app/views/time/accrual/AccrualProjectionsIndex";

/**
 * Accrual projections for any annual employee the user currently supervises.
 * Ported from WEB-INF/view/template/time/accrual/emp-projections.jsp.
 */
export default function EmployeeAccrualProjectionsIndex() {
  return (
    <EmployeeSelectPage
      heading="Employee Accrual Projections"
      subject="Accrual Projections"
      activeOnly
      payType="RA|SA"
    >
      {(empId) => <AccrualProjectionsSection empId={empId} />}
    </EmployeeSelectPage>
  );
}
