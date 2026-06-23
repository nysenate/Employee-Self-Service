import React, { useState } from "react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import LoadingIndicator from "app/components/LoadingIndicator";
import Card from "app/components/Card";
import Paycheck from "app/views/myinfo/payroll/checkhistory/Paycheck";
import { useEmployeePaychecks } from "app/views/myinfo/payroll/checkhistory/useEmployeePaychecks";

export default function CheckHistoryForm({
  empId,
  calendarYears,
  fiscalYears,
}) {
  const latestCalendarYear = maxYear(calendarYears);
  const latestFiscalYear = maxYear(fiscalYears);
  const [year, setYear] = useState(latestCalendarYear);
  const [useFiscalYears, setUseFiscalYears] = useState(false);
  const selectedYears = useFiscalYears ? fiscalYears : calendarYears;
  const hasYears = selectedYears.length > 0 && Number.isFinite(year);

  React.useEffect(() => {
    if (!selectedYears.includes(year)) {
      setYear(maxYear(selectedYears));
    }
  }, [selectedYears, year]);

  function handleFiscalYearChange(event) {
    const nextUseFiscalYears = event.target.checked;
    setUseFiscalYears(nextUseFiscalYears);
    setYear(nextUseFiscalYears ? latestFiscalYear : latestCalendarYear);
  }

  return (
    <div>
      <Hero>Paycheck History</Hero>
      <Controls className="p-3 text-center">
        <div className="inline-block w-56">
          <label className="mr-1 font-semibold text-teal-700" htmlFor="year">
            Filter by year
          </label>
          <select
            id="year"
            name="year"
            className="select"
            value={year}
            onChange={(e) => setYear(parseInt(e.target.value))}
            disabled={!hasYears}
          >
            {yearOptions(fiscalYears, calendarYears, useFiscalYears)}
          </select>
        </div>
        <div className="inline-flex items-center">
          <label
            className="mx-1 text-sm font-semibold text-teal-700"
            htmlFor="useFiscalYears"
          >
            Show Fiscal Years
          </label>
          <input
            id="useFiscalYears"
            name="useFiscalYears"
            type="checkbox"
            checked={useFiscalYears}
            onChange={handleFiscalYearChange}
          />
        </div>
      </Controls>
      {hasYears ? (
        <CheckResults
          empId={empId}
          year={year}
          useFiscalYears={useFiscalYears}
        />
      ) : (
        <NoPaychecksFound year={year} />
      )}
    </div>
  );
}

function CheckResults({ empId, year, useFiscalYears }) {
  const paycheckSummary = useEmployeePaychecks(empId, year, useFiscalYears);

  if (paycheckSummary.isPending) {
    return <LoadingIndicator />;
  }

  return (
    <>
      {paycheckSummary.data.paychecks.length === 0 ? (
        <NoPaychecksFound year={year} />
      ) : (
        <Card className="mt-3">
          <Card.Header>
            <span className="text-lg font-semibold">
              {paycheckHeader(year, useFiscalYears)}
            </span>
          </Card.Header>
          <Paycheck summary={paycheckSummary.data} />
        </Card>
      )}
    </>
  );
}

function NoPaychecksFound({ year }) {
  return (
    <Card className="mt-3">
      <Card.Header>
        <span className="text-lg font-semibold">
          {Number.isFinite(year)
            ? `No paychecks found for ${year}`
            : "No paycheck records found"}
        </span>
      </Card.Header>
    </Card>
  );
}

const maxYear = (years) => {
  return years.length > 0 ? Math.max(...years) : "";
};

const yearOptions = (fiscalYears, calendarYears, useFiscalYears) => {
  return useFiscalYears
    ? fiscalYears.map((year) => (
        <option value={year} key={year}>
          {year - 1} - {year}
        </option>
      ))
    : calendarYears.map((year) => (
        <option value={year} key={year}>
          {year}
        </option>
      ));
};

const paycheckHeader = (year, useFiscalYears) => {
  return useFiscalYears ? (
    <>
      {year - 1} - {year} Fiscal Year Paycheck Records
    </>
  ) : (
    <>{year} Paycheck Records</>
  );
};
