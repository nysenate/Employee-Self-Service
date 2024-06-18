import React from "react";
import { Dialog } from '@headlessui/react';

/**
 * Displays a modal dialog.
 *
 * @param {boolean} isLocked - If true, clicking outside the modal will not close it.
 * @param {boolean} isOpen - The modal is displayed when this is true.
 * @param {function()} onClose - A function to call when the modal is closed. The modal is closed when the user
 * clicks outside it or presses the Escape key.
 * @param {string=} title - An optional title for the modal dialog.
 * @param {JSX.Element} children - The content of the modal.
 * @returns {JSX.Element}
 */
export default function Popup({ isLocked, isOpen, onClose, title, children }) {
  // Function to handle dialog close only if not locked
  const handleClose = () => {
    if (!isLocked) {
      onClose();
    }
  };

  return (
    <Dialog open={isOpen} onClose={handleClose} className="relative z-50">
      <div
        className="fixed inset-0 bg-black/60"
        aria-hidden="true"
        onClick={handleClose}
      />
      <div className="fixed inset-0 flex items-center justify-center p-4">
        <Dialog.Panel
          className="w-auto max-w-screen-xl bg-white"
          onClick={(e) => e.stopPropagation()} // Prevent clicks inside the modal from propagating
        >
          {title && (
            <Dialog.Title className="p-2 font-semibold text-center border-b-1 border-teal-200">
              {title}
            </Dialog.Title>
          )}
          <div className="m-2">
            {children}
          </div>
        </Dialog.Panel>
      </div>
    </Dialog>
  );
}
