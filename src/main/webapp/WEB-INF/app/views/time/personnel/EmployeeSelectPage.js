import React, { useState } from "react";
import Hero from "app/components/Hero";
import { possessive } from "app/utils/possessive";
import EmployeeSelect from "app/views/time/personnel/EmployeeSelect";

/**
 * The shape shared by every "Employee ..." page under Manage Employees: a hero, a dropdown of
 * the employees the user supervises, and the same section the employee themselves would see,
 * headed by the employee's name.
 *
 * @param heading The hero text, i.e. "Employee Accrual History".
 * @param subject What is being viewed, i.e. "Accrual History". Used both in the select label and,
 *                possessively, in the heading above the section.
 * @param activeOnly If true, only employees supervised today are listed.
 * @param payType If given, a regex source matching the pay types to list, i.e. "RA|SA".
 * @param children Called with the selected employee's id to render the section for them.
 */
export default function EmployeeSelectPage({
  heading,
  subject,
  activeOnly = false,
  payType,
  children,
}) {
  const [selectedEmp, setSelectedEmp] = useState(null);

  return (
    <div>
      <Hero>{heading}</Hero>

      <EmployeeSelect
        selectSubject={subject}
        activeOnly={activeOnly}
        payType={payType}
        onSelect={setSelectedEmp}
      />

      {selectedEmp && (
        <div className="mt-3">
          <h1 className="bg-white p-3 text-center text-2xl">
            {selectedEmp.empFirstName} {possessive(selectedEmp.empLastName)}{" "}
            {subject}
          </h1>
          {/*
           * Keyed on the employee so that a section's own state, such as the year it has
           * filtered to, starts over rather than carrying across to the next employee.
           */}
          <div key={selectedEmp.empId}>{children(selectedEmp.empId)}</div>
        </div>
      )}
    </div>
  );
}
