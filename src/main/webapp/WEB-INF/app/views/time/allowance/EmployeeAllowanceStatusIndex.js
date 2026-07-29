import React from "react";
import AssertPermission from "app/components/AssertPermission";
import EmployeeSelectPage from "app/views/time/personnel/EmployeeSelectPage";
import { AllowanceStatusSection } from "app/views/time/allowance/AllowanceStatusIndex";

/**
 * Allowed hours for any temporary employee the user currently supervises.
 * Ported from WEB-INF/view/template/time/allowance/emp-status.jsp.
 */
export default function EmployeeAllowanceStatusIndex() {
  return (
    <AssertPermission permission="time:emp-allowance-page">
      <EmployeeSelectPage
        heading="Employee Allowed Hours"
        subject="Current Allowed Hours"
        activeOnly
        payType="TE"
      >
        {(empId) => <AllowanceStatusSection empId={empId} />}
      </EmployeeSelectPage>
    </AssertPermission>
  );
}
