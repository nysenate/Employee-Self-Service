import React from "react";
import Button from "app/components/Button";
import Modal from "app/components/Modal";
import LoadingStatus from "app/components/LoadingStatus";

export function ApplicationSubmissionDialog({
  isOpen,
  onCancel,
  onConfirm,
  title = "Submit travel application?",
  body = "Once submitted, this application will be sent for review and can no longer be edited.",
  actionLabel = "Submit application",
  isPending = false,
  pendingText = "Submitting your travel application…",
  error = null,
}) {
  return (
    <Modal
      isOpen={isOpen}
      onOpenChange={(open) => !open && !isPending && onCancel()}
      isDismissable={!isPending}
      className="max-w-lg"
    >
      <Modal.Title>
        {isPending ? pendingText : error ? "Action was not confirmed" : title}
      </Modal.Title>
      <Modal.Body>
        {isPending ? (
          <LoadingStatus
            message="Please keep this page open while we confirm your changes."
            size="lg"
            className="py-4"
            indicatorClassName="text-orange-700"
          />
        ) : (
          error || body
        )}
      </Modal.Body>
      {!isPending &&
        (error ? (
          <Modal.Buttons>
            <Button variant="secondary" onPress={onCancel}>
              Return to application
            </Button>
          </Modal.Buttons>
        ) : (
          <Modal.Buttons>
            <Button variant="secondary" onPress={onCancel}>
              Cancel
            </Button>
            <Button onPress={onConfirm}>{actionLabel}</Button>
          </Modal.Buttons>
        ))}
    </Modal>
  );
}

export function SubmissionSuccessModal({
  isOpen,
  onReturn,
  onLogout,
  title = "Application submitted",
  body = "Your travel application was submitted successfully. What would you like to do next?",
}) {
  return (
    <Modal isOpen={isOpen} isDismissable={false} className="max-w-lg">
      <Modal.Title>{title}</Modal.Title>
      <Modal.Body>{body}</Modal.Body>
      <Modal.Buttons>
        <Button onPress={onReturn}>Go back to ESS</Button>
        <Button variant="secondary" onPress={onLogout}>
          Log out of ESS
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
