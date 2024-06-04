import React, { useState } from "react"
import useAuth from "app/contexts/Auth/useAuth";
import { fetchApiJson } from "app/utils/fetchJson";
import LoadingIndicator from "app/components/LoadingIndicator";
import CheckHistoryForm from "app/views/myinfo/payroll/checkhistory/CheckHistoryForm";


export default function CheckHistoryIndex() {
  let empId = useAuth().empId()
  const [ calendarYears, setCalendarYears ] = useState()
  const [ fiscalYears, setFiscalYears ] = useState()

  React.useEffect(() => {
    async function load() {
      const calYearsPromise = fetchApiJson(`/employees/activeYears?empId=${empId}&fiscalYear=false`)
        .then((body) => body.activeYears)
      const fiscalYearsPromise = fetchApiJson(`/employees/activeYears?empId=${empId}&fiscalYear=true`)
        .then((body) => body.activeYears)

      const [ cYears, fYears ] = await Promise.all([ calYearsPromise, fiscalYearsPromise ])
      setCalendarYears(cYears)
      setFiscalYears(fYears)
    }

    load()
  }, [ empId ])

  if (!calendarYears || !fiscalYears) {
    return <LoadingIndicator/>
  } else {
    return <CheckHistoryForm empId={empId} calendarYears={calendarYears} fiscalYears={fiscalYears}/>
  }
}