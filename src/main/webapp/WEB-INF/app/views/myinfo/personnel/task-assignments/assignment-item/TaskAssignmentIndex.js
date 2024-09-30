import React from 'react';
import { useParams } from "react-router-dom";
import useAuth from "app/contexts/Auth/useAuth";
import { useTaskAssignment } from "app/api/taskAssignmentApi";
import LoadingIndicator from "app/components/LoadingIndicator";
import DocumentAcknowledgeAssignment
  from "app/views/myinfo/personnel/task-assignments/assignment-item/DocumentAcknowledgeAssignment";


export default function TaskAssignmentIndex() {
  const auth = useAuth()
  const { taskId } = useParams()
  const { data: assignment, isLoading } = useTaskAssignment(auth.empId(), taskId)


  if (isLoading) {
    return <LoadingIndicator/>
  }

  switch (assignment.task.taskType) {
    case "DOCUMENT_ACKNOWLEDGMENT":
      return <DocumentAcknowledgeAssignment assignment={assignment}/>
    case "VIDEO_CODE_ENTRY":

    case "MOODLE_COURSE":

    case "EVERFI_COURSE":

    case "ETHICS_COURSE":

    case "ETHICS_LIVE_COURSE":
  }

  return (
    <div>
      Unknown Task assignment item index
    </div>
  )
}