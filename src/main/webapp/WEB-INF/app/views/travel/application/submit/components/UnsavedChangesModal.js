import React from "react";
import Button from "app/components/Button";
import Modal from "app/components/Modal";

export default function UnsavedChangesModal({ guard }) {
  return (
    <Modal
      isOpen={guard.isOpen}
      onOpenChange={(isOpen) => !isOpen && guard.stay()}
      className="max-w-lg"
    >
      <Modal.Title>Leave this application?</Modal.Title>
      <Modal.Body>
        You have unsaved changes. If you leave now, those changes will be lost.
      </Modal.Body>
      <Modal.Buttons>
        <Button variant="secondary" onPress={guard.stay}>
          Stay on application
        </Button>
        <Button variant="destructive" onPress={guard.leave}>
          Leave application
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
