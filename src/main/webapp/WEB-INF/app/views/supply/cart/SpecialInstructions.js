import React, { useState } from "react";
import styles from "./ShoppingCartIndex.module.css";
import { Button } from "../../../components/Button";
import { Link } from "react-router-dom";

const SpecialInstructions = ({ openEmptyCartPopup, openCheckOutPopup, instructions, setInstructions }) => {

  return (
    <div className={styles.specialInstructionsContainer}>
      <label className={styles.specialInstructionsLabel} htmlFor="special-instructions-area">Special Instructions</label>
      <textarea
        id="special-instructions-area"
        className={styles.specialInstructionsTextArea}
        value={instructions}
        onChange={(e) => setInstructions(e.target.value)}
      />
      <div className={styles.buttonGroup}>
        <Button style={{ backgroundColor: "#8d8d8d", margin: "5px" }} onClick={openEmptyCartPopup}>Empty Cart</Button>
        <Link to="/supply/shopping/order" style={{ textDecoration: 'none' }}>
          <Button style={{ marginLeft: '5px', backgroundColor: "#8d8d8d", margin: "5px" }}>
            Continue Browsing
          </Button>
        </Link>
        <Button style={{ marginLeft: '5px', margin: "5px" }} onClick={openCheckOutPopup}>Checkout</Button>
      </div>
      <div className={styles.clearfix}></div>
    </div>
  );
};

export default SpecialInstructions;