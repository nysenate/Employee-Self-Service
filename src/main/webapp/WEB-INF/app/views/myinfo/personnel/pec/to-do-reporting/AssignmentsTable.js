import { CheckIcon, MinusIcon, XMarkIcon } from "@heroicons/react/16/solid";
import React, { useState } from "react";
import { isoToMediumDate } from "app/utils/dateUtils";
import Button from "app/components/Button";
import useAuth from "app/contexts/Auth/useAuth";
import Modal from "app/components/Modal";
import { useTasks } from "app/views/myinfo/personnel/pec/to-do-reporting/useTasks";
import {
  useManuallyDeactivateTaskAssignment,
  useManuallyOverrideCompletionStatus,
} from "app/views/myinfo/personnel/pec/useTaskAssignment";

export default function AssignmentsTable({ taskAssignments, state, dispatch }) {
  // Assignment details will be displayed for the row of this employee. Only display one row details at a time.
  const [selectedEmpId, setSelectedEmpId] = useState(null);
  // Load all tasks so we can look up full info on assignments.
  const tasksApi = useTasks(false);
  // Store all tasks in a map of taskId to task obj.
  const [tasksMap, setTasksMap] = useState(new Map());

  React.useEffect(() => {
    if (tasksApi.isSuccess) {
      let map = new Map();
      for (const item of tasksApi.data) {
        map.set(item.taskId, item);
      }
      setTasksMap(map);
    }
  }, [tasksApi.data]);

  const onRowClick = (empId) => {
    if (empId === selectedEmpId) {
      setSelectedEmpId(null);
    } else {
      setSelectedEmpId(empId);
    }
  };

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
              tasksMap={tasksMap}
              showDetails={a.employee.employeeId === selectedEmpId}
              onClick={onRowClick}
            />
          ))}
        </tbody>
      </table>
    </div>
  );
}

function AssignmentRow({ emp, assignments, tasksMap, showDetails, onClick }) {
  const showDetailsClasses = showDetails ? "border-1 border-gray-500" : "";
  return (
    <>
      <tr
        className={`table__row ${showDetailsClasses}`}
        onClick={() => onClick(emp.employeeId)}
      >
        <td>
          <CompletedStatus assignments={assignments} />
        </td>
        <td className="table__cell table__cell--left">
          {emp.lastName}, {emp.firstName}
          {emp.initial ? "," : ""} {emp.initial}
        </td>
        <td className="table__cell table__cell--left">
          {emp.respCtr?.respCenterHead?.name ?? ""}
        </td>
      </tr>
      {showDetails && (
        <tr className={showDetailsClasses}>
          <td colSpan="3">
            <AssignmentRowDetails
              emp={emp}
              assignments={assignments}
              tasksMap={tasksMap}
            />
          </td>
        </tr>
      )}
    </>
  );
}

