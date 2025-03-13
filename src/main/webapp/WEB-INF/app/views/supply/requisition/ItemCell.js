import styles from "../universalStyles.module.css";
import React, { useEffect, useState } from "react";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";
import Modal from "app/components/Modal";
import { Button } from "app/components/Button";

export default function ItemCell({ item, handleOverOrderAttempt }) {
  const { cart, incrementItem, decrementItem, updateQuantity } =
    useSupplyContext();
  const itemQuantity = cart.items[item.id] || 0;
  const [isImagePreviewOpen, setIsImagePreviewOpen] = useState(false);
  const [isSpecialItemOpen, setIsSpecialItemOpen] = useState(false);

  const onSpecialItemResolved = () => {
    setIsSpecialItemOpen(false);
    incrementItem(item.id);
  };

  const addToCart = () => {
    if (item.specialRequest) {
      setIsSpecialItemOpen(true);
    } else {
      incrementItem(item.id);
    }
  };

  return (
    <div
      className={`w-[220px] p-3 border-1 relative ${item.specialRequest && "bg-red-100"}`}
    >
      {item.specialRequest && (
        <div className={styles.ribbon}>
          <span>Special</span>
        </div>
      )}
      <img
        className={styles.supplyItemImage}
        alt={item.description}
        src={`/assets/supply_photos/${item.commodityCode}.jpg`}
        onError={({ currentTarget }) => {
          currentTarget.onerror = null; // prevents looping
          currentTarget.src = "/assets/supply_photos/no_photo_available.png";
        }}
        onClick={() => setIsImagePreviewOpen(true)}
      />
      <p
        className={`m-1 font-semibold text-center`}
        style={{ height: "40px", overflow: "hidden" }}
      >
        {item.description}
      </p>
      <div>
        <div className="text-center">
          <p className={styles.darkGray} style={{ margin: "0px" }}>
            {item.unit}
          </p>
          {!itemQuantity ? (
            <input
              className={styles.addToCartBtn}
              onClick={addToCart}
              type="button"
              value="Add to Cart"
            />
          ) : (
            <ItemQuantitySelector
              item={item}
              quantity={itemQuantity}
              incrementItem={incrementItem}
              decrementItem={decrementItem}
              updateQuantity={updateQuantity}
            />
          )}
        </div>
      </div>
      <ImagePreviewModal
        item={item}
        isOpen={isImagePreviewOpen}
        onClose={() => setIsImagePreviewOpen(false)}
      />
      <SpecialItemModal
        isOpen={isSpecialItemOpen}
        onResolve={onSpecialItemResolved}
        onReject={() => setIsSpecialItemOpen(false)}
      />
    </div>
  );
}

function ItemQuantitySelector({
  item,
  quantity,
  incrementItem,
  decrementItem,
  updateQuantity,
}) {
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
    console.log(item);
  };

  const onQtyWarningRejected = () => {
    setIsQtyWarningOpen(false);
    setDirtyQty(quantity);
  };

  useEffect(() => {
    setDirtyQty(quantity);
  }, [quantity]);

  return (
    <>
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
    </>
  );
}

function ImagePreviewModal({ item, isOpen, onClose }) {
  return (
    <Modal isOpen={isOpen} onSoftReject={onClose}>
      <Modal.Title>{item.description}</Modal.Title>
      <Modal.Body>
        <img
          className=""
          alt={item.description}
          src={`/assets/supply_photos/${item.commodityCode}_800.jpg`}
          onError={({ currentTarget }) => {
            currentTarget.onerror = null; // prevents looping
            currentTarget.src = "/assets/supply_photos/no_photo_available.png";
          }}
        />
      </Modal.Body>
    </Modal>
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

function SpecialItemModal({ isOpen, onResolve, onReject }) {
  return (
    <Modal isOpen={isOpen}>
      <Modal.Title>Special Order Item</Modal.Title>
      <Modal.Body>
        You are ordering a special order item.
        <br />
        <span className="font-bold">
          All special order items require prior approval from the M&O Director's
          Office with written justification.
        </span>
      </Modal.Body>
      <Modal.Buttons>
        <Button color="secondary" onClick={onReject}>
          Cancel
        </Button>
        <Button color="success" onClick={onResolve}>
          Add to Cart
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
