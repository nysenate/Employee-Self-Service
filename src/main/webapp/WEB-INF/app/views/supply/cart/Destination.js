import styles from "./ShoppingCartIndex.module.css";
import React from "react";

const Destination = () => {
  const locId = JSON.parse(localStorage.getItem('destination')).locId;
  const locationDescription = JSON.parse(localStorage.getItem('destination')).locationDescription;

  return (
    <div className={styles.subHeroContainer}>
      <div className={styles.subHeroContext}>
        <div className={styles.destinationContainer}>
          <span style={{fontWeight: '700', color: '#374282'}}>Destination: </span>{locId} ({locationDescription})
        </div>
      </div>
    </div>
  )
}

export default Destination;