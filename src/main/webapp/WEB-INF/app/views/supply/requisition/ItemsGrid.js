import styles from "../universalStyles.module.css";
import React, { useEffect, useState } from "react";
import { Button } from "../../../components/Button";

const ItemsGrid = ({ items, currentPage, itemsPerPage, cart, handleQuantityChange, handleOverOrderAttempt }) => {
    const startIndex = (currentPage - 1) * itemsPerPage;
    const currentItems = items.slice(startIndex, startIndex + itemsPerPage);

    return (
        <div className={styles.grid}>
            {currentItems.map((item) => (
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

    return (
      <div className={`${styles.col312} ${styles.textAlignCenter}`}>
          <div className={`${styles.contentContainer} ${item.specialRequest ? styles.supplySpecialItem : ''}`}>
              <div style={{ paddingTop: '5px', overflow: 'hidden', position: 'relative' }}>
                  {item.specialRequest && (<div className={styles.cornerRibbon}>
                      <span>Special</span>
                  </div>)}
                  <img
                    className={styles.supplyItemImage}
                    src={`/assets/supply_photos/${item.commodityCode}.jpg`}
                    alt={item.description}
                    // height="120"
                  />
                  <p className={`${styles.darkGray} ${styles.marginV} ${styles.bold}`} style={{ height: '40px', overflow: 'hidden' }}>
                      {item.description}
                  </p>
                  <div>
                      <div className={styles.textAlignCenter}>
                          <p className={styles.darkGray} style={{ margin: '0px' }}>{item.unit}</p>
                          {!itemInCart ? (
                            <input
                              className={styles.addToCartBtn}
                              onClick={() => handleQuantityChange(item.id, localValue + 1)}
                              type="button"
                              value="Add to Cart"
                            />
                          ) : (
                             <>
                                 <input
                                   className={styles.qtyAdjustButton}
                                   onClick={() => handleQuantityChange(item.id, Math.max(0, parseInt(localValue, 10) - 1))}
                                   type="button"
                                   value="-"
                                 />
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
                                 <input
                                   className={`${styles.qtyAdjustButton} ${isMaxQuantity ? styles.darkWarn : ''}`}
                                   onClick={() => {
                                       const numericLocalValue = parseInt(localValue, 10); // Ensure it's a number
                                       if (numericLocalValue === item.perOrderAllowance) {
                                           handleOverOrderAttempt(item.id, numericLocalValue + 1);
                                       } else {
                                           handleQuantityChange(item.id, numericLocalValue + 1);
                                       }
                                   }}
                                   type="button"
                                   value="+"
                                 />
                             </>
                           )}
                      </div>
                  </div>
              </div>
          </div>
      </div>
    );
};

export default ItemsGrid;