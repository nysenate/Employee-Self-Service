import React, { useEffect, useState } from "react";
import Hero from "../../../components/Hero";
import styles from './RequisitionFormIndex.module.css';
import { Button } from "../../../components/Button";
import { fetchApiJson } from "app/utils/fetchJson";
import useAuth from "app/contexts/Auth/useAuth";
import LoadingIndicator from "app/components/LoadingIndicator";
import Pagination from "./Pagination";

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
          {locations && locations.map((location) => (
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

const DestinationDetails = ({ locations, destination, handleChangeClick, items, handleSortChange, sortOption }) => {
  const location = locations?.find(loc => loc.locId === destination);

  if (!location) return <div>Location not found</div>;

  return (
    <div className={styles.destinationDetails}>
      <div className={styles.detailsRow}>
        <div className={styles.destinationInfo}>
          <span>Destination: </span>
          <span style={{ marginLeft: '10px', color: 'black' }}>{location.code} ({location.locationDescription})</span>
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

const ItemsGrid = ({ items, currentPage, itemsPerPage }) => {
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentItems = items?.slice(startIndex, startIndex + itemsPerPage);

  return (
    <div className={styles.itemGrid}>
      {currentItems?.map((item) => (
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
          height="120"
        />
      </div>
      <div className={styles.itemDescription}>
        <h4>{item.description}</h4>
        <p>{item.unit}</p>
      </div>
      <Button>Add to Cart</Button>
    </div>
  );
};

const getLocations = async (empId) => {
  return await fetchApiJson(`/supply/destinations/${empId}`)
    .then((body) => body);
};

const getItems = async (locId) => {
  return await fetchApiJson(`/supply/items/orderable/${locId}`)
    .then((body) => body.result);
};

export default function RequisitionFormIndex() {
  const auth = useAuth();
  const [locations, setLocations] = useState([]); // Initialize as an empty array
  const [destination, setDestination] = useState();
  const [tempDestination, setTempDestination] = useState(destination);
  const [items, setItems] = useState([]);
  const itemsPerPage = 16; // Adjust the number of items per page as needed
  const [currentPage, setCurrentPage] = useState(1);
  const [sortOption, setSortOption] = useState('name');
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const fetchInitialData = async () => {
      setIsLoading(true);
      const fetchedLocations = await getLocations(auth.empId());
      setLocations(fetchedLocations.result);
      setIsLoading(false);
    };
    fetchInitialData();
  }, [auth]);

  useEffect(() => {
    if (destination) {
      const fetchItems = async () => {
        setIsLoading(true);
        const fetchedItems = await getItems(destination);
        setItems(fetchedItems);
        setIsLoading(false);
      };
      fetchItems();
    }
  }, [destination]);

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

  const handleSortChange = (e) => {
    setSortOption(e.target.value);
  };

  const handlePageChange = (page) => {
    if (page >= 1 && page <= Math.ceil(items.length / itemsPerPage)) {
      setCurrentPage(page);
    }
  };

  if (isLoading || !locations.length) { // Adjust the condition to check for empty locations array
    return (
      <div>
        <Hero>Requisition Form</Hero>
        <LoadingIndicator />
      </div>
    );
  }

  return (
    <div>
      <Hero>Requisition Form</Hero>
      {destination ? (
        <div>
          <DestinationDetails
            locations={locations}
            destination={destination}
            handleChangeClick={handleChangeClick}
            items={items}
            handleSortChange={handleSortChange}
            sortOption={sortOption}
          />
          <Pagination
            currentPage={currentPage}
            totalPages={Math.ceil(items.length / itemsPerPage)}
            onPageChange={handlePageChange}
            top={true}
          />
          <ItemsGrid items={items} currentPage={currentPage} itemsPerPage={itemsPerPage} />
          <Pagination
            currentPage={currentPage}
            totalPages={Math.ceil(items.length / itemsPerPage)}
            onPageChange={handlePageChange}
            top={false}
          />
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
