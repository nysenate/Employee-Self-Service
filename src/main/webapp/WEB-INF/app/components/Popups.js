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
      <div style={{margin: '20px', textAlign: 'center'}}>
        <p>You are trying to order over the recommended quantity. This requires management approval.</p>
        <p>Would you like to continue?</p>
      </div>
      <div style={{display: 'flex', justifyContent: 'center', gap: '5px'}}>
        <Button style={{backgroundColor: '#8d8d8d'}} onClick={handleCancel}>Cancel</Button>
        <Button onClick={handleYes}>Yes</Button>
      </div>
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

      <div>
        <h4 className="p-2 font-semibold text-center" style={{margin: '10px'}} >Would you like to pick up this order <br /> or have it delivered to you?</h4>
        <p style={{marginBottom: '10px'}} >Note: Orders can be picked up from L212.</p>
        <div style={{display: 'flex', justifyContent: 'center', gap: '5px'}}>
          <Button onClick={handlePickup}>I'll pick it up</Button>
          &nbsp;&nbsp;
          <Button style={{backgroundColor: '#8d8d8d'}} onClick={handleDelivery}>Please deliver to me</Button>
        </div>
      </div>

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
      <p style={{margin: '10px', marginBottom: '20px', marginTop: '20px'}}>Are you sure you want to empty your cart?</p>
      <div style={{display: 'flex', justifyContent: 'center', gap: '5px'}}>
        <Button style={{backgroundColor: '#8d8d8d'}} onClick={handleCancel}>Cancel</Button>
        <Button onClick={handleYes}>Yes</Button>
      </div>
    </Popup>
  );
}

export function ChangeDestinationPopup({ isModalOpen, closeModal, onAction }) {
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
      title="Change Destination"
    >
      <p style={{margin: '10px', marginBottom: '10px', marginTop: '20px', padding: '10px'}}>
        You are about to change your destination.
        <br /><br />
        Please note that your shopping cart will be emptied as a result of this operation.
        <br /><br />
        Would you like to continue?</p>
      <div style={{display: 'flex', justifyContent: 'center', gap: '5px'}}>
        <Button style={{backgroundColor: '#8d8d8d'}} onClick={handleCancel}>Cancel</Button>
        <Button onClick={handleYes}>Yes</Button>
      </div>
    </Popup>
  );
}
