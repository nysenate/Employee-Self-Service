import React from "react";
import LoadingIndicator from "app/components/LoadingIndicator";
import { Dialog } from "@headlessui/react";

// TODO WIP
export default function ModalLoading({ isOpen }) {
  return (
    <Dialog open={isOpen} onClose={() => undefined} className="relative z-50">
      <div className="fixed inset-0 bg-black/60" aria-hidden="true" />
      <div className="fixed inset-0 flex items-center justify-center p-4">
        <Dialog.Panel className="w-auto max-w-lg bg-neutral-400">
          <div className="px-36">
            <LoadingIndicator />
          </div>
        </Dialog.Panel>
      </div>
    </Dialog>
  );
}
