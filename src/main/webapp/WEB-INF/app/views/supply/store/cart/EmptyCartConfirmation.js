import Modal from "app/components/Modal";
import Button from "app/components/Button";
import React, { useState } from "react";
import { useSupplyContext } from "app/views/supply/store/useSupplyContext";

export default function EmptyCartConfirmation({ isOpen, setIsOpen }) {
  const { clearCart } = useSupplyContext();

  const onResolve = () => {
    clearCart();
    setIsOpen(false);
  };

  return (
    <Modal isOpen={isOpen}>
      <Modal.Title>Confirm Empty Cart</Modal.Title>
      <Modal.Body>Are you sure you want to empty your cart?</Modal.Body>
      <Modal.Buttons>
        <Button variant="secondary" onPress={() => setIsOpen(false)}>
          Cancel
        </Button>
        <Button variant="primary" onPress={onResolve}>
          Yes
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
