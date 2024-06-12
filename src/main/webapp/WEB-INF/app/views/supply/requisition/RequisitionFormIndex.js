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
const ItemsGrid = ({ items, currentPage, itemsPerPage }) => {
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentItems = items.slice(startIndex, startIndex + itemsPerPage);

  return (
    <div className={styles.itemGrid}>
      {currentItems.map((item) => (
        <ItemDisplay key={item.id} item={item} />
      ))}
    </div>
  );
};
const ItemDisplay = (item) => {
  // const handleImageError = (e) => {
  //   e.target.src = "/assets/supply_photos/default.jpg"; // Set a default image
  // };
  console.log(item);
  return (
    <div className={styles.itemCard}>
      <div className={styles.itemImage}>
        <img
          src={`/assets/supply_photos/${item.item.commodityCode}.jpg`}
          alt={item.description}
          width="160"
          height="120"
        />
      </div>
      <div className={styles.itemDescription}>
        <h4>{item.item.description}</h4>
        <p>{item.item.packageSize}</p>
      </div>
      <div className={styles.itemPrice}>
        <p>{item.item.price}</p>
      </div>
      <Button>Add to Cart</Button>
    </div>
  );
}
const Pagination = ({ currentPage, totalPages, onPageChange }) => {
  const pageNumbers = [];

  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i);
  }

  return (
    <div className={styles.pagination}>
      <button
        className={styles.pageButton}
        onClick={() => onPageChange(1)}
        disabled={currentPage === 1}
      >
        &laquo;
      </button>
      <button
        className={styles.pageButton}
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 1}
      >
        &lsaquo;
      </button>
      {pageNumbers.map((number) => (
        <button
          key={number}
          className={`${styles.pageButton} ${number === currentPage ? styles.activePage : ''}`}
          onClick={() => onPageChange(number)}
        >
          {number}
        </button>
      ))}
      <button
        className={styles.pageButton}
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages}
      >
        &rsaquo;
      </button>
      <button
        className={styles.pageButton}
        onClick={() => onPageChange(totalPages)}
        disabled={currentPage === totalPages}
      >
        &raquo;
      </button>
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
    () => JSON.parse(localStorage.getItem('destination')) || ''  );
  const [tempDestination, setTempDestination] = useState(destination);
  const [locations, setLocations] = useState([]);
  const [items, setItems] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 6; // Adjust the number of items per page as needed

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
    setCurrentPage(1);
  };
  const handleChangeClick = () => {
    setTempDestination('');
    setDestination('');
    setItems([]);
  };
  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  return (
    <div>
      <Hero>Requisition Form</Hero>
      {destination ? (
        <div>
          <DestinationDetails destination={destination} handleChangeClick={handleChangeClick} items={items} />
          <Pagination currentPage={currentPage} totalPages={Math.ceil(items.length / itemsPerPage)} onPageChange={handlePageChange} />
          <ItemsGrid items={items} currentPage={currentPage} itemsPerPage={itemsPerPage} />
          <Pagination currentPage={currentPage} totalPages={Math.ceil(items.length / itemsPerPage)} onPageChange={handlePageChange} />
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
