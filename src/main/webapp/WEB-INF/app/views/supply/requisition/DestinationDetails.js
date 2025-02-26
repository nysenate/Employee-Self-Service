import styles from "./RequisitionFormIndex.module.css";
import { Button } from "../../../components/Button";
import React from "react";

const DestinationDetails = ({
  destination,
  handleChangeClick,
  sortOption,
  handleSortChange,
}) => {
  return (
    <div className={styles.destinationDetails}>
      <div className={styles.detailsRow}>
        <div className={styles.destinationInfo}>
          <span>Destination: </span>
          <span style={{ marginLeft: "10px", color: "black" }}>
            {destination.code} ({destination.locationDescription})
          </span>
          <button onClick={handleChangeClick} className={styles.changeButton}>
            [change]
          </button>
        </div>
      </div>
    </div>
  );
};

export default DestinationDetails;
