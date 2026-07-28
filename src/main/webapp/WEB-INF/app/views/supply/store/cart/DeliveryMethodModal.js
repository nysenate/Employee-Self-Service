import Modal from "app/components/Modal";
import Button from "app/components/Button";
import React from "react";

export default function DeliveryMethodModal({ isOpen, setIsOpen, onResolve }) {
  const onPickup = () => {
    setIsOpen(false);
    onResolve("PICKUP");
  };

  const onDelivery = () => {
    setIsOpen(false);
    onResolve("DELIVERY");
  };

  return (
    <Modal isOpen={isOpen} onOpenChange={setIsOpen}>
      <Modal.Title>Choose Delivery Method</Modal.Title>
      <Modal.Body>
        <span className="font-semibold">
          Would you like to pick up this order or have it delivered to you?
        </span>
        <br />
        <br />
        Note: Orders can be picked up from L212
      </Modal.Body>
      <Modal.Buttons>
        <Button variant="primary" onPress={onPickup}>
          I'll pick it up
        </Button>
        <Button variant="secondary" onPress={onDelivery}>
          Please deliver it to me
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
