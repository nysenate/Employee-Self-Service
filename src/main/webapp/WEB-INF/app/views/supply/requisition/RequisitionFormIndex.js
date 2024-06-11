import React, { useState, useEffect } from "react";
import Hero from "../../../components/Hero";
import styles from './RequisitionFormIndex.module.css';
import { Button } from "../../../components/Button";
import { fetchApiJson } from "app/utils/fetchJson";
import useAuth from "app/contexts/Auth/useAuth";

const SelectDestination = ({ tempDestination, handleTempDestinationChange, handleConfirmClick }) => {
  return (
    <div className={styles.destinationContainer}>
      <div className={styles.destinationMessage}>
        Please select a destination
        <select
          className={styles.selectDestination}
          value={tempDestination}
          onChange={handleTempDestinationChange}
        >
          <option value="">Select Destination</option>
          <option value="A42FB">A42FB (2ND FLOOR, AG4)</option>
          <option value="B21C">B21C (3RD FLOOR, BG2)</option>
          <option value="C34D">C34D (1ST FLOOR, CD3)</option>
        </select>
        <Button onClick={handleConfirmClick}>Confirm</Button>
      </div>
    </div>
  );
};

const DestinationDetails = ({ destination, handleChangeClick }) => {
  return (
    <div className={styles.destinationDetails}>
      <div className={styles.detailsRow}>
        <div className={styles.destinationInfo}>
          <span>Destination: </span>
          <span style={{ marginLeft: '10px', color: 'black' }}>{destination}</span>
          <button onClick={handleChangeClick} className={styles.changeButton}>[change]</button>
        </div>
        <div className={styles.searchBar}>
          <input type="text" className={styles.searchInput} />
          <Button>Search</Button>
          <Button className={styles.resetButton}>Reset</Button>
        </div>
        <div className={styles.sortBy}>
          <label>Sort By:</label>
          <select className={styles.sortSelect}>
            <option value="name">Name</option>
            <option value="category">Category</option>
          </select>
        </div>
      </div>
    </div>
  );
};

const getEmpDetails = async empId => {
  return await fetchApiJson(`/supply/destinations/${empId}`)
    .then((body) => body);
}

export default function RequisitionFormIndex() {
  const auth = useAuth();

  const [destination, setDestination] = useState(
    () => JSON.parse(localStorage.getItem('destination')) || ''
  );
  const [tempDestination, setTempDestination] = useState(destination);

  useEffect(() => {
    getEmpDetails(auth.empId()).then(r => console.log(r.result[0].code))
    localStorage.setItem('destination', JSON.stringify(destination));
  }, [destination]);

  const handleTempDestinationChange = (e) => {
    setTempDestination(e.target.value);
  };

  const handleConfirmClick = () => {
    setDestination(tempDestination);
  };

  const handleChangeClick = () => {
    setTempDestination('');
    setDestination('');
  };

  return (
    <div>
      <Hero>Requisition Form</Hero>
      {destination ? (
        <DestinationDetails destination={destination} handleChangeClick={handleChangeClick} />
      ) : (
         <SelectDestination
           tempDestination={tempDestination}
           handleTempDestinationChange={handleTempDestinationChange}
           handleConfirmClick={handleConfirmClick}
         />
       )}
    </div>
  );
}
