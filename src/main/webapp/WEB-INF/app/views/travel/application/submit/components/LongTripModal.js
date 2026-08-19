import React from "react";
import Button from "app/components/Button";
import Modal from "app/components/Modal";

export default function LongTripModal({ isOpen, onConfirm, onReview }) {
  return (
    <Modal isOpen={isOpen} onOpenChange={(open) => !open && onReview()}>
      <Modal.Title>Confirm your travel dates</Modal.Title>
      <Modal.Body>
        This trip is longer than seven days. Are the departure and return dates
        correct?
      </Modal.Body>
      <Modal.Buttons>
        <Button variant="secondary" onPress={onReview}>
          Review dates
        </Button>
        <Button onPress={onConfirm}>Dates are correct</Button>
      </Modal.Buttons>
    </Modal>
  );
}
