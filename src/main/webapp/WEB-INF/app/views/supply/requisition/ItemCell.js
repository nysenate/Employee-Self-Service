import styles from "../universalStyles.module.css";
import React, { useEffect, useState } from "react";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";

export default function ItemCell({ item, handleOverOrderAttempt }) {
  const { cart, incrementItem, decrementItem, updateQuantity } =
    useSupplyContext();
  const itemQuantity = cart.items[item.id] || 0;

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
              onClick={() => incrementItem(item.id)}
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
            dirtyQty >= item.perOrderAllowance &&
            quantity < item.perOrderAllowance
          ) {
            console.log("OVER ORDER ATTEMPT"); // TODO Over order qty modal
          } else {
            updateQuantity(item.id, dirtyQty);
          }
        }}
      />
      <input
        className={`${styles.qtyAdjustButton} ${isMaxQuantity ? styles.darkWarn : ""}`}
        onClick={() => {
          if (dirtyQty === item.perOrderAllowance) {
            console.log("OVER ORDER ATTEMPT"); // TODO Over order qty modal
          } else {
            incrementItem(item.id);
          }
        }}
        type="button"
        value="+"
      />
    </>
  );
}
