import styles from "./RequisitionFormIndex.module.css";
import { Button } from "../../../components/Button";
import React from "react";


const DestinationDetails = ({ destination, handleChangeClick, sortOption, handleSortChange }) => {
    return (
        <div className={styles.destinationDetails}>
            <div className={styles.detailsRow}>
                <div className={styles.destinationInfo}>
                    <span>Destination: </span>
                    <span style={{ marginLeft: '10px', color: 'black' }}>{destination.code} ({destination.locationDescription})</span>
                    <button onClick={handleChangeClick} className={styles.changeButton}>[change]</button>
                </div>
                <div className={styles.searchBar}>
                    <input type="text" className={styles.searchInput} />
                    <Button>Search</Button>
                    <Button className={styles.resetButton}>Reset</Button>
                </div>
                <div className={styles.sortBy}>
                    <label>Sort By:</label>
                    <select className={styles.sortSelect} value={sortOption} onChange={handleSortChange}>
                        <option value="name">Name</option>
                        <option value="category">Category</option>
                    </select>
                </div>
            </div>
        </div>
    );
};



export default DestinationDetails;