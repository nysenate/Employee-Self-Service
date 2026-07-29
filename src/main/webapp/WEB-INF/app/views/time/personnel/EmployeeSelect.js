import React, { useEffect, useMemo, useRef, useState } from "react";
import Controls from "app/components/Controls";
import Notification from "app/components/Notification";
import LoadingIndicator from "app/components/LoadingIndicator";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import {
  isCurrentlySupervised,
  useSupEmpGroup,
} from "app/views/time/personnel/useSupEmpGroup";

/**
 * Picks one of the employees the user supervises, so a supervisor can view a page for them.
 * When the user supervises other supervisors, a second dropdown above it chooses whose
 * employees are listed.
 *
 * Ported from the legacy employeeSelect directive
 * (assets/js/src/common/employee-select-directive.js).
 *
 * @param selectSubject What is being viewed, used in the label: "View {subject} for Employee".
 * @param activeOnly If true, only supervisors and employees supervised today are listed.
 * @param showSenators If true, senators are listed too. They are omitted by default.
 * @param payType If given, a regex source matching the pay types to list, i.e. "RA|SA".
 * @param onSelect Called with the selected employee info, or null when there is none.
 */
export default function EmployeeSelect({
  selectSubject = "info",
  activeOnly = false,
  showSenators = false,
  payType,
  onSelect,
}) {
  const { data: user } = useRequireAuthedUser();
  const supEmpGroup = useSupEmpGroup(user?.employeeId);
  const { supEmpGroups, getEmpInfos } = supEmpGroup;

  const [iSelEmpGroup, setISelEmpGroup] = useState(0);
  const [iSelEmp, setISelEmp] = useState(0);

  // Only groups the user still supervises are offered when the page asks for active only.
  const selectableGroups = useMemo(
    () =>
      supEmpGroups
        .map((empGroup, index) => ({ empGroup, index }))
        .filter(
          ({ empGroup }) => !activeOnly || isCurrentlySupervised(empGroup),
        ),
    [supEmpGroups, activeOnly],
  );

  const employees = useMemo(() => {
    const payTypeRegex = payType ? new RegExp(payType, "i") : null;
    return getEmpInfos(iSelEmpGroup, !showSenators).filter(
      (emp) =>
        (!activeOnly || isCurrentlySupervised(emp)) &&
        (!payTypeRegex || payTypeRegex.test(emp.payType)),
    );
  }, [getEmpInfos, iSelEmpGroup, showSenators, activeOnly, payType]);

  // Changing the supervisor replaces the employee list, so the selection restarts at the top.
  useEffect(() => {
    setISelEmp(0);
  }, [iSelEmpGroup]);

  /*
   * Held in a ref so that the page above is told about the selection whenever it changes,
   * without having to hand down a callback that keeps its identity between renders.
   */
  const onSelectRef = useRef(onSelect);
  onSelectRef.current = onSelect;

  const selectedEmp = employees[iSelEmp] || null;
  useEffect(() => {
    onSelectRef.current(selectedEmp);
  }, [selectedEmp]);

  if (supEmpGroup.isPending) {
    return <LoadingIndicator />;
  }

  return (
    <div>
      <Controls>
        {selectableGroups.length > 1 && (
          <p className="mb-2">
            <label
              className="font-semibold text-teal-700"
              htmlFor="sup-emp-group"
            >
              View Employees Under Supervisor&nbsp;
            </label>
            <GroupedSelect
              id="sup-emp-group"
              value={iSelEmpGroup}
              onChange={setISelEmpGroup}
              options={selectableGroups.map(({ empGroup, index }) => ({
                value: index,
                label: empGroup.dropDownLabel,
                group: empGroup.group,
              }))}
            />
          </p>
        )}
        <p>
          <label className="font-semibold text-teal-700" htmlFor="selected-emp">
            View {selectSubject} for Employee&nbsp;
          </label>
          {employees.length > 0 && (
            <GroupedSelect
              id="selected-emp"
              value={iSelEmp}
              onChange={setISelEmp}
              options={employees.map((emp, index) => ({
                value: index,
                label: emp.dropDownLabel,
                group: emp.group,
              }))}
            />
          )}
        </p>
      </Controls>

      {employees.length === 0 && (
        <Notification
          level="info"
          title={
            selectableGroups.length > 1
              ? `No valid Employee ${selectSubject} can be viewed for the selected supervisor.`
              : `No valid Employee ${selectSubject} are available for viewing.`
          }
        />
      )}
    </div>
  );
}

/**
 * A select whose options are collected under optgroups, matching the legacy "group by" on the
 * Angular selects. Options without a group are listed ahead of any group.
 */
function GroupedSelect({ id, value, onChange, options }) {
  const ungrouped = options.filter((option) => !option.group);
  const groupNames = [
    ...new Set(options.filter((option) => option.group).map((o) => o.group)),
  ];

  return (
    <select
      id={id}
      name={id}
      className="select"
      value={value}
      onChange={(e) => onChange(parseInt(e.target.value))}
    >
      {ungrouped.map((option) => (
        <option value={option.value} key={option.value}>
          {option.label}
        </option>
      ))}
      {groupNames.map((groupName) => (
        <optgroup label={groupName} key={groupName}>
          {options
            .filter((option) => option.group === groupName)
            .map((option) => (
              <option value={option.value} key={option.value}>
                {option.label}
              </option>
            ))}
        </optgroup>
      ))}
    </select>
  );
}
