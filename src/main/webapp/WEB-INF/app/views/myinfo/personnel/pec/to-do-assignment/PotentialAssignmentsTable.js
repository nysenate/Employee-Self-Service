import React, { useState } from "react";
import { MinusIcon } from "@heroicons/react/16/solid";
import { useTasks } from "../to-do-reporting/useTasks";
import { useManuallyAssignTask } from "../useTaskAssignment";
import Modal from "../../../../../components/Modal";
import { isoToMediumDate } from "../../../../../utils/dateUtils";
import Button from "app/components/Button";
import useAuthedUser from "app/hooks/useAuthedUser";

export default function PotentialAssignmentsTable({
  potentialAssignments,
  state,
  dispatch,
}) {
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
            <th className="w-20">Unassigned/ Active</th>
            <th>Name</th>
            <th>Office</th>
          </tr>
        </thead>
        <tbody className="table__body table__body--striped table__body--highlight">
          {potentialAssignments.map((a) => (
            <AssignmentRow
              key={a.employee.employeeId}
              emp={a.employee}
              potentialAssignments={a.tasks}
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

function AssignmentRow({
  emp,
  potentialAssignments,
  tasksMap,
  showDetails,
  onClick,
}) {
  const showDetailsClasses = showDetails ? "border-1 border-gray-500" : "";
  return (
    <>
      <tr
        className={`table__row ${showDetailsClasses}`}
        onClick={() => onClick(emp.employeeId)}
      >
        <td>
          <CompletedStatus
            potentialAssignments={potentialAssignments}
            tasksMap={tasksMap}
          />
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
            <RowDetails
              emp={emp}
              assignments={potentialAssignments}
              tasksMap={tasksMap}
            />
          </td>
        </tr>
      )}
    </>
  );
}

function CompletedStatus({ potentialAssignments, tasksMap }) {
  const numOfActiveTasks = [...tasksMap.values()].filter(
    (t) => t.active,
  ).length;
  return (
    <div className="flex items-center gap-1">
      <MinusIcon className="h-4 w-4 cursor-pointer text-yellow-600" />
      <span>
        {potentialAssignments.length}/{numOfActiveTasks}
      </span>
    </div>
  );
}

function RowDetails({ emp, assignments, tasksMap }) {
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

      {assignments.length > 0 && (
        <div className="my-2">
          <div className="font-semibold">Unassigned Trainings:</div>
          <ul className="ml-8 list-disc">
            {assignments.map((assignment) => (
              <UnassignedDetails
                key={assignment.taskId}
                emp={emp}
                assignment={assignment}
                tasksMap={tasksMap}
              />
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

// TODO similar to login button, try to extract a common component?
const buttonClasses = `py-0.5 bg-gray-100 border-1 border-gray-400 transition
duration-500 hover:bg-gray-50 hover:text-teal-600 disabled:pointer-events-none disabled:opacity-50`;

function UnassignedDetails({ emp, assignment, tasksMap }) {
  const [isAssignModalOpen, setIsAssignModalOpen] = useState(false);

  return (
    <li>
      {tasksMap.get(assignment.taskId).title}
      <div className="mb-4 mt-1 flex gap-1">
        <button
          className={`${buttonClasses} px-1`}
          onClick={() => setIsAssignModalOpen(true)}
        >
          Assign Task
        </button>
      </div>
      <AssignTaskModal
        isOpen={isAssignModalOpen}
        setIsOpen={setIsAssignModalOpen}
        emp={emp}
        task={tasksMap.get(assignment.taskId)}
      />
    </li>
  );
}

function AssignTaskModal({ isOpen, setIsOpen, emp, task }) {
  const { data: user } = useAuthedUser();
  const manuallyAssignApi = useManuallyAssignTask();

  const onProceed = () => {
    manuallyAssignApi
      .mutateAsync({
        updatedByEmpId: user.employeeId,
        taskId: task.taskId,
        assignedEmpId: emp.employeeId,
      })
      .then(() => setIsOpen(false))
      .catch((error) => {
        throw error;
      });
  };

  return (
    <Modal isOpen={isOpen}>
      <Modal.Title>Personnel Task Assignment</Modal.Title>
      <Modal.Body>
        <div className="text-center">
          <p className="mb-1 font-semibold">
            Tasks that rely on external services (Everfi & KnowBe4) must be
            assigned on their platforms. ESS CANNOT assign tasks on their
            platforms!
          </p>
          <p className="mb-1 font-semibold">
            Warning: You are attempting to assign a task to employee
          </p>
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
