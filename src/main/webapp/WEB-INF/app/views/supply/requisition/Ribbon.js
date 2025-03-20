import React from "react";
import styles from "app/views/supply/requisition/Ribbon.module.css";

export default function Ribbon({ children }) {
  return (
    <div className={styles.ribbon}>
      <span>{children}</span>
    </div>
  );
}
