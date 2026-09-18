import React from "react";
import Button from "app/components/Button";
import Modal from "app/components/Modal";

export function SubmissionConfirmationModal({
  isOpen,
  onCancel,
  onConfirm,
  title = "Submit travel application?",
  body = "Once submitted, this application will be sent for review and can no longer be edited.",
  actionLabel = "Submit application",
}) {
  return (
    <Modal
      isOpen={isOpen}
      onOpenChange={(open) => !open && onCancel()}
      className="max-w-lg"
    >
      <Modal.Title>{title}</Modal.Title>
      <Modal.Body>{body}</Modal.Body>
      <Modal.Buttons>
        <Button variant="secondary" onPress={onCancel}>
          Cancel
        </Button>
        <Button onPress={onConfirm}>{actionLabel}</Button>
      </Modal.Buttons>
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
