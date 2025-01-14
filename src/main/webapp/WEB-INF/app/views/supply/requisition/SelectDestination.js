import styles from "./RequisitionFormIndex.module.css";
import { Button } from "../../../components/Button";
import React, { useState } from "react";

const SelectDestination = ({ locations, currentDestination, handleConfirmClick }) => {
    const [tempDestination, setTempDestination] = useState(currentDestination);

    const handleTempDestinationChange = (locId) => {
        const selectedLocation = locations.find((loc) => loc.locId === locId);
        setTempDestination(selectedLocation);
    };

    return (
        <div className={styles.destinationContainer}>
            <div className={styles.destinationMessage}>
                Please select a destination
                <select
                    className={styles.selectDestination}
                    value={tempDestination ? tempDestination.locId : ""}
                    onChange={(e) => handleTempDestinationChange(e.target.value)}
                >
                    <option value="">Select Destination</option>
                    {locations.map((location) => (
                        <option key={location.locId} value={location.locId}>
                            {location.code} ({location.locationDescription})
                        </option>
                    ))}
                </select>
                <Button onClick={() => handleConfirmClick(tempDestination)}>Confirm</Button>
            </div>
        </div>
    );
};

export default SelectDestination;
