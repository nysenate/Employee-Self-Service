import Hero from "app/components/Hero";
import styles from "app/views/supply/cart/ShoppingCartIndex.module.css";
import { Link } from "react-router-dom";
import { Button } from "app/components/Button";
import React from "react";
import Controls from "app/components/Controls";

export default function EmptyCart() {
  return (
    <div>
      <Hero>Shopping Cart</Hero>
      <Controls>
        <div>
          <div className={styles.emptyCartMessage}>Your cart is empty.</div>
          <div className={styles.emptyCartContainer}>
            <Link
              to="/supply/requisition-form"
              style={{ textDecoration: "none" }}
            >
              <Button color="secondary">Continue Browsing</Button>
            </Link>
          </div>
        </div>
      </Controls>
    </div>
  );
}
