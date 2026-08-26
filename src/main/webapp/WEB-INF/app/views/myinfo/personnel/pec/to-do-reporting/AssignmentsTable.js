import {
  CheckIcon,
  ChevronDownIcon,
  MinusIcon,
  XMarkIcon,
} from "@heroicons/react/16/solid";
import React, { useState } from "react";
import { isoToMediumDate } from "app/utils/dateUtils";
import Accordion from "app/components/Accordion";
import Button from "app/components/Button";
import Modal from "app/components/Modal";
import {
  useManuallyDeactivateTaskAssignment,
  useManuallyOverrideCompletionStatus,
} from "app/views/myinfo/personnel/pec/useTaskAssignment";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";

export default function AssignmentsTable({ taskAssignments }) {
  // Assignment details will be displayed for the row of this employee. Only display one row details at a time.
  const [selectedEmpId, setSelectedEmpId] = useState(null);

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
            <th className="w-20 text-center">Remaining</th>
            <th>Name</th>
            <th>Office</th>
          </tr>
        </thead>
        <tbody className="table__body table__body--striped table__body--highlight">
          {taskAssignments.map((empAssignments) => (
            <AssignmentRow
              key={empAssignments.employee.employeeId}
              empAssignments={empAssignments}
              showDetails={empAssignments.employee.employeeId === selectedEmpId}
              onClick={onRowClick}
            />
          ))}
        </tbody>
      </table>
    </div>
  );
}

function AssignmentRow({ empAssignments, showDetails, onClick }) {
  const showDetailsClasses = showDetails ? "!bg-teal-50/70" : "";
  const accentClasses = showDetails ? "border-teal-600" : "border-transparent";
  const emp = empAssignments.employee;
  return (
    <>
      <tr
        className={`table__row group ${showDetailsClasses}`}
        onClick={() => onClick(emp.employeeId)}
      >
        <td className={`border-l-4 py-1.5 text-center ${accentClasses}`}>
          <CompletedStatus empAssignments={empAssignments} />
        </td>
        <td className="table__cell table__cell--left">
          <div className="flex items-center gap-2">
            <ChevronDownIcon
              aria-hidden="true"
              className={`h-4 w-4 shrink-0 text-gray-500 transition-transform group-hover:text-teal-700 ${
                showDetails ? "rotate-180 text-teal-700" : ""
              }`}
            />
            <span className="font-medium text-gray-900 group-hover:underline">
              {emp.lastName}, {emp.firstName}
              {emp.initial ? "," : ""} {emp.initial}
            </span>
          </div>
        </td>
        <td className="table__cell table__cell--left">
          {emp.respCtr?.respCenterHead?.name ?? ""}
        </td>
      </tr>
      {showDetails && (
        <tr>
          <td
            colSpan="3"
            className="border-l-4 border-teal-600 bg-white px-5 py-3"
          >
            <AssignmentRowDetails empAssignments={empAssignments} />
          </td>
        </tr>
      )}
    </>
  );
}

function CompletedStatus({ empAssignments }) {
  const statusConfig = () => {
    if (empAssignments.completionStatus === "ALL_COMPLETE") {
      return {
        label: "All trainings complete",
        text: "Done",
        badgeClasses: "border-green-400 bg-green-100 text-green-900",
        icon: <CheckIcon className="h-4 w-4" />,
      };
    } else if (empAssignments.completionStatus === "ALL_OUTSTANDING") {
      return {
        label: "No trainings complete",
        text: empAssignments.incompleteCount,
        badgeClasses: "border-red-300 bg-red-100 text-red-800",
        icon: <XMarkIcon className="h-4 w-4" />,
      };
    } else {
      return {
        label: "Some trainings complete",
        text: empAssignments.incompleteCount,
        badgeClasses: "border-yellow-300 bg-yellow-100 text-yellow-800",
        icon: <MinusIcon className="h-4 w-4" />,
      };
    }
  };
  const status = statusConfig();

  return (
    <div
      className={`inline-grid w-16 grid-cols-[1rem_1fr] items-center gap-1 rounded border px-2 py-0.5 text-xs font-semibold tabular-nums ${status.badgeClasses}`}
      aria-label={status.label}
      title={status.label}
    >
      <span className="flex justify-center">{status.icon}</span>
      <span className="text-center">{status.text}</span>
    </div>
  );
}

// TODO similar to login button, try to extract a common component?
const buttonClasses = `py-0.5 bg-gray-100 border border-gray-400 transition
duration-500 hover:bg-gray-50 hover:text-teal-600 disabled:pointer-events-none disabled:opacity-50`;

