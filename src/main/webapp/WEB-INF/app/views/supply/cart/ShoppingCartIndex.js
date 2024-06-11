import React, { useState } from 'react';
import styles from './ShoppingCartIndex.module.css';
import styled from 'styled-components';
import { Button } from "../../../components/Button";
import Hero from "../../../components/Hero";

const Destination = () => (
  <div className="bg-white content-info" style={{ height: "70px", color: "black", borderBottom: 'none' }}>
    <div className="padding-10" style={{ display: 'flex'}}>
      <div style={{ display: 'inline-block'}}>
        <span className="supply-text">Destination: </span>A42FB (2nd FLOOR, AG4)
      </div>
    </div>
  </div>
);

const CartList = ({ cartItems }) => (
  <div>
    LIST:
    <ul>
      {cartItems.map((cartItem, index) => (
        <li key={index}>
          {cartItem.name}
        </li>
      ))}
    </ul>
  </div>
);

const SpecialInstructions = () => {
  const [instructions, setInstructions] = useState('');

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
        <Button style={{ backgroundColor: "grey", margin: "5 px" }} onClick={() => setInstructions('')}>Empty Cart</Button>
        <Button style={{ marginLeft: '5px', backgroundColor: "grey", margin: "5 px" }}>Continue Browsing</Button>
        <Button style={{ marginLeft: '5px', margin: "5 px" }} >Checkout</Button>
      </div>
      <div className={styles.clearfix}></div>
    </div>
  );
};

export default function ShoppingCart() {
  const empty = false; // Set to true to simulate an empty cart for the example
  const [cartItems, setCartItems] = useState(empty ? [] : [{ name: "Item1" }]);

  return (
    <div>
      <Hero>Shopping Cart</Hero>
      <div className="content-container content-controls">
        {cartItems.length === 0 ? (
          <div>
            <div className={styles.emptyCartMessage}>Your cart is empty.</div>
            <div className={styles.emptyCartContainer}>
              <Button style={{ backgroundColor: "grey" }}>Continue Browsing</Button>
            </div>
          </div>
        ) : (
          <>
            <Destination />
            <CartList cartItems={cartItems} />
            <div className={styles.cartCheckoutContainer}>
              <SpecialInstructions />
            </div>
          </>
        )}
      </div>
    </div>
  );
}
