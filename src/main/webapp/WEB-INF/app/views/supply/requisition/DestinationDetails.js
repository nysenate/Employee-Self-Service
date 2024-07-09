import styles from "./RequisitionFormIndex.module.css";
import { Button } from "../../../components/Button";
import React, { useState } from "react";


const DestinationDetails = ({ destination, handleChangeClick, setItems }) => {
    const [sortOption, setSortOption] = useState('name');

    const handleSortChange = (e) => {
        setSortOption(e.target.value);
        setItems(sortItems(items, e.target.value));
    };

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

const sortItems = (items, sortOption) => {
    if (sortOption === 'name') {
        return [...items].sort((a, b) => a.description.localeCompare(b.description));
    }
    if (sortOption === 'category') {
        return [...items].sort((a, b) => a.category.localeCompare(b.category));
    }
    return items;
};


export default DestinationDetails;