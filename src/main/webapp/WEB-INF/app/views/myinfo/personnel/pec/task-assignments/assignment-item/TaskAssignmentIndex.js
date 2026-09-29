import ErrorAlert from "app/components/ErrorAlert";
import React from "react";
import { useParams } from "react-router-dom";
import LoadingStatus from "app/components/LoadingStatus";
import DocumentAcknowledgeAssignment from "app/views/myinfo/personnel/pec/task-assignments/assignment-item/document-assignment/DocumentAcknowledgeAssignment";
import VideoAssignment from "app/views/myinfo/personnel/pec/task-assignments/assignment-item/video-assignment/VideoAssignment";
import MoodleAssignment from "app/views/myinfo/personnel/pec/task-assignments/assignment-item/moodle-assignment/MoodleAssignment";
import EthicsCourseAssignment from "app/views/myinfo/personnel/pec/task-assignments/assignment-item/ethics-course-assignment/EthicsCourseAssignment";
import EthicsLiveAssignment from "app/views/myinfo/personnel/pec/task-assignments/assignment-item/ethics-live-assignment/EthicsLiveAssignment";
import { useTaskAssignment } from "app/views/myinfo/personnel/pec/useTaskAssignment";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";

export default function TaskAssignmentIndex() {
  const { data: user, isPending: isUserPending } = useRequireAuthedUser();
  const { taskId: taskIdParam } = useParams();
  const taskId = Number(taskIdParam);
  const assignmentQuery = useTaskAssignment(user?.employeeId, taskId);
  const { data: assignment, isPending: isAssignmentPending } = assignmentQuery;

  if (!Number.isFinite(taskId))
    return <ErrorAlert title="Invalid assignment" />;

  if (isUserPending || isAssignmentPending) {
    return (
      <LoadingStatus
        message="Loading assignment…"
        layout="centered"
        size="lg"
        className="min-h-48 p-6"
      />
    );
  }

  if (!assignment) return <ErrorAlert title="Assignment not found" />;

  return (
    <>
      {assignmentQuery.isFetching && (
        <LoadingStatus message="Refreshing assignment…" />
      )}
      {assignmentQuery.isError && (
        <ErrorAlert title="Unable to refresh assignment">
          Please try again.
        </ErrorAlert>
      )}
      <AssignmentContent assignment={assignment} />
    </>
  );
}

function AssignmentContent({ assignment }) {
  switch (assignment.task.taskType) {
    case "DOCUMENT_ACKNOWLEDGMENT":
      return <DocumentAcknowledgeAssignment assignment={assignment} />;
    case "VIDEO_CODE_ENTRY":
      return <VideoAssignment assignment={assignment} />;
    case "MOODLE_COURSE":
      return <MoodleAssignment assignment={assignment} />;
    case "EVERFI_COURSE":
      window.location.href = assignment.task.url;
      return (
        <LoadingStatus
          message="Opening training…"
          layout="centered"
          className="min-h-48 p-6"
        />
      );
    case "ETHICS_COURSE":
      return <EthicsCourseAssignment assignment={assignment} />;
    case "ETHICS_LIVE_COURSE":
      return <EthicsLiveAssignment assignment={assignment} />;
  }

  return <ErrorAlert title="Unsupported assignment type" />;
}
