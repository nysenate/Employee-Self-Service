import { useRef, useState } from "react";

/** Coordinates confirmation and commit for new, edited, and resubmitted applications.
 * The workflow supplies the current draft and renders operation-specific results.
 */
export function useApplicationSubmission({ draft, commit }) {
  const [submission, setSubmission] = useState({
    status: "idle",
    error: null,
    result: null,
  });
  // Update synchronously so repeated events cannot commit twice before a render.
  const statusRef = useRef("idle");

  function transition(status, values = {}) {
    statusRef.current = status;
    setSubmission({ status, error: null, result: null, ...values });
  }

  function requestConfirmation() {
    if (statusRef.current !== "idle" || commit.isPending || commit.isDisabled)
      return;
    transition("confirming");
  }

  function cancelConfirmation() {
    if (statusRef.current === "confirming") transition("idle");
  }

  async function confirm() {
    if (
      statusRef.current !== "confirming" ||
      commit.isPending ||
      commit.isDisabled
    )
      return;
    transition("submitting");
    try {
      const result = await commit.execute(draft);
      transition("success", { result });
    } catch (error) {
      transition("idle", { error });
    }
  }

  return {
    ...submission,
    isLocked:
      submission.status === "submitting" || submission.status === "success",
    requestConfirmation,
    cancelConfirmation,
    confirm,
  };
}
