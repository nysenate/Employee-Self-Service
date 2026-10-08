import ErrorAlert from "app/components/ErrorAlert";
import React from "react";
import LoadingStatus from "app/components/LoadingStatus";
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
    return (
      <LoadingStatus
        message="Loading paycheck history…"
        layout="centered"
        size="lg"
        className="min-h-48 p-6"
      />
    );
  }

  if (!employeeActiveYears.data || !employeeActiveFiscalYears.data) {
    return (
      <ErrorAlert title="Unable to load paycheck years">
        Please try again.
      </ErrorAlert>
    );
  }

  return (
    <>
      {(employeeActiveYears.isError || employeeActiveFiscalYears.isError) && (
        <ErrorAlert title="Unable to refresh paycheck years">
          Please try again.
        </ErrorAlert>
      )}
      {(employeeActiveYears.isFetching ||
        employeeActiveFiscalYears.isFetching) && (
        <LoadingStatus message="Refreshing paycheck years…" />
      )}
      <CheckHistoryForm
        empId={user.employeeId}
        calendarYears={employeeActiveYears.data}
        fiscalYears={employeeActiveFiscalYears.data}
      />
    </>
  );
}
