import styles from "./ShoppingCartIndex.module.css";
import React, { useEffect, useState } from "react";

const ItemsGrid = ({ items, cart, handleQuantityChange, handleOverOrderAttempt }) => {
  return (
    <div className={styles.itemGrid}>
      {items.map((item) => (
        <ItemDisplay
          key={item.id}
          item={item}
          cart={cart}
          handleQuantityChange={handleQuantityChange}
          handleOverOrderAttempt={handleOverOrderAttempt}
        />
      ))}
    </div>
  );
};

const ItemDisplay = ({ item, cart, handleQuantityChange, handleOverOrderAttempt }) => {
  const itemInCart = cart[item.id];
  const isMaxQuantity = itemInCart && itemInCart >= item.perOrderAllowance;
  const [localValue, setLocalValue] = useState(cart[item.id] || 0);

  // Synchronize localValue and itemInCart
  useEffect(() => {
    setLocalValue(itemInCart || 0);
  }, [itemInCart]);

  const handleTempInputChange = (e) => {
    const { value } = e.target;
    if (/^\d*$/.test(value)) { // Only allow numbers
      setLocalValue(value);
    }
  };

  if (!itemInCart) return null;

  return (
    <div className={styles.itemCard}>
      <div className={styles.itemImageContainer}>
        <div className={styles.itemImageContent}>
          <img
            className={styles.supplyItemImage}
            src={`/assets/supply_photos/${item.commodityCode}.jpg`}
            alt={item.description}
            height="120"
          />
        </div>
      </div>
      <div className={styles.titleContainer}>
        <div className={styles.titleContent}>
          <h3>{item.description}</h3>
        </div>
      </div>
      <div className={styles.qtyContainer}>
        <div className={styles.qtySelectorContainer}>
          <div className="text-align-center" style={{textAlign: 'center', width: '200px'}}>
            <p style={{color: 'dark-grey', margin: '0', height: '18px'}}>{item.unit}</p>
            <div  className={styles.qtySelector}>
              {/* Decrement Button */}
              <button
                className={styles.qtyAdjustButton}
                onClick={() => handleQuantityChange(item.id, Math.max(0, parseInt(localValue, 10) - 1))}
              >
                -
              </button>

              {/* Quantity Input */}
              <input
                className={styles.qtyInput}
                style={{ color: parseInt(localValue, 10) > item.perOrderAllowance ? 'red' : '' }}
                type="text"
                value={localValue}
                onChange={handleTempInputChange}
                onBlur={() => {
                  const numericLocalValue = parseInt(localValue, 10); // Ensure it's a number
                  if (numericLocalValue > item.perOrderAllowance && itemInCart <= item.perOrderAllowance) {
                    handleOverOrderAttempt(item.id, numericLocalValue);
                    setLocalValue(cart[item.id]);
                  } else {
                    handleQuantityChange(item.id, numericLocalValue);
                  }
                }}
              />

              {/* Increment Button */}
              <button
                className={styles.qtyAdjustButton}
                onClick={() => {
                  const numericLocalValue = parseInt(localValue, 10); // Ensure it's a number
                  if (numericLocalValue === item.perOrderAllowance) {
                    handleOverOrderAttempt(item.id, numericLocalValue + 1);
                  } else {
                    handleQuantityChange(item.id, numericLocalValue + 1);
                  }
                }}
                style={{ backgroundColor: isMaxQuantity ? 'red' : '' }}
              >
                +
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ItemsGrid;