function CompletedStatus({ assignments }) {
  const totalCount = assignments.length;
  const completedCount = assignments.filter((a) => a.completed).length;

  const completedStatusIcon = () => {
    if (completedCount === totalCount) {
      return <CheckIcon className="h-4 w-4 cursor-pointer text-green-900" />;
    } else if (completedCount === 0) {
      return <XMarkIcon className="h-4 w-4 cursor-pointer text-red-600" />;
    } else {
      return <MinusIcon className="h-4 w-4 cursor-pointer text-yellow-600" />;
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

// TODO similar to login button, try to extract a common component?
const buttonClasses = `py-0.5 bg-gray-100 border-1 border-gray-400 transition
duration-500 hover:bg-gray-50 hover:text-teal-600 disabled:pointer-events-none disabled:opacity-50`;

function AssignmentRowDetails({ emp, assignments, tasksMap }) {
  const incompleteAssignments = assignments.filter((a) => !a.completed);
  const completedAssignments = assignments.filter((a) => a.completed);

  return (
    <div className="px-2 text-left">
      <div>
        <span className="font-semibold">Email: </span>
        <span>{emp.email}</span>
      </div>
      <div>
        <span className="font-semibold">Cont. Service From: </span>
        <span>{isoToMediumDate(emp.contServiceDate)}</span>
      </div>

      {incompleteAssignments.length > 0 && (
        <div className="my-2">
          <div className="font-semibold">Incomplete Trainings:</div>
          <ul className="ml-8 list-disc">
            {incompleteAssignments.map((assignment) => (
              <IncompleteAssignmentDetails
                key={assignment.taskId}
                emp={emp}
                assignment={assignment}
                tasksMap={tasksMap}
              />
            ))}
          </ul>
        </div>
      )}

      {completedAssignments.length > 0 && (
        <div className="my-2">
          <div className="font-semibold">Completed Trainings:</div>
          <ul className="ml-8 list-disc">
            {completedAssignments.map((assignment) => (
              <li key={assignment.taskId}>
                {tasksMap.get(assignment.taskId).title}
                <div className="mb-4 mt-1">
                  <a
                    href={`/api/v1/personnel/task/acknowledgment/download?taskId=${assignment.taskId}&empId=${assignment.empId}`}
                    className={`${buttonClasses} px-1`}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    Download signed pdf
                  </a>
                  <span className="font-light text-gray-700">
                    {" "}
                    completed {isoToMediumDate(assignment.timestamp)}
                  </span>
                </div>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

function IncompleteAssignmentDetails({ emp, assignment, tasksMap }) {
  const [isOverrideModalOpen, setIsOverrideModalOpen] = useState(false);
  const [isDeactivateModalOpen, setIsDeactivateModalOpen] = useState(false);

  return (
    <li>
      {tasksMap.get(assignment.taskId).title}
      <div className="mb-4 mt-1 flex gap-1">
        <button
          className={`${buttonClasses} px-1`}
          onClick={() => setIsOverrideModalOpen(true)}
        >
          Manually Override
        </button>
        <button
          className={`${buttonClasses} px-1`}
          onClick={() => setIsDeactivateModalOpen(true)}
        >
          Deactivate Task
        </button>
      </div>
      <ManuallyOverrideModal
        isOpen={isOverrideModalOpen}
        setIsOpen={setIsOverrideModalOpen}
        emp={emp}
        assignment={assignment}
        task={tasksMap.get(assignment.taskId)}
      />
      <ManuallyDeactivateModal
        isOpen={isDeactivateModalOpen}
        setIsOpen={setIsDeactivateModalOpen}
        emp={emp}
        assignment={assignment}
        task={tasksMap.get(assignment.taskId)}
      />
    </li>
  );
}

function ManuallyOverrideModal({ isOpen, setIsOpen, emp, assignment, task }) {
  const userEmpId = useAuth().empId();
  const manuallyOverrideApi = useManuallyOverrideCompletionStatus();

  const onProceed = () => {
    manuallyOverrideApi
      .mutateAsync({
        updatedByEmpId: userEmpId,
        taskId: task.taskId,
        isCompleted: true,
        assignedEmpId: assignment.empId,
      })
      .then(() => setIsOpen(false))
      .catch((error) => {
        throw error;
      });
  };

  return (
    <Modal isOpen={isOpen}>
      <Modal.Title>Personnel Task Override</Modal.Title>
      <Modal.Body>
        <div className="text-center">
          Warning: You are attempting to submit a task COMPLETION override for
          employee
          <br />
          {emp.fullName}
          <br />
          for task
          <br />
          {task.title}
        </div>
      </Modal.Body>
      <Modal.Buttons>
        <Button color="success" onClick={onProceed}>
          Proceed
        </Button>
        <Button color="error" onClick={() => setIsOpen(false)}>
          Cancel
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}

function ManuallyDeactivateModal({ isOpen, setIsOpen, emp, assignment, task }) {
  const userEmpId = useAuth().empId();
  const deactivateTaskAssignmentApi = useManuallyDeactivateTaskAssignment();

  const onProceed = () => {
    deactivateTaskAssignmentApi
      .mutateAsync({
        updatedByEmpId: userEmpId,
        taskId: task.taskId,
        isActive: false,
        assignedEmpId: assignment.empId,
      })
      .then(() => setIsOpen(false))
      .catch((error) => {
        throw error;
      });
  };

  return (
    <Modal isOpen={isOpen}>
      <Modal.Title>Personnel Task Override</Modal.Title>
      <Modal.Body>
        <div className="text-center">
          Warning: You are attempting to submit a task ACTIVE STATUS override
          for employee
          <br />
          {emp.fullName}
          <br />
          for task
          <br />
          {task.title}
        </div>
      </Modal.Body>
      <Modal.Buttons>
        <Button color="success" onClick={onProceed}>
          Proceed
        </Button>
        <Button color="error" onClick={() => setIsOpen(false)}>
          Cancel
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
