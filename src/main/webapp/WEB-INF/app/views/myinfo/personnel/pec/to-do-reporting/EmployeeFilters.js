import React, { useState } from "react";
import {
  setRespCtrHeads,
  toggleInactiveEmployees,
  updateContServDate,
} from "./todoReportingActions";
import { formatISO, sub } from "date-fns";
import OfficeMultiSelect from "../OfficeMultiSelect";

export default function EmployeeFilters({ state, dispatch }) {
  return (
    <div className="mt-4">
      <span className="text-lg font-semibold">Employee Filters</span>
      <div className="mt-1">
        <IncludeInactiveEmployeesCheckbox state={state} dispatch={dispatch} />
        <ContServiceDateFilter state={state} dispatch={dispatch} />
        <OfficeFilter dispatch={dispatch} />
      </div>
    </div>
  );
}

function IncludeInactiveEmployeesCheckbox({ state, dispatch }) {
  return (
    <label
      className="flex items-center gap-1 font-light"
      htmlFor="includeInactiveEmployees"
    >
      <input
        id="includeInactiveEmployees"
        name="includeInactiveEmployees"
        type="checkbox"
        checked={!state.empActive}
        onChange={(e) => dispatch(toggleInactiveEmployees(e.target.checked))}
      />
      Include Inactive Employees
    </label>
  );
}

function ContServiceDateFilter({ state, dispatch }) {
  const [customDate, setCustomDate] = useState(
    formatISO(sub(new Date(), { weeks: 2 }), { representation: "date" }),
  );

  const onCustomDateChange = (date) => {
    setCustomDate(date);
    dispatch(updateContServDate(date));
  };

  return (
    <fieldset className="mt-2">
      <legend className="font-light">Continuous Service Start Date</legend>
      <label className="flex items-center gap-1 font-light" htmlFor="Any">
        <input
          id="Any"
          name="contServDate"
          type="radio"
          value=""
          checked={state.contSrvFrom === ""}
          onChange={(e) => dispatch(updateContServDate(e.target.value))}
        />
        Any
      </label>

      <label className="flex items-center gap-1 font-light" htmlFor="Custom">
        <input
          id="Custom"
          name="contServDate"
          type="radio"
          value={customDate}
          checked={state.contSrvFrom !== ""}
          onChange={(e) => dispatch(updateContServDate(e.target.value))}
        />
        Custom
        {state.contSrvFrom !== "" && (
          <input
            type="date"
            className="ml-2"
            value={customDate}
            onChange={(e) => onCustomDateChange(e.target.value)}
          />
        )}
      </label>
    </fieldset>
  );
}

function OfficeFilter({ dispatch }) {
  const onChange = (selectedOffices) => {
    dispatch(setRespCtrHeads(selectedOffices));
  };
  return <OfficeMultiSelect onChange={onChange} />;
}
