import React, { useEffect, useState } from "react";
import Hero from "../../../components/Hero";
import styles from './RequisitionFormIndex.module.css';
import { Button } from "../../../components/Button";
import { fetchApiJson } from "app/utils/fetchJson";
import useAuth from "app/contexts/Auth/useAuth";
import LoadingIndicator from "app/components/LoadingIndicator";
import Pagination from "./Pagination";
import { incrementItem, decrementItem, clearCart } from '../cartUtils';

const SelectDestination = ({ locations, tempDestination, handleTempDestinationChange, handleConfirmClick }) => {
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
        <Button onClick={handleConfirmClick}>Confirm</Button>
      </div>
    </div>
  );
};

const DestinationDetails = ({ destination, handleChangeClick, items, handleSortChange, sortOption }) => {
  const location = destination; // destination now holds the entire location object

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

const ItemsGrid = ({ items, currentPage, itemsPerPage, addToCart }) => {
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentItems = items?.slice(startIndex, startIndex + itemsPerPage);

  return (
    <div className={styles.itemGrid}>
      {currentItems?.map((item) => (
        <ItemDisplay key={item.id} item={item} addToCart={addToCart} />
      ))}
    </div>
  );
};

const ItemDisplay = ({ item, addToCart }) => {
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
        <h4 style={{fontWeight: 'medium'}}>{item.description}</h4>
        <p>{item.unit}</p>
      </div>
      <Button onClick={() => addToCart(item.id)}>Add to Cart</Button>
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
  const [destination, setDestination] = useState(null); // Update to hold the entire location object
  const [tempDestination, setTempDestination] = useState(null);
  const [items, setItems] = useState([]);
  const itemsPerPage = 16; // Adjust the number of items per page as needed
  const [currentPage, setCurrentPage] = useState(1);
  const [sortOption, setSortOption] = useState('name');
  const [isLoading, setIsLoading] = useState(false);
  const [cart, setCart] = useState(JSON.parse(localStorage.getItem('cart')) || {});

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
        const fetchedItems = await getItems(destination.locId);
        setItems(fetchedItems);
        setIsLoading(false);
      };
      fetchItems();
      localStorage.setItem('destinationLocId', JSON.stringify(destination.locId));
      localStorage.setItem('destinationLocationDescription', JSON.stringify(destination.locationDescription));
    }
  }, [destination]);

  useEffect(() => { localStorage.setItem('cart', JSON.stringify(cart)); }, [cart]);
  const handleIncrement = (itemId) => {
    incrementItem(itemId);
    setCart(JSON.parse(localStorage.getItem('cart')));
  };
  const handleDecrement = (itemId) => {
    decrementItem(itemId);
    setCart(JSON.parse(localStorage.getItem('cart')));
  };
  const handleClearCart = () => {
    clearCart();
    setCart({});
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

  const handleTempDestinationChange = (locId) => {
    const selectedLocation = locations.find((loc) => loc.locId === locId);
    setTempDestination(selectedLocation);
  };

  const handleConfirmClick = () => {
    setDestination(tempDestination);
    setCurrentPage(1);
  };

  const handleChangeClick = () => {
    setTempDestination(null);
    setDestination(null);
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
          <ItemsGrid items={items} currentPage={currentPage} itemsPerPage={itemsPerPage} addToCart={handleIncrement}/>
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
