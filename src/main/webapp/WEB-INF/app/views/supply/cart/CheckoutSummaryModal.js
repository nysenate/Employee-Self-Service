import Modal from "app/components/Modal";
import { Button } from "app/components/Button";
import React from "react";
import { Link } from "react-router-dom";

export default function CheckoutSummaryModal({ isOpen, setIsOpen, res }) {
  return (
    <Modal isOpen={isOpen}>
      <Modal.Title>Your Requisition Request has been Submitted</Modal.Title>
      <Modal.Body>
        <div className="text-center">
          <span className="font-semibold">
            Your requisition id number is: {res?.result.requisitionId}
            <br />
            <br />
            {res?.result.deliveryMethod === "PICKUP" && (
              <span>
                Please pickup your order from L212 at your earliest convenience.
                <br />
                <br />
              </span>
            )}
            What would you like to do next?
          </span>
        </div>
      </Modal.Body>
      <Modal.Buttons>
        <Link to="/logout">
          <Button color="error">Log out of ESS</Button>
        </Link>
        <Link to="/supply/requisition-form">
          <Button color="success">Back to ESS</Button>
        </Link>
      </Modal.Buttons>
    </Modal>
  );
}
