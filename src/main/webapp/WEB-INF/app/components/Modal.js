import React from "react"
import { Dialog } from '@headlessui/react'


/**
 * Displays a modal dialog.
 *
 * @param {boolean} isOpen - The modal is displayed when this is true. To close the modal, set to false.
 * @param {function()} onSoftReject - A function to call when the user clicks outside the modal or presses escape.
 * Not setting this will force the user to interact with the modal. Given function should set isOpen to false.
 * @param {JSX.Element} children - The content of the modal.
 * @returns {JSX.Element}
 */
function Modal({ isOpen, onSoftReject = () => undefined, children }) {
  return (
    <Dialog open={isOpen} onClose={onSoftReject}
            className="relative z-50">
      <div className="fixed inset-0 bg-black/60" aria-hidden="true"/>
      <div className="fixed inset-0 flex items-center justify-center p-4">
        <Dialog.Panel className="w-auto max-w-screen-xl bg-white">
          {children}
        </Dialog.Panel>
      </div>
    </Dialog>
  )
}

const Title = ({ children }) => (
  <Dialog.Title className="py-3 px-4 font-semibold text-center border-b-1 border-teal-200">
    {children}
  </Dialog.Title>
)

const Body = ({ children }) => (
  <div className="m-4">
    {children}
  </div>
)

const Controls = ({ children }) => (
  <div className="p-3 border-t-1 border-teal-200">
    {children}
  </div>
)

/**
 * Similar to Controls, but adds additional styling to center buttons.
 * Use this for a simple button layout, use Controls for a more customizable layout.
 */
const Buttons = ({ children }) => (
  <Controls>
    <div className="flex justify-center gap-3">
      {children}
    </div>
  </Controls>
)

Modal.Title = Title
Modal.Body = Body
Modal.Controls = Controls
Modal.Buttons = Buttons

export default Modal