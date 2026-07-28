import React from "react";
import { setRespCtrHeads, toggleExcludeMembers } from "./todoAssignmentActions";
import OfficeMultiSelect from "../OfficeMultiSelect";

export default function EmployeeFilters({ state, dispatch }) {
  return (
    <div className="mt-4">
      <span className="text-lg font-semibold">Employee Filters</span>
      <div className="mt-1">
        <ExcludeMembersCheckbox state={state} dispatch={dispatch} />
        <OfficeFilter dispatch={dispatch} />
      </div>
    </div>
  );
}

function ExcludeMembersCheckbox({ state, dispatch }) {
  return (
    <label
      className="flex items-center gap-1 font-light"
      htmlFor="excludeMembers"
    >
      <input
        id="excludeMembers"
        name="excludeMembers"
        type="checkbox"
        checked={!state.isSenator}
        onChange={(e) => dispatch(toggleExcludeMembers(e.target.checked))}
      />
      Exclude Members
    </label>
  );
}

function OfficeFilter({ dispatch }) {
  const onChange = (selectedOffices) => {
    dispatch(setRespCtrHeads(selectedOffices));
  };
  return <OfficeMultiSelect onChange={onChange} />;
}
