import Modal from "app/components/Modal";
import Button from "app/components/Button";
import React from "react";

/**
 * A simple modal for displaying a small confirmation message.
 * @param isOpen
 * @param onResolve
 * @param title
 * @param body
 * @returns {Element}
 * @constructor
 */
export default function ModalNotice({ isOpen, onResolve, title, body }) {
  return (
    <Modal isOpen={isOpen}>
      <Modal.Title>{title}</Modal.Title>
      <Modal.Body>
        <div className="max-w-lg">{body}</div>
      </Modal.Body>
      <Modal.Buttons>
        <Button color="theme" onClick={onResolve}>
          Okay
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
