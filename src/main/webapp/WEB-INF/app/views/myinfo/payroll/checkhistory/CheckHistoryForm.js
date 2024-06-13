import React, { useState } from "react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import { fetchApiJson } from "app/utils/fetchJson";
import LoadingIndicator from "app/components/LoadingIndicator";
import Card from "app/components/Card";
import Paycheck from "app/views/myinfo/payroll/checkhistory/Paycheck";
import { useEmployeePaychecks } from "app/api/employeePaychecksApi";

export default function CheckHistoryForm({ empId, calendarYears, fiscalYears }) {
  const [ year, setYear ] = useState(Math.max(...calendarYears))
  const [ useFiscalYears, setUseFiscalYears ] = useState(false)

  // Year can end up in the future when useFiscalYear = true and the most recent fiscal year is checked.
  // Then, unchecking useFiscalYear, will lead to year being an invalid value (not in calendarYears).
  // This checks and corrects for that edge case.
  React.useEffect(() => {
    if (!useFiscalYears && year > Math.max(...calendarYears)) {
      setYear(Math.max(...calendarYears))
    }
  }, [ useFiscalYears ])

  return (
    <div>
      <Hero>Paycheck History</Hero>
      <Controls className="text-center p-3">
        <div className="inline-block w-56">
          <label className="text-teal-700 font-semibold mr-1" htmlFor="year">Filter by year</label>
          <select id="year"
                  name="year"
                  className="select"
                  value={year}
                  onChange={e => setYear(e.target.value)}>
            {yearOptions(fiscalYears, calendarYears, useFiscalYears)}
          </select>
        </div>
        <div className="inline-flex">
          <label className="text-teal-700 font-semibold mx-1" htmlFor="useFiscalYears">Show Fiscal Years</label>
          <input id="useFiscalYears"
                 name="useFiscalYears"
                 type="checkbox"
                 defaultChecked={useFiscalYears}
                 onChange={(e => setUseFiscalYears(e.target.checked))}/>
        </div>
      </Controls>
      <CheckResults empId={empId}
                    year={year}
                    useFiscalYears={useFiscalYears}/>
    </div>
  )
}

function CheckResults({ empId, year, useFiscalYears }) {
  const paycheckSummary = useEmployeePaychecks(empId, year, useFiscalYears)

  if (paycheckSummary.isPending) {
    return <LoadingIndicator/>
  }

  return (
    <>
      {paycheckSummary.data.paychecks.length === 0
       ? <Card className="mt-3">
         <Card.Header><span className="text-lg font-semibold">No paychecks found for {year}</span>
         </Card.Header>
       </Card>
       : <Card className="mt-3">
         <Card.Header>
           <span className="text-lg font-semibold">{paycheckHeader(year, useFiscalYears)}</span>
         </Card.Header>
         <Paycheck summary={paycheckSummary.data}/>
       </Card>
      }
    </>
  )
}

const yearOptions = (fiscalYears, calendarYears, useFiscalYears) => {
  return useFiscalYears
         ? fiscalYears.map(year => <option value={year} key={year}>{year - 1} - {year}</option>)
         : calendarYears.map(year => <option value={year} key={year}>{year}</option>)
}

const paycheckHeader = (year, useFiscalYears) => {
  return useFiscalYears
         ? <>{year - 1} - {year} Fiscal Year Paycheck Records</>
         : <>{year} Paycheck Records</>
}
