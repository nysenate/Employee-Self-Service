import React from "react";
import {
  Navigate,
  useLocation,
  useNavigate,
  useParams,
} from "react-router-dom";
import Button from "app/components/Button";
import ErrorAlert from "app/components/ErrorAlert";
import Hero from "app/components/Hero";
import LoadingStatus from "app/components/LoadingStatus";
import { useNotifySuccess } from "app/components/NotificationProvider";
import { useUserTravelRoles } from "app/views/travel/shared/hooks/useUserTravelRoles";
import { canAdminEditTravel } from "app/views/travel/shared/travelRoles";
import { useApplicationEditSession } from "../workflow/hooks/useApplicationEditSession";
import { parseApplicationId } from "../workflow/applicationRoutes";
import { ADMIN_EDIT_STEPS } from "../workflow/workflowSteps";
import TravelApplicationWorkflow from "../workflow/TravelApplicationWorkflow";
import { normalizeReviewReturnTo } from "./editRoutes";
import { useEditTravelApplication } from "./useEditTravelApplication";

const PRESENTATION = {
  finalActionLabel: "Save Edits",
  confirmationTitle: "Save travel application edits?",
  confirmationBody:
    "Your changes will update this application without restarting review or sending email notifications.",
  confirmationActionLabel: "Save Edits",
  pendingText: "Saving travel application edits…",
};

export default function EditTravelApplication() {
  const { appId: rawId } = useParams();
  const { data: roles, isPending } = useUserTravelRoles();
  const appId = parseApplicationId(rawId);
  return (
    <div className="space-y-5">
      <Hero>Edit Travel Application</Hero>
      {isPending ? (
        <LoadingStatus
          message="Loading your travel application…"
          layout="centered"
          size="lg"
          className="min-h-48 p-6"
        />
      ) : !canAdminEditTravel(roles) ? (
        <ErrorAlert title="Access is unavailable">
          Only Travel Admins can edit travel applications.
        </ErrorAlert>
      ) : appId === null ? (
        <ErrorAlert title="Travel application unavailable">
          The application address is invalid.
        </ErrorAlert>
      ) : (
        <Editor key={appId} appId={appId} />
      )}
    </div>
  );
}

function Editor({ appId }) {
  const notifySuccess = useNotifySuccess();
  const location = useLocation();
  const navigate = useNavigate();
  const returnTo = normalizeReviewReturnTo(location.state?.returnTo);
  const returnState = location.state?.reviewContext;
  const session = useApplicationEditSession({ appId, operation: "edit" });
  const edit = useEditTravelApplication(appId);
  const leave = () => navigate(returnTo, { replace: true, state: returnState });
  if (session.isPending)
    return (
      <LoadingStatus
        message="Loading your travel application…"
        layout="centered"
        size="lg"
        className="min-h-48 p-6"
      />
    );
  if (session.isError || !session.data?.draft || !session.data?.application) {
    const status = session.error?.response?.status;
    const message =
      status === 401 || status === 403
        ? "You do not have permission to edit this application."
        : status === 404
          ? "The requested application could not be found."
          : session.error?.data?.errorCode === "MISSING_DEPARTMENT"
            ? "ESS could not determine the traveler’s department. Contact your personnel office."
            : "The application could not be loaded. Please try again.";
    return (
      <ErrorAlert title="Travel application unavailable">
        <p>{message}</p>
        <div className="mt-4 flex gap-3">
          <Button onPress={() => session.refetch()}>Try again</Button>
          <Button variant="secondary" onPress={leave}>
            Return to reviews
          </Button>
        </div>
      </ErrorAlert>
    );
  }
  return (
    <TravelApplicationWorkflow
      initialDraft={session.data.draft}
      application={session.data.application}
      steps={ADMIN_EDIT_STEPS}
      presentation={PRESENTATION}
      onCancel={leave}
      commit={{
        execute: async (draft) => {
          const result = await edit.mutateAsync(draft);
          notifySuccess(`Changes saved to application #${appId}`);
          return result;
        },
        isPending: edit.isPending,
        isDisabled: [401, 403].includes(edit.error?.response?.status),
        renderError: (error) => (
          <ErrorAlert title="Edits were not confirmed">
            {[401, 403].includes(error?.response?.status)
              ? "You no longer have permission to save these edits. Your entered changes are still available."
              : "We could not confirm that your edits were saved. Your entered changes are still available. You can retry saving or return to reviews to check the application."}
          </ErrorAlert>
        ),
      }}
      renderCompletion={() => (
        <Navigate to={returnTo} replace state={returnState} />
      )}
    />
  );
}
