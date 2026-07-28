import React from "react";
import LoadingIndicator from "app/components/LoadingIndicator";
import CheckHistoryForm from "app/views/myinfo/payroll/checkhistory/CheckHistoryForm";
import { useEmployeeActiveYears } from "app/views/myinfo/payroll/checkhistory/useEmployeeActiveYears";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";

export default function CheckHistoryIndex() {
  const { data: user } = useRequireAuthedUser();
  const employeeActiveYears = useEmployeeActiveYears(user?.employeeId, false);
  const employeeActiveFiscalYears = useEmployeeActiveYears(
    user?.employeeId,
    true,
  );

  if (employeeActiveYears.isPending || employeeActiveFiscalYears.isPending) {
    return <LoadingIndicator />;
  }

  return (
    <CheckHistoryForm
      empId={user.employeeId}
      calendarYears={employeeActiveYears.data}
      fiscalYears={employeeActiveFiscalYears.data}
    />
  );
}
