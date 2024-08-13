import React, { useEffect, useState } from 'react';
import { loadAuth } from "app/contexts/Auth/authStorage";
import { AcademicCapIcon, CheckIcon, DocumentTextIcon, TrophyIcon, VideoCameraIcon } from "@heroicons/react/16/solid";
import { convertTimestampToLocalDateString } from "app/views/myinfo/personnel/to-do-reporting/EmployeeTaskDetails";
import { Link } from "react-router-dom";


export default function Assignments() {
  const [ completedAssignments, setCompletedAssignments ] = useState([]);
  const [ inCompletedAssignments, setInCompletedAssignments ] = useState([]);
  const empId = loadAuth().empId;
  const getTaskDetails = (type) => {
    let task = {
      taskType: "",
      link: "",
      text:"",
      icon: null
    };
    switch (type) {
      case "DOCUMENT_ACKNOWLEDGMENT":
        task.taskType = "Acknowledged"
        task.text = "Acknowledge"
        task.link = "/myinfo/personnel/todo/acknowledgment"
        break
      case "VIDEO_CODE_ENTRY":
        task.taskType = "Watched"
        task.text = "Watch"
        task.link = "/myinfo/personnel/todo/video"
        break
      case "MOODLE_COURSE":
        task.taskType = "Completed"
        task.text = "Complete"
        task.link = "/myinfo/personnel/todo/legethics"
        break
      case "EVERFI_COURSE":
        task.taskType = "Completed"
        task.text = "Complete"
        break
      case "ETHICS_COURSE":
        task.taskType = "Completed"
        task.text = "Complete"
        task.link = "/myinfo/personnel/todo/ethicscourse"
        break
      case "ETHICS_LIVE_COURSE":
        task.taskType = "Completed"
        task.text = "Complete"
        task.link = "/myinfo/personnel/todo/ethicslivecourse"
        break
    }
    return task;
  }

  useEffect(() => {
    const fetchTaskDetails = async () => {
      try {
        const init = {
          method: "GET", headers: {
            "Content-Type": "application/json", "Accept": "application/json",
          }, cache: 'no-store',
        };
        const response = await fetch(`/api/v1/personnel/task/assignment/${empId}?detail=true`, init);
        if (!response.ok) {
          throw new Error('Failed to fetch data');
        }
        const data = await response.json();
        setCompletedAssignments(data.assignments
          .filter(assignment => assignment.completed && assignment.active)
          .sort((a, b) => a.taskId - b.taskId));
        setInCompletedAssignments(data.assignments
          .filter(assignment => !assignment.completed && assignment.active)
          .sort((a, b) => b.taskId - a.taskId));
        console.log(data.assignments);
      } catch (error) {
        console.error('Error fetching task details:', error);
      }
    };
    fetchTaskDetails();
  }, []);

  return (
    <div className={"mx-[9em] mt-5 mb-2 pb-5"}>
      <span className={"text-2xl"}>Incompleted Assignments</span>
      <ul className={"my-2"}>
        {inCompletedAssignments.length === 0 ?
         <li className={"p-1 box-border text-[13px] ml-8"}>
           You do not have any tasks needing attention.
         </li> :
         inCompletedAssignments.map(assignment => (
           <li className={"p-1 flex box-border text-[13px] ml-8"} key={assignment.task.taskId}>
             {assignment.task.taskType === "VIDEO_CODE_ENTRY" ?
              (<VideoCameraIcon className={"size-5 text-teal-600"}/>) :
              assignment.task.taskType === "DOCUMENT_ACKNOWLEDGMENT"?
              (<DocumentTextIcon className={"size-5 text-teal-600"} />):
              (<AcademicCapIcon className={"size-5 text-teal-600"}/>)
             }
             {assignment.task.taskType === "EVERFI_COURSE" ? (
               <a className="ml-1 text-teal-600 font-semibold hover:bg-gray-50"
                  href={assignment.task.url}
                  target="_blank"
                  rel="noopener noreferrer">
                 <u>Complete: {assignment.task.title}</u>
               </a>) : (
                <Link
                  to={getTaskDetails(assignment.task.taskType).link + `/${assignment.taskId}`}
                  state={{
                    task: assignment.task,
                    completed: assignment.completed,
                    timestamp: convertTimestampToLocalDateString(assignment.timestamp)
                  }}
                  className="ml-1 text-teal-600 font-semibold hover:bg-gray-50"
                >
                  <u>{getTaskDetails(assignment.task.taskType).text}: {assignment.task.title}</u>
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
             <TrophyIcon className={"size-3.5 text-teal-600"}/>
             {assignment.task.taskType === "EVERFI_COURSE" ? (
               <a className="ml-1 text-teal-600 font-normal hover:bg-gray-50 transition-opacity duration-300"
                  href={assignment.task.url}
                  target="_blank"
                  rel="noopener noreferrer">
                 <u>{assignment.task.title}</u>
                 <span className={"text-gray-400 ml-1"}>
                - {getTaskDetails(assignment.task.taskType).taskType} on {convertTimestampToLocalDateString(assignment.timestamp)}
                 </span>
               </a>) : (
                <Link
                  to={getTaskDetails(assignment.task.taskType).link + `/${assignment.taskId}`}
                  state={{
                    task: assignment.task,
                    completed: assignment.completed,
                    timestamp: convertTimestampToLocalDateString(assignment.timestamp)
                  }}
                  className="ml-1 text-teal-600 font-normal hover:bg-gray-50"
                >
                  <u>{assignment.task.title}</u>
                  <span className={"text-gray-400 ml-1"}>
                - {getTaskDetails(assignment.task.taskType).taskType} on {convertTimestampToLocalDateString(assignment.timestamp)}
              </span>
                </Link>
              )}
           </li>)
         )}
      </ul>
    </div>
  );
}



