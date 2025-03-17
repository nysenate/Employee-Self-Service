import React, { useEffect, useState } from "react";
import styles from "app/views/supply/universalStyles.module.css";
import Modal from "app/components/Modal";
import { Button } from "app/components/Button";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";

export default function ItemQuantityControls({ item }) {
  const { cart, incrementItem, decrementItem, updateQuantity } =
    useSupplyContext();
  const quantity = cart.items[item.id] || 0;
  const [dirtyQty, setDirtyQty] = useState(quantity);
  const isMaxQuantity = quantity >= item.perOrderAllowance;
  const [isQtyWarningOpen, setIsQtyWarningOpen] = useState(false);

  const onQtyWarningResolved = () => {
    setIsQtyWarningOpen(false);
    if (quantity === dirtyQty) {
      incrementItem(item.id);
    } else {
      updateQuantity(item.id, dirtyQty);
    }
  };

  const onQtyWarningRejected = () => {
    setIsQtyWarningOpen(false);
    setDirtyQty(quantity);
  };

  useEffect(() => {
    setDirtyQty(quantity);
  }, [quantity]);

  return (
    <div>
      <input
        className={styles.qtyAdjustButton}
        onClick={() => decrementItem(item.id)}
        type="button"
        value="-"
      />
      <input
        className={`${styles.qtyInput} [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none`}
        style={{
          color: quantity > item.perOrderAllowance ? "red" : "",
        }}
        type="number"
        value={dirtyQty}
        onChange={(e) => setDirtyQty(parseInt(e.target.value) || 0)}
        onBlur={() => {
          if (
            dirtyQty > item.perOrderAllowance &&
            quantity <= item.perOrderAllowance
          ) {
            setIsQtyWarningOpen(true);
          } else {
            updateQuantity(item.id, dirtyQty);
          }
        }}
      />
      <input
        className={`${styles.qtyAdjustButton} ${isMaxQuantity ? styles.darkWarn : ""}`}
        onClick={() => {
          if (dirtyQty === item.perOrderAllowance) {
            setIsQtyWarningOpen(true);
          } else {
            incrementItem(item.id);
          }
        }}
        type="button"
        value="+"
      />
      <QtyWarningModal
        isOpen={isQtyWarningOpen}
        onResolve={onQtyWarningResolved}
        onReject={onQtyWarningRejected}
      />
    </div>
  );
}

function QtyWarningModal({ isOpen, onResolve, onReject }) {
  return (
    <Modal isOpen={isOpen}>
      <Modal.Title>Ordering over recommended quantity</Modal.Title>
      <Modal.Body>
        You are trying to order over the recommended quantity. This requires
        management approval.
        <br />
        Would you like to continue?
      </Modal.Body>
      <Modal.Buttons>
        <Button color="secondary" onClick={onReject}>
          Cancel
        </Button>
        <Button color="success" onClick={onResolve}>
          Yes
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
