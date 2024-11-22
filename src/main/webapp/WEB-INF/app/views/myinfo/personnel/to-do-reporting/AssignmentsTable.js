import { CheckIcon, MinusIcon, XMarkIcon } from "@heroicons/react/16/solid";
import React from "react";

// TODO is state needed??
export default function AssignmentsTable({ taskAssignments, state, dispatch }) {
  console.log(taskAssignments);
  return (
    <>
      <Pagination/>
      <Table taskAssignments={taskAssignments}/>
      <Pagination/>
    </>
  );
}

function Table({ taskAssignments }) {
  return (
    <div className="py-3">
      <table className="table">
        <thead>
        <tr className="table__head__row">
          <th className="w-20">Completed/ Assigned</th>
          <th>Name</th>
          <th>Office</th>
        </tr>
        </thead>
        <tbody className="table__body table__body--striped table__body--highlight">
        {taskAssignments.map((a) => (
          <AssignmentRow
            key={a.employee.employeeId}
            emp={a.employee}
            assignments={a.tasks}
          />
        ))}
        </tbody>
      </table>
    </div>
  );
}

function AssignmentRow({ emp, assignments }) {
  return (
    <tr className="table__row">
      <td>
        <CompletedStatus assignments={assignments}/>
      </td>
      <td className="table__cell table__cell--left">
        {emp.lastName}, {emp.firstName}
        {emp.initial ? "," : ""} {emp.initial}
      </td>
      <td className="table__cell table__cell--left">
        {emp.respCtr?.respCenterHead?.name ?? ""}
      </td>
    </tr>
  );
}

function CompletedStatus({ assignments }) {
  const totalCount = assignments.length;
  const completedCount = assignments.filter((a) => a.completed).length;
  const incompleteCount = assignments.filter((a) => !a.completed).length;

  const completedStatusIcon = () => {
    if (completedCount === totalCount) {
      return <CheckIcon className="h-4 w-4 text-green-900 cursor-pointer"/>;
    } else if (completedCount === 0) {
      return <XMarkIcon className="h-4 w-4 text-red-600 cursor-pointer"/>;
    } else {
      return <MinusIcon className="h-4 w-4 text-yellow-600 cursor-pointer"/>;
    }
  };

  return (
    <div className="flex items-center gap-1">
      {completedStatusIcon()}
      <span>
        {completedCount}/{totalCount}
      </span>
    </div>
  );
}

// TODO
function Pagination() {
  return <></>;
}
