import React, { useState, useEffect } from "react";
import Hero from "../../../components/Hero";
import styles from './RequisitionFormIndex.module.css';
import { Button } from "../../../components/Button";
import { fetchApiJson } from "app/utils/fetchJson";
import useAuth from "app/contexts/Auth/useAuth";

const SelectDestination = ({ locations, tempDestination, handleTempDestinationChange, handleConfirmClick }) => {
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
          {locations.map((location) => (
            <option key={location.locId} value={location.locId}>
              {location.code} ({location.locationDescription})
            </option>
          ))}
        </select>
        <Button onClick={handleConfirmClick}>Confirm</Button>
      </div>
    </div>
  );
};
const DestinationDetails = ({ destination, handleChangeClick, items }) => {
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
const ItemsTemplate = () => {
  return (
    <div>
      <img
        src="/assets/supply_photos/SEALSENATEG.jpg"
        width="160"
        height="120"
        alt="New York State Senate Capital Building"
      />
    </div>
  );
}
const Items = ({ items }) => {
  const handleImageError = (e) => {
    e.target.src = "/assets/supply_photos/default.jpg"; // Set a default image
  };

  return (
    <div>
      {items.map((item) => (
        <div key={item.id} value={item.id}>
          {item.id} ({item.description})
          <img
            src={`/assets/supply_photos/${item.commodityCode}.jpg`}
            width="160"
            height="120"
            alt={`${item.description}`}
            onError={handleImageError}
          />
        </div>
      ))}
    </div>
  );
};

const getLocations = async empId => {
  return await fetchApiJson(`/supply/destinations/${empId}`)
    .then((body) => body);
};
const getItems = async locId => {
  return await fetchApiJson(`/supply/items/orderable/${locId}`)
    .then((body) => body.result);
};

export default function RequisitionFormIndex() {
  const auth = useAuth();
  const [destination, setDestination] = useState(
    () => JSON.parse(localStorage.getItem('destination')) || ''
  );
  const [tempDestination, setTempDestination] = useState(destination);
  const [locations, setLocations] = useState([]);
  const [items, setItems] = useState([]);

  useEffect(() => {
    localStorage.setItem('destination', JSON.stringify(destination));
    if (destination) {
      fetchItems(destination);
    }
  }, [destination]);

  useEffect(() => {
    getLocations(auth.empId()).then(r => setLocations(r.result));
  }, [auth]);

  const fetchItems = async (destination) => {
    const items = await getItems(destination);
    setItems(items);
  };

  const handleTempDestinationChange = (e) => {
    setTempDestination(e.target.value);
  };

  const handleConfirmClick = () => {
    setDestination(tempDestination);
  };

  const handleChangeClick = () => {
    setTempDestination('');
    setDestination('');
    setItems([]);
  };

  return (
    <div>
      <Hero>Requisition Form</Hero>
      {destination ? (
        <div>
          <DestinationDetails destination={destination} handleChangeClick={handleChangeClick} items={items} />
          {/*<Items items={items} />*/}
          <ItemsTemplate />
        </div>
      ) : (
         <SelectDestination
           locations={locations}
           tempDestination={tempDestination}
           handleTempDestinationChange={handleTempDestinationChange}
           handleConfirmClick={handleConfirmClick}
         />
       )}


    </div>
  );
}
