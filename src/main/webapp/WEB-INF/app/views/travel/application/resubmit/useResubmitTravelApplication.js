import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { fetchApiJson } from "app/api/fetchJson";
import { refreshTravelAfterResubmission } from "./resubmissionCache";

export function useResubmitTravelApplication(appId) {
  const queryClient = useQueryClient();
  const [recovery, setRecovery] = useState({ type: "idle" });

  const mutation = useMutation({
    mutationFn: async (draft) => {
      setRecovery({ type: "idle" });
      try {
        const acknowledgement = await fetchApiJson(
          `/travel/application/edit/resubmit/${appId}`,
          {
            method: "POST",
            payload: draft,
          },
        );
        if (acknowledgement?.success !== true) {
          throw new ResubmissionOutcomeUnknownError();
        }
        return acknowledgement;
      } catch (error) {
        setRecovery({ type: classifyResubmissionError(error) });
        throw error;
      }
    },
    retry: false,
    onSuccess: () => refreshTravelAfterResubmission(queryClient),
  });

  const statusCheck = useMutation({
    mutationFn: async () => {
      try {
        const body = await fetchApiJson(`/travel/applications/${appId}`);
        const status = body.result?.status;
        if (typeof status?.isDisapproved !== "boolean") {
          throw new Error("The application status response was invalid.");
        }
        if (status.isDisapproved) {
          setRecovery({ type: "retryable-status" });
        } else {
          setRecovery({ type: "status-changed" });
        }
        return body;
      } catch (error) {
        setRecovery({ type: "status-check-failed" });
        throw error;
      }
    },
    retry: false,
  });

  return {
    ...mutation,
    recovery,
    isCommitBlocked: isCommitBlocked(recovery.type),
    checkApplicationStatus: async () => {
      try {
        return await statusCheck.mutateAsync();
      } catch {
        return undefined;
      }
    },
    isStatusCheckPending: statusCheck.isPending,
  };
}

export function classifyResubmissionError(error) {
  const status = error?.response?.status;
  const errorCode = error?.data?.errorCode;

  if (status === 409 || errorCode === "TRAVEL_RESUBMISSION_CONFLICT") {
    return "conflict";
  }
  if (status === 401 || status === 403) {
    return "access";
  }
  if (status === 400 || status === 422) {
    return "correction";
  }
  return "unknown-outcome";
}

function isCommitBlocked(recoveryType) {
  return [
    "conflict",
    "access",
    "unknown-outcome",
    "status-changed",
    "status-check-failed",
  ].includes(recoveryType);
}

export class ResubmissionOutcomeUnknownError extends Error {
  constructor() {
    super("The resubmission acknowledgement was missing or invalid.");
    this.name = "ResubmissionOutcomeUnknownError";
  }
}
