import styles from "./RequisitionFormIndex.module.css";
import React, { useEffect, useState } from "react";
import { Button } from "../../../components/Button";

const ItemsGrid = ({ items, currentPage, itemsPerPage, cart, handleQuantityChange, handleOverOrderAttempt }) => {
    const startIndex = (currentPage - 1) * itemsPerPage;
    const currentItems = items.slice(startIndex, startIndex + itemsPerPage);

    return (
        <div className={styles.itemGrid}>
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
        <div className={styles.itemCard}>
            <div className={styles.itemImage}>
                <img
                    src={`/assets/supply_photos/${item.commodityCode}.jpg`}
                    alt={item.description}
                    height="120"
                />
            </div>
            <div className={styles.itemDescription}>
                <h4>{item.description}</h4>
                <p>{item.unit}</p>
            </div>
            {itemInCart ? (
                <div className={styles.itemQuantities}>
                    <div className={styles.itemInputs}>
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
            ) : (
                <Button onClick={() => handleQuantityChange(item.id, localValue+1)}>Add to Cart</Button>
            )}
        </div>
    );
};

export default ItemsGrid;