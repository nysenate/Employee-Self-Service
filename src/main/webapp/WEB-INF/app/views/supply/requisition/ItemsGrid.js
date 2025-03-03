import styles from "../universalStyles.module.css";
import React, { useEffect, useState } from "react";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";

export default function ItemsGrid({ items, handleOverOrderAttempt }) {
  return (
    <div className={styles.grid}>
      {items.map((item) => (
        <ItemDisplay
          key={item.id}
          item={item}
          handleOverOrderAttempt={handleOverOrderAttempt}
        />
      ))}
    </div>
  );
}

const ItemDisplay = ({ item, handleOverOrderAttempt }) => {
  const { cart, incrementItem, decrementItem, updateQuantity } =
    useSupplyContext();
  const itemQuantity = cart.items[item.id] || 0;
  const isMaxQuantity = itemQuantity >= item.perOrderAllowance;
  const [localValue, setLocalValue] = useState(cart[item.id] || 0);

  console.log(cart);
  // Synchronize localValue and itemQuantity
  useEffect(() => {
    setLocalValue(itemQuantity || 0);
  }, [itemQuantity]);

  const handleTempInputChange = (e) => {
    const { value } = e.target;
    if (/^\d*$/.test(value)) {
      // Only allow numbers
      setLocalValue(value);
    }
  };

  return (
    <div className={`${styles.col312} ${styles.textAlignCenter}`}>
      <div
        className={`${styles.contentContainer} ${item.specialRequest ? styles.supplySpecialItem : ""}`}
      >
        <div
          style={{
            paddingTop: "5px",
            overflow: "hidden",
            position: "relative",
          }}
        >
          {item.specialRequest && (
            <div className={styles.cornerRibbon}>
              <span>Special</span>
            </div>
          )}
          <img
            className={styles.supplyItemImage}
            src={`/assets/supply_photos/${item.commodityCode}.jpg`}
            alt={item.description}
            // height="120"
          />
          <p
            className={`${styles.darkGray} ${styles.marginV} ${styles.bold}`}
            style={{ height: "40px", overflow: "hidden" }}
          >
            {item.description}
          </p>
          <div>
            <div className={styles.textAlignCenter}>
              <p className={styles.darkGray} style={{ margin: "0px" }}>
                {item.unit}
              </p>
              {!itemQuantity ? (
                <input
                  className={styles.addToCartBtn}
                  // onClick={() => handleQuantityChange(item.id, localValue + 1)}
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
      </div>
    </div>
  );
};

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
