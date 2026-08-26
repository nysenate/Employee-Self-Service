import React from "react";
import Button from "app/components/Button";
import Modal from "app/components/Modal";

export default function OutsideConusModal({ isOpen, onClose }) {
  return (
    <Modal
      isOpen={isOpen}
      onOpenChange={(open) => !open && onClose()}
      className="max-w-4xl"
    >
      <Modal.Title>
        Your destination is outside the continental U.S.
      </Modal.Title>
      <Modal.Body>
        <p className="mb-3">
          Currently, ESS Travel can only calculate housing and other costs for
          the 48 continental U.S. states. If you are traveling outside of these
          states, please submit a paper Request for Travel Approval:
        </p>
        <ul className="list-disc space-y-1 pl-8">
          <li>
            A printable PDF Request for Travel Approval is available{" "}
            <ExternalLink href="https://my.nysenate.gov/department/secretary-senate/travel">
              here
            </ExternalLink>
            .
          </li>
          <li>
            Non-continental U.S. per diems can be looked up{" "}
            <ExternalLink href="https://www.travel.dod.mil/Travel-Transportation-Rates/Per-Diem/Per-Diem-Rate-Lookup/">
              here
            </ExternalLink>
            .
          </li>
        </ul>
      </Modal.Body>
      <Modal.Buttons>
        <Button onPress={onClose}>Okay</Button>
      </Modal.Buttons>
    </Modal>
  );
}

function ExternalLink({ href, children }) {
  return (
    <a href={href} target="_blank" rel="noopener noreferrer">
      {children}
    </a>
  );
}
