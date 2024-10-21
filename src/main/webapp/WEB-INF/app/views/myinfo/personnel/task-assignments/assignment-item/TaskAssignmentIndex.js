import React from 'react';
import { useParams } from "react-router-dom";
import useAuth from "app/contexts/Auth/useAuth";
import { useTaskAssignment } from "app/api/taskAssignmentApi";
import LoadingIndicator from "app/components/LoadingIndicator";
import DocumentAcknowledgeAssignment
  from "app/views/myinfo/personnel/task-assignments/assignment-item/document-assignment/DocumentAcknowledgeAssignment";
import VideoAssignment
  from "app/views/myinfo/personnel/task-assignments/assignment-item/video-assignment/VideoAssignment";
import MoodleAssignment
  from "app/views/myinfo/personnel/task-assignments/assignment-item/moodle-assignment/MoodleAssignment";
import EthicsCourseAssignment
  from "app/views/myinfo/personnel/task-assignments/assignment-item/ethics-course-assignment/EthicsCourseAssignment";


export default function TaskAssignmentIndex() {
  const auth = useAuth()
  let { taskId } = useParams()
  taskId = Number(taskId)
  const { data: assignment, isLoading } = useTaskAssignment(auth.empId(), taskId)


  if (isLoading) {
    return <LoadingIndicator/>
  }

  switch (assignment.task.taskType) {
    case "DOCUMENT_ACKNOWLEDGMENT":
      return <DocumentAcknowledgeAssignment assignment={assignment}/>
    case "VIDEO_CODE_ENTRY":
      return <VideoAssignment assignment={assignment}/>
    case "MOODLE_COURSE":
      return <MoodleAssignment assignment={assignment}/>
    case "EVERFI_COURSE":
      window.location.href = assignment.task.url
      return <></>
    case "ETHICS_COURSE":
      return <EthicsCourseAssignment assignment={assignment}/>
    case "ETHICS_LIVE_COURSE":
  }

  return (
    <div>
      Unknown Task assignment item index
    </div>
  )
}