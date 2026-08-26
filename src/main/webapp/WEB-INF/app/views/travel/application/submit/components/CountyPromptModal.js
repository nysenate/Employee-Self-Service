import React, { useEffect, useState } from "react";
import Button from "app/components/Button";
import Modal from "app/components/Modal";

export default function CountyPromptModal({ pending, onSubmit, onCancel }) {
  const [county, setCounty] = useState("");

  useEffect(() => setCounty(""), [pending]);

  return (
    <Modal
      isOpen={Boolean(pending)}
      onOpenChange={(open) => !open && onCancel()}
      className="max-w-md"
    >
      <Modal.Title>Enter county</Modal.Title>
      <Modal.Body>
        <p className="mb-3 text-sm text-gray-600">
          The county could not be determined for {pending?.addressText}. Enter
          it to continue.
        </p>
        <label htmlFor="missing-county" className="mb-1 block font-medium">
          County
        </label>
        <input
          id="missing-county"
          className="input w-full"
          value={county}
          onChange={(event) => setCounty(event.target.value)}
        />
      </Modal.Body>
      <Modal.Buttons>
        <Button variant="secondary" onPress={onCancel}>
          Cancel
        </Button>
        <Button
          isDisabled={!county.trim()}
          onPress={() => onSubmit(county.trim())}
        >
          Save county
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
