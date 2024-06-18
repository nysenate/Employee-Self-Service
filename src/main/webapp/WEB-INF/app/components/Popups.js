// Popups.js
import React from 'react';
import Popup from "./Popup"; // Adjust the import path as necessary
import { Button } from "./Button";

export function OverOrderPopup({ isModalOpen, closeModal, onAction }) {
  const handleCancel = () => {
    onAction(false); // Passing false when cancel is clicked
    closeModal();
  };

  const handleYes = () => {
    onAction(true); // Passing true when yes is clicked
    closeModal();
  };

  return (
    <Popup
      isLocked={true}
      isOpen={isModalOpen}
      onClose={closeModal}
      title="Ordering over recommended quantity"
    >
      <p>You are trying to order over the recommended quantity. This requires management approval.</p>
      <p>Would you like to continue?</p>
      <Button onClick={handleCancel}>Cancel</Button>
      <Button onClick={handleYes}>Yes</Button>
    </Popup>
  );
}

export function CheckOutPopup({ isModalOpen, closeModal, onAction }) {
  const handlePickup = () => {
    onAction('pickup');
    closeModal();
  };

  const handleDelivery = () => {
    onAction('delivery');
    closeModal();
  };

  return (
    <Popup
      isLocked={false}
      isOpen={isModalOpen}
      onClose={closeModal}
      title="Choose delivery method"
    >
      <p>Would you like to pick up this order or have it delivered to you?</p>
      <p>Note: Orders can be picked up from L212.</p>
      <Button onClick={handlePickup}>I'll pick it up</Button>
      <Button onClick={handleDelivery}>Please deliver to me</Button>
    </Popup>
  );
}

export function EmptyCartPopup({ isModalOpen, closeModal, onAction }) {
  const handleCancel = () => {
    onAction(false);
    closeModal();
  };

  const handleYes = () => {
    onAction(true);
    closeModal();
  };

  return (
    <Popup
      isLocked={true}
      isOpen={isModalOpen}
      onClose={closeModal}
      title="Confirm Empty Cart"
    >
      <p>Are you sure you want to empty your cart?</p>
      <Button onClick={handleCancel}>Cancel</Button>
      <Button onClick={handleYes}>Yes</Button>
    </Popup>
  );
}
