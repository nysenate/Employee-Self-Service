import React from "react";
import Button from "app/components/Button";
import Modal from "app/components/Modal";

export default function CancelEditsModal({
  isOpen,
  onKeepEditing,
  onDiscard,
  isDiscardDisabled = false,
}) {
  return (
    <Modal
      isOpen={isOpen}
      onOpenChange={(open) => !open && onKeepEditing()}
      className="max-w-lg"
    >
      <Modal.Title>Cancel editing this travel application?</Modal.Title>
      <Modal.Body>Any changes you have made will be lost.</Modal.Body>
      <Modal.Buttons>
        <Button variant="secondary" onPress={onKeepEditing}>
          Keep editing
        </Button>
        <Button
          variant="destructive"
          onPress={onDiscard}
          isDisabled={isDiscardDisabled}
        >
          Discard edits
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
