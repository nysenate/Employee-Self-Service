import React from "react";
import { Fragment } from "react";
import { Dialog, Transition } from "@headlessui/react";
import NiceModal, { useModal } from "@ebay/nice-modal-react";

/**
 * Displays a modal dialog.
 *
 * @param {boolean} isOpen - The modal is displayed when this is true. To close the modal, set to false.
 * @param {boolean} allowSoftDismiss - Should clicking outside the modal or pressing Esc key close the modal.
 * @param {JSX.Element} children - The content of the modal.
 * @returns {JSX.Element}
 */
const TestModal = ({ allowSoftDismiss = true, children }) => {
  const modal = useModal();

  if (!modal || typeof modal.show !== "function") {
    throw new Error(
      "TestModal must be used inside a NiceModal.create() context",
    );
  }

  return (
    <Transition
      appear
      show={modal.visible}
      as={Fragment}
      afterLeave={() => modal.remove()}
    >
      <Dialog
        open={modal.visible}
        onClose={allowSoftDismiss ? () => modal.hide() : () => undefined}
        transition
        className="relative z-50 transition duration-300 ease-in"
      >
        <div className="fixed inset-0 bg-black/60" aria-hidden="true" />
        <div className="fixed inset-0 flex items-center justify-center p-4">
          <Dialog.Panel className="w-auto max-w-screen-xl bg-white">
            {children}
          </Dialog.Panel>
        </div>
      </Dialog>
    </Transition>
  );
};

const Title = ({ children }) => (
  <Dialog.Title className="border-b-1 border-teal-200 px-4 py-3 text-center font-semibold">
    {children}
  </Dialog.Title>
);

const Body = ({ children }) => <div className="m-4">{children}</div>;

const Controls = ({ children }) => (
  <div className="border-t-1 border-teal-200 p-3">{children}</div>
);

/**
 * Similar to Controls, but adds additional styling to center buttons.
 * Use this for a simple button layout, use Controls for a more customizable layout.
 */
const Buttons = ({ children }) => (
  <Controls>
    <div className="flex justify-center gap-3">{children}</div>
  </Controls>
);

TestModal.Title = Title;
TestModal.Body = Body;
TestModal.Controls = Controls;
TestModal.Buttons = Buttons;

export default TestModal;
