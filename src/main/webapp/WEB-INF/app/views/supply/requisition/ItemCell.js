import React, { useState } from "react";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";
import Modal from "app/components/Modal";
import { Button } from "app/components/Button";
import ItemQuantityControls from "app/views/supply/ItemQuantityControls";
import Ribbon from "app/views/supply/requisition/Ribbon";

export default function ItemCell({ item }) {
  const { cart, incrementItem } = useSupplyContext();
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
      {item.specialRequest && <Ribbon>Special</Ribbon>}
      <img
        className="my-3 mx-auto h-[120px]"
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
          <div className="">{item.unit}</div>
          {!itemQuantity ? (
            <input
              className="w-40 m-2 h-[28px] bg-green-600 hover:bg-green-500 font-semibold rounded-sm text-white pointer transition"
              onClick={addToCart}
              type="button"
              value="Add to Cart"
            />
          ) : (
            <ItemQuantityControls item={item} />
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
