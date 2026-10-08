import React from "react";
import Button from "app/components/Button";
import Modal from "app/components/Modal";

export default function CancelApplicationDialog({
  canCancel,
  mutation,
  onClose,
  onConfirm,
}) {
  return (
    <Modal
      isOpen
      isDismissable={!mutation.isPending}
      onOpenChange={(open) => !open && !mutation.isPending && onClose()}
    >
      <Modal.Title>Cancel Travel Application</Modal.Title>
      <Modal.Body>
        <p>Are you sure you want to cancel this application?</p>
        {mutation.isError && (
          <p role="alert" className="mt-3 text-red-700">
            We couldn’t confirm cancellation. Please close this dialog and
            reopen the application to check its status before trying again.
          </p>
        )}
      </Modal.Body>
      <Modal.Buttons>
        <Button
          variant="destructive"
          isPending={mutation.isPending}
          isDisabled={!canCancel || mutation.isError}
          onPress={onConfirm}
        >
          Yes
        </Button>
        <Button
          variant="secondary"
          isDisabled={mutation.isPending}
          onPress={onClose}
        >
          No
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
