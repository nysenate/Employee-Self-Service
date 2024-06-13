import React from "react"
import useAuth from "app/contexts/Auth/useAuth";
import LoadingIndicator from "app/components/LoadingIndicator";
import CheckHistoryForm from "app/views/myinfo/payroll/checkhistory/CheckHistoryForm";
import { useEmployeeActiveYears } from "app/api/activeYearsApi";


export default function CheckHistoryIndex() {
  const empId = useAuth().empId()
  const employeeActiveYears = useEmployeeActiveYears(empId, false)
  const employeeActiveFiscalYears = useEmployeeActiveYears(empId, true)

  if (employeeActiveYears.isPending || employeeActiveFiscalYears.isPending) {
    return <LoadingIndicator/>
  }

  return <CheckHistoryForm empId={empId}
                           calendarYears={employeeActiveYears.data}
                           fiscalYears={employeeActiveFiscalYears.data}/>
}