function AssignmentRowDetails({ empAssignments }) {
  const emp = empAssignments.employee;
  const incompleteAssignments = empAssignments.incompleteAssignments;
  const obsoleteAssignments = empAssignments.obsoleteAssignments;
  const completedAssignments = empAssignments.completedAssignments;
  const hasTrainingAssignments =
    incompleteAssignments.length > 0 ||
    obsoleteAssignments.length > 0 ||
    completedAssignments.length > 0;
  const defaultExpandedTrainingSections = [
    incompleteAssignments.length > 0 ? "incomplete" : null,
    completedAssignments.length > 0 ? "completed" : null,
  ].filter(Boolean);

  return (
    <div className="max-w-5xl text-left text-sm">
      <div>
        <span className="font-semibold">Email: </span>
        <span>{emp.email}</span>
      </div>
      <div>
        <span className="font-semibold">Cont. Service From: </span>
        <span>{isoToMediumDate(emp.contServiceDate)}</span>
      </div>

      {hasTrainingAssignments && (
        <div>
          <Accordion
            allowsMultipleExpanded
            className="mt-3 max-w-4xl"
            defaultExpandedKeys={defaultExpandedTrainingSections}
          >
            <TrainingAssignmentsAccordionItem
              id="incomplete"
              title="Incomplete Trainings"
              assignments={incompleteAssignments}
              renderAssignment={(assignment) => (
                <IncompleteAssignmentDetails
                  key={assignment.task.taskId}
                  emp={emp}
                  assignment={assignment}
                />
              )}
            />

            <TrainingAssignmentsAccordionItem
              id="obsolete"
              title="Obsolete Trainings"
              assignments={obsoleteAssignments}
              renderAssignment={(assignment) => (
                <IncompleteAssignmentDetails
                  key={assignment.task.taskId}
                  emp={emp}
                  assignment={assignment}
                />
              )}
            />

            <TrainingAssignmentsAccordionItem
              id="completed"
              title="Completed Trainings"
              assignments={completedAssignments}
              renderAssignment={(assignment) => (
                <li key={assignment.task.taskId}>
                  {assignment.task.title}
                  <div className="mt-1 mb-4">
                    <a
                      href={`/api/v1/personnel/task/acknowledgment/download?taskId=${assignment.task.taskId}&empId=${emp.employeeId}`}
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
              )}
            />
          </Accordion>
        </div>
      )}
    </div>
  );
}

function TrainingAssignmentsAccordionItem({
  id,
  title,
  assignments,
  renderAssignment,
}) {
  if (assignments.length === 0) {
    return null;
  }

  return (
    <Accordion.Item id={id} title={`${title}: ${assignments.length}`}>
      <Accordion.Panel>
        <ul className="ml-8 list-disc">{assignments.map(renderAssignment)}</ul>
      </Accordion.Panel>
    </Accordion.Item>
  );
}

function IncompleteAssignmentDetails({ emp, assignment }) {
  const task = assignment.task;
  const [isCompleteManuallyModalOpen, setIsCompleteManuallyModalOpen] = useState(false);
  const [isDeactivateModalOpen, setIsDeactivateModalOpen] = useState(false);

  return (
    <li>
      {task.title}
      <div className="mt-1 mb-4 flex gap-1">
        {assignment.canMarkComplete && (
          <button
            className={`${buttonClasses} px-1`}
            onClick={() => setIsCompleteManuallyModalOpen(true)}
          >
            Complete Manually
          </button>
        )}
        {assignment.canDeactivateAssignment && (
          <button
            className={`${buttonClasses} px-1`}
            onClick={() => setIsDeactivateModalOpen(true)}
          >
            Deactivate Assignment
          </button>
        )}
      </div>
      <CompleteManuallyModal
        isOpen={isCompleteManuallyModalOpen}
        setIsOpen={setIsCompleteManuallyModalOpen}
        emp={emp}
        assignment={assignment}
      />
      <ManuallyDeactivateModal
        isOpen={isDeactivateModalOpen}
        setIsOpen={setIsDeactivateModalOpen}
        emp={emp}
        assignment={assignment}
      />
    </li>
  );
}

function CompleteManuallyModal({ isOpen, emp, setIsOpen, assignment }) {
  const { data: user } = useRequireAuthedUser();
  const manuallyOverrideApi = useManuallyOverrideCompletionStatus();

  const onProceed = () => {
    manuallyOverrideApi
      .mutateAsync({
        updatedByEmpId: user.employeeId,
        taskId: assignment.task.taskId,
        isCompleted: true,
        assignedEmpId: assignment.empId,
      })
      .then(() => setIsOpen(false))
      .catch((error) => {
        throw error;
      });
  };

  return (
    <Modal isOpen={isOpen} className="w-full max-w-lg">
      <Modal.Title>Complete Task Manually</Modal.Title>
      <Modal.Body>
        <div className="space-y-3">
          <p>
            This will mark the following task as complete for this employee. Use
            this only when completion should be recorded manually.
          </p>

          <div>
            <div>
              <span className="font-semibold">Employee: </span>
              {emp.fullName}
            </div>
            <div>
              <span className="font-semibold">Task: </span>
              {assignment.task.title}
            </div>
          </div>
        </div>
      </Modal.Body>
      <Modal.Buttons>
        <Button variant="primary" onPress={onProceed}>
          Confirm Completion
        </Button>
        <Button variant="secondary" onPress={() => setIsOpen(false)}>
          Cancel
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}

function ManuallyDeactivateModal({ isOpen, setIsOpen, emp, assignment }) {
  const { data: user } = useRequireAuthedUser();
  const deactivateTaskAssignmentApi = useManuallyDeactivateTaskAssignment();

  const onProceed = () => {
    deactivateTaskAssignmentApi
      .mutateAsync({
        updatedByEmpId: user.employeeId,
        taskId: assignment.task.taskId,
        isActive: false,
        assignedEmpId: assignment.empId,
      })
      .then(() => setIsOpen(false))
      .catch((error) => {
        throw error;
      });
  };

  return (
    <Modal isOpen={isOpen} className="w-full max-w-lg">
      <Modal.Title>Deactivate Assignment</Modal.Title>
      <Modal.Body>
        <div className="space-y-3">
          <p>
            This will deactivate the following assignment for this employee. The
            task will no longer be counted as incomplete.
          </p>

          <div>
            <div>
              <span className="font-semibold">Employee: </span>
              {emp.fullName}
            </div>
            <div>
              <span className="font-semibold">Task: </span>
              {assignment.task.title}
            </div>
          </div>
        </div>
      </Modal.Body>
      <Modal.Buttons>
        <Button variant="destructive" onPress={onProceed}>
          Deactivate Assignment
        </Button>
        <Button variant="secondary" onPress={() => setIsOpen(false)}>
          Cancel
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
