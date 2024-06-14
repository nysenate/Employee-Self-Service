import React, { useEffect, useState } from "react";
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

const ItemDisplay = ({ item }) => {
  return (
    <div className={styles.itemCard}>
      <div className={styles.itemImage}>
        <img
          src={`/assets/supply_photos/${item.commodityCode}.jpg`}
          alt={item.description}
          width="160"
          height="120"
        />
      </div>
      <div className={styles.itemDescription}>
        <h4>{item.description}</h4>
        <p>{item.packageSize}</p>
      </div>
      <div className={styles.itemPrice}>
        <p>{item.price}</p>
      </div>
      <Button>Add to Cart</Button>
    </div>
  );
}

const Pagination = ({ currentPage, totalPages, onPageChange, top }) => {
  const calculatePageNumbers = (currentPage, totalPages) => {
    let pageNumbers = [];
    pageNumbers.push(1);

    if (currentPage > 5 && totalPages>9) {
      pageNumbers.push('...');
    }

    let dl = Math.abs(currentPage - 1) - 4;
    let dr = Math.abs(currentPage - totalPages) - 4;
    let rangeStart = currentPage - 3;

    console.log("currentPage", currentPage);
    console.log("dl", dl);
    console.log("dr", dr);

    if (dl < 0) {
      rangeStart = currentPage -3 - dl;
      console.log("rangeStart: ", rangeStart, " = ", currentPage, " -3 - ", dl)
    }
    if (dr < 0) {
      rangeStart = currentPage -3 + dr;
    }

    for (let i = rangeStart; i <= rangeStart + 6 && i < totalPages; i++) {
      if (i > 1 && i < totalPages) {
        pageNumbers.push(i);
      }
    }

    if (currentPage < totalPages - 4 && totalPages>9) {
      pageNumbers.push('...');
    }
    pageNumbers.push(totalPages);

    return pageNumbers;
  };

  const pageNumbers = calculatePageNumbers(currentPage, totalPages);

  return (
    <div className={`${styles.pagination} ${top ? styles.paginationTop : styles.paginationBottom}`}>
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
      {pageNumbers.map((number, index) => (
        <button
          key={index}
          className={`${styles.pageButton} ${number === currentPage ? styles.activePage : ''}`}
          onClick={() => (number >= 1 && number <= totalPages) ? onPageChange(number) : console.log("nah")}
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
  const itemsPerPage = 16; // Adjust the number of items per page as needed

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

  const handlePageChange = (page) => {
    if (page >= 1 && page <= Math.ceil(items.length / itemsPerPage)) {
      setCurrentPage(page);
    }
  };

  return (
    <div>
      <Hero>Requisition Form</Hero>
      {destination ? (
        <div>
          <DestinationDetails destination={destination} handleChangeClick={handleChangeClick} items={items} />
          <Pagination currentPage={currentPage} totalPages={Math.ceil(items.length / itemsPerPage)} onPageChange={handlePageChange} top={true} />
          <ItemsGrid items={items} currentPage={currentPage} itemsPerPage={itemsPerPage} />
          <Pagination currentPage={currentPage} totalPages={Math.ceil(items.length / itemsPerPage)} onPageChange={handlePageChange} top={false} />
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
