import Modal from "app/components/Modal";
import Button from "app/components/Button";
import React from "react";

/**
 * A simple modal for displaying a small confirmation message.
 * @param {boolean} isOpen
 * @param {() => void} onClose
 * @param {React.ReactNode} title
 * @param {React.ReactNode} body
 * @param {boolean} [isDismissable=false] Whether click-away/Escape can dismiss the notice.
 * @returns {Element}
 */
export default function ModalNotice({
  isOpen,
  onClose,
  title,
  body,
  isDismissable = false,
}) {
  if (process.env.NODE_ENV !== "production" && typeof onClose !== "function") {
    throw new Error("[ModalNotice] `onClose` callback is required.");
  }

  const handleOpenChange = (open) => {
    if (!open) {
      onClose();
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onOpenChange={handleOpenChange}
      isDismissable={isDismissable}
    >
      <Modal.Title>{title}</Modal.Title>
      <Modal.Body>
        <div className="max-w-lg">{body}</div>
      </Modal.Body>
      <Modal.Buttons>
        <Button variant="theme" onPress={onClose}>
          Okay
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
