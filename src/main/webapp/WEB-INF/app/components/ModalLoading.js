import React from "react";
import LoadingIndicator from "app/components/LoadingIndicator";
import Modal from "app/components/Modal";

// TODO WIP
export default function ModalLoading({ isOpen }) {
  return (
    <Modal
      isOpen={isOpen}
      ariaLabel="Loading"
      className="w-auto max-w-lg bg-neutral-400"
    >
      <div className="px-36 py-4">
        <LoadingIndicator />
      </div>
    </Modal>
  );
}
