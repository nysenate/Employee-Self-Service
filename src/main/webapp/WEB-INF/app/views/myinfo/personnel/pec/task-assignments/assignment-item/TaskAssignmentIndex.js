import React from "react";
import { useParams } from "react-router-dom";
import LoadingIndicator from "app/components/LoadingIndicator";
import DocumentAcknowledgeAssignment from "app/views/myinfo/personnel/pec/task-assignments/assignment-item/document-assignment/DocumentAcknowledgeAssignment";
import VideoAssignment from "app/views/myinfo/personnel/pec/task-assignments/assignment-item/video-assignment/VideoAssignment";
import MoodleAssignment from "app/views/myinfo/personnel/pec/task-assignments/assignment-item/moodle-assignment/MoodleAssignment";
import EthicsCourseAssignment from "app/views/myinfo/personnel/pec/task-assignments/assignment-item/ethics-course-assignment/EthicsCourseAssignment";
import EthicsLiveAssignment from "app/views/myinfo/personnel/pec/task-assignments/assignment-item/ethics-live-assignment/EthicsLiveAssignment";
import { useTaskAssignment } from "app/views/myinfo/personnel/pec/useTaskAssignment";
import useAuthedUser from "app/core/useAuthedUser";

export default function TaskAssignmentIndex() {
  const { data: user } = useAuthedUser();
  let { taskId } = useParams();
  taskId = Number(taskId);
  const { data: assignment, isLoading } = useTaskAssignment(
    user?.employeeId,
    taskId,
  );

  if (isLoading) {
    return <LoadingIndicator />;
  }

  switch (assignment.task.taskType) {
    case "DOCUMENT_ACKNOWLEDGMENT":
      return <DocumentAcknowledgeAssignment assignment={assignment} />;
    case "VIDEO_CODE_ENTRY":
      return <VideoAssignment assignment={assignment} />;
    case "MOODLE_COURSE":
      return <MoodleAssignment assignment={assignment} />;
    case "EVERFI_COURSE":
      window.location.href = assignment.task.url;
      return <></>;
    case "ETHICS_COURSE":
      return <EthicsCourseAssignment assignment={assignment} />;
    case "ETHICS_LIVE_COURSE":
      return <EthicsLiveAssignment assignment={assignment} />;
  }

  return <div>Unknown Task assignment item index</div>;
}
