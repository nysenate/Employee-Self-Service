import React, { useEffect, useState } from "react";
import { formatDateForInput } from "../helpers";
import { fetchApiJson } from "../../../utils/fetchJson";
import styles from "../universalStyles.module.css";

const Header = ({ filters, handleFilterChange }) => {
    const [filterOptions, setFilterOptions] = useState({
        locationOptions: [],
        itemOptions: [],
    });

    const fromDate = formatDateForInput(new Date(filters.from));
    const toDate = formatDateForInput(new Date(filters.to));

    useEffect(() => {
        const fetchOptions = async () => {
            try {
                const locations = await fetchApiJson(`/locations`).then(body => body.result);
                const items = await fetchApiJson(`/supply/items`).then(body => body.result);

                setFilterOptions({
                    locationOptions: locations,
                    itemOptions: items,
                });
            } catch (error) {
                console.error("Error fetching filter options:", error);
            }
        };

        fetchOptions();
    }, []);

    return (
        <div className={`${styles.contentContainer} ${styles.contentControls}`}>
            <div className={styles.contentInfo} style={{ position: 'relative' }}>
                <h4 className={`${styles.supplyText}`} style={{ margin: '0px', textAlign: 'center' }}>
                    Search item order counts.
                </h4>
                <a href="/supply/item-history-print" style={{ position: 'absolute', top: '11px', right: '10px' }}>
                    Print Report
                </a>
            </div>
            <div className={styles.grid} style={{ textAlign: 'center' }}>
                <div className={styles.col612} style={{ padding: '0 10px 10px 10px' }}>
                    <label className={styles.supplyText}>Item:</label>
                    <select value={filters.item || ''} onChange={(e) => handleFilterChange('item', e.target.value)}>
                        <option key="All" value="All">
                            All
                        </option>
                        {filterOptions.itemOptions.map(option => (
                            <option key={option.id} value={option.id}>
                                {option.commodityCode}
                            </option>
                        ))}
                    </select>
                </div>
                <div className={styles.col612} style={{ padding: '0 10px 10px 10px' }}>
                    <label className={styles.supplyText}>Location:</label>
                    <select value={filters.location || ''} onChange={(e) => handleFilterChange('location', e.target.value)}>
                        <option key="All" value="All">
                            All
                        </option>
                        {filterOptions.locationOptions.map(option => (
                            <option key={option.locId} value={option.locId}>
                                {option.locId}
                            </option>
                        ))}
                    </select>
                </div>
                <div className={styles.col612} style={{ padding: '0 10px 10px 10px' }}>
                    <label className={styles.supplyText}>From:</label>
                    <input
                        type="date"
                        id="from-date"
                        name="from-date"
                        value={fromDate}
                        onChange={(e) => handleFilterChange('from', new Date(e.target.value))}
                    />
                </div>
                <div className={styles.col612} style={{ padding: '0 10px 10px 10px' }}>
                    <label className={styles.supplyText}>To:</label>
                    <input
                        type="date"
                        id="to-date"
                        name="to-date"
                        value={toDate}
                        onChange={(e) => handleFilterChange('to', new Date(e.target.value))}
                    />
                </div>
            </div>
        </div>
    );
}

export default Header;
