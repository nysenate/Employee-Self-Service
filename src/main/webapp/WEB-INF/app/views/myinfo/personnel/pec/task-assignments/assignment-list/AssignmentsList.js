import React, { useEffect, useState } from 'react';
import { AcademicCapIcon, CheckIcon, DocumentTextIcon, TrophyIcon, VideoCameraIcon } from "@heroicons/react/16/solid";
import { Link } from "react-router-dom";
import useAuth from "app/contexts/Auth/useAuth";
import LoadingIndicator from "app/components/LoadingIndicator";
import { isoToMediumDate } from "app/utils/dateUtils";
import { useTaskAssignments } from "app/views/myinfo/personnel/pec/useTaskAssignment";


/**
 * Generates URL, Url Text
 */
const assignmentDisplayFields = (type) => {
  let task = {
    verb: "", // Verb describing the action to take/taken on a task.
    verbPastTense: "", // The past tense of verb.
    link: "", // Url to task, for non Everfi tasks. For Everfi, use `assignment.task.url`.
    icon: null // The icon used in the incomplete list.
  };
  switch (type) {
    case "DOCUMENT_ACKNOWLEDGMENT":
      task.verbPastTense = "Acknowledged"
      task.verb = "Acknowledge"
      task.icon = <DocumentTextIcon className="size-4 text-teal-600"/>
      break
    case "VIDEO_CODE_ENTRY":
      task.verbPastTense = "Watched"
      task.verb = "Watch"
      task.icon = <VideoCameraIcon className="size-4 text-teal-600"/>
      break
    case "MOODLE_COURSE":
      task.verbPastTense = "Completed"
      task.verb = "Complete"
      task.icon = <AcademicCapIcon className="size-4 text-teal-600"/>
      break
    case "EVERFI_COURSE":
      task.verbPastTense = "Completed"
      task.verb = "Complete"
      task.icon = <AcademicCapIcon className="size-4 text-teal-600"/>
      break
    case "ETHICS_COURSE":
      task.verbPastTense = "Completed"
      task.verb = "Complete"
      task.icon = <AcademicCapIcon className="size-4 text-teal-600"/>
      break
    case "ETHICS_LIVE_COURSE":
      task.verbPastTense = "Completed"
      task.verb = "Complete"
      task.icon = <AcademicCapIcon className="size-4 text-teal-600"/>
      break
  }
  return task;
}

export default function AssignmentsList() {
  const empId = useAuth().empId()
  const assignments = useTaskAssignments(empId)
  const [incompleteAssignments, setIncompleteAssignments] = useState([]);
  const [completedAssignments, setCompletedAssignments] = useState([]);

  useEffect(() => {
    setIncompleteAssignments(assignments.data?.filter(a => !a.completed) ?? [])
    setCompletedAssignments(assignments.data?.filter(a => a.completed) ?? [])
  }, [assignments.data]);

  if (assignments.isPending) {
    return <LoadingIndicator/>
  }

  return (
    <div className={"mx-[9em] mt-5 mb-2 pb-5"}>
      <span className={"text-2xl"}>Incomplete Assignments</span>
      <ul className={"my-2"}>
        {incompleteAssignments.length === 0 ?
         <li className={"p-1 box-border text-[13px] ml-8"}>
           You do not have any tasks needing attention.
         </li> :
         incompleteAssignments.map(assignment => (
           <li className={"p-1 flex box-border text-[13px] ml-8"} key={assignment.task.taskId}>
             {assignmentDisplayFields(assignment.task.taskType).icon}
             {assignment.task.taskType === "EVERFI_COURSE" ? (
               <a className="ml-1 text-teal-600 font-semibold hover:bg-gray-50"
                  href={assignment.task.url}
                  target="_blank"
                  rel="noopener noreferrer">
                 <u>Complete: {assignment.task.title}</u>
               </a>) : (
                <Link
                  to={`${assignment.taskId}`}
                  state={{
                    task: assignment.task,
                    completed: assignment.completed,
                    timestamp: isoToMediumDate(assignment.timestamp)
                  }}
                  className="ml-1 text-teal-600 font-semibold hover:bg-gray-50"
                >
                  <u>{assignmentDisplayFields(assignment.task.taskType).verb}: {assignment.task.title}</u>
                </Link>
              )}
           </li>
         ))}
      </ul>

      <span className={"text-2xl"}>Completed Assignments</span>
      <ul className={"my-2"}>
        {completedAssignments.length === 0 ?
         <li className={"p-1 box-border text-[13px] ml-8"}>
           You do not have any completed tasks.
         </li> :
         completedAssignments.map(assignment => (
           <li className={"p-1 flex box-border text-[12.5px] ml-8"} key={assignment.task.taskId}>
             <CheckIcon className={"size-4 text-teal-600"}/>
             {assignment.task.taskType === "EVERFI_COURSE" ? (
               <a className="ml-1 text-teal-600 font-normal hover:bg-gray-50 transition-opacity duration-300"
                  href={assignment.task.url}
                  target="_blank"
                  rel="noopener noreferrer">
                 <u>{assignment.task.title}</u>
                 <span className={"text-gray-400 ml-1"}>
                - {assignmentDisplayFields(assignment.task.taskType).verbPastTense} on {isoToMediumDate(assignment.timestamp)}
                 </span>
               </a>) : (
                <Link
                  to={`${assignment.taskId}`}
                  state={{
                    task: assignment.task,
                    completed: assignment.completed,
                    timestamp: isoToMediumDate(assignment.timestamp)
                  }}
                  className="ml-1 text-teal-600 font-normal hover:bg-gray-50"
                >
                  <u>{assignment.task.title}</u>
                  <span className={"text-gray-400 ml-1"}>
                    - {assignmentDisplayFields(assignment.task.taskType).verbPastTense} on {isoToMediumDate(assignment.timestamp)}
                  </span>
                </Link>
              )}
           </li>)
         )}
      </ul>
    </div>
  );
}
