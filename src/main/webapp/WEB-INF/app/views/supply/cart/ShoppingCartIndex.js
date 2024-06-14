import React, { useState, useEffect } from 'react';
import styles from './ShoppingCartIndex.module.css';
import { Button } from "../../../components/Button";
import Hero from "../../../components/Hero";
import { incrementItem, decrementItem, clearCart } from '../cartUtils';
import { Link } from 'react-router-dom';

const Destination = () => {
  // const locId = JSON.parse(localStorage.getItem('destinationLocId'));
  // const locationDescription = JSON.parse(localStorage.getItem('destinationLocationDescription'));
  return (
    <div className="bg-white content-info" style={{ height: "70px", color: "black", borderBottom: 'none' }}>
      <div className="padding-10" style={{ display: 'flex'}}>
        <div style={{ display: 'inline-block'}}>
          {/*<span className="supply-text">Destination: </span>{locId} ({locationDescription})*/}
          <span className="supply-text">Destination: </span> AGDS (asdfasdf)
        </div>
      </div>
    </div>
  );
}

const CartList = ({ cartItems }) => (
  <div>
    LIST:
    <ul>
      {cartItems.map((cartItem, index) => (
        <li key={index}>
          {cartItem.id} - {cartItem.quantity}
        </li>
      ))}
    </ul>
  </div>
);

const SpecialInstructions = ({ clearCart }) => {
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
        <Button style={{ backgroundColor: "grey", margin: "5px" }} onClick={clearCart}>Empty Cart</Button>
        <Link to="/supply/shopping/order" style={{ textDecoration: 'none' }}>
          <Button style={{ marginLeft: '5px', backgroundColor: "grey", margin: "5px" }}>
            Continue Browsing
          </Button>
        </Link>
        <Button style={{ marginLeft: '5px', margin: "5px" }}>Checkout</Button>
      </div>
      <div className={styles.clearfix}></div>
    </div>
  );
};

export default function ShoppingCart() {
  const [cart, setCart] = useState(() => JSON.parse(localStorage.getItem('cart')) || {});
  useEffect(() => { localStorage.setItem('cart', JSON.stringify(cart)); }, [cart]);
  const handleIncrement = (itemId) => {
    incrementItem(itemId);
    setCart(JSON.parse(localStorage.getItem('cart')));
  };
  const handleDecrement = (itemId) => {
    decrementItem(itemId);
    setCart(JSON.parse(localStorage.getItem('cart')));
  };
  const handleClearCart = () => {
    clearCart();
    setCart({});
  };
  const cartItems = Object.entries(cart).map(([id, quantity]) => ({ id, quantity }));

  return (
    <div>
      <Hero>Shopping Cart</Hero>
      <div className="content-container content-controls">
        {cartItems.length === 0 ? (
          <div>
            <div className={styles.emptyCartMessage}>Your cart is empty.</div>
            <div className={styles.emptyCartContainer}>
              <Link to="/supply/shopping/order" style={{ textDecoration: 'none' }}>
                <Button style={{ backgroundColor: "grey" }}>Continue Browsing</Button>
              </Link>
            </div>
          </div>
        ) : (
           <>
             <Destination />
             <CartList cartItems={cartItems} />
             <div className={styles.cartCheckoutContainer}>
               <SpecialInstructions clearCart={handleClearCart}/>
             </div>
           </>
         )}
      </div>
    </div>
  );
}
