import React from "react"
import { Dialog } from '@headlessui/react'


/**
 * Displays a modal dialog.
 *
 * @param {boolean} isOpen - The modal is displayed when this is true.
 * @param {function()} onClose - A function to call when the modal is closed. The modal is closed when the user
 * clicks outside it or presses the Escape key.
 * @param {string=} title - An optional title for the modal dialog.
 * @param {JSX.Element} children - The content of the modal.
 * @returns {JSX.Element}
 */
export default function Modal({ isOpen, onClose, title, children }) {
  return (
    <Dialog open={isOpen} onClose={onClose}
            className="relative z-50">
      <div className="fixed inset-0 bg-black/60" aria-hidden="true"/>
      <div className="fixed inset-0 flex items-center justify-center p-4">
        <Dialog.Panel className="w-auto max-w-screen-xl bg-white">
          {title &&
            <Dialog.Title className="p-2 font-semibold text-center border-b-1 border-teal-200">{title}</Dialog.Title>
          }
          <div className="m-2">
            {children}
          </div>
        </Dialog.Panel>
      </div>
    </Dialog>
  )
}
