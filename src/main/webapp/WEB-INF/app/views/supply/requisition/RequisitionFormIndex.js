import React, { useEffect, useState } from "react";
import Hero from "../../../components/Hero";
import { OverOrderPopup } from "../../../components/Popups";
import styles from './RequisitionFormIndex.module.css';
import { Button } from "../../../components/Button";
import { fetchApiJson } from "app/utils/fetchJson";
import useAuth from "app/contexts/Auth/useAuth";
import LoadingIndicator from "app/components/LoadingIndicator";
import Pagination from "./Pagination";
import { incrementItem, decrementItem, clearCart, updateItemQuantity } from '../cartUtils';

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

//Items
const ItemsGrid = ({ items, currentPage, itemsPerPage, cart, handleQuantityChange, handleOverOrderAttempt }) => {
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentItems = items.slice(startIndex, startIndex + itemsPerPage);

  return (
    <div className={styles.itemGrid}>
      {currentItems.map((item) => (
        <ItemDisplay
          key={item.id}
          item={item}
          cart={cart}
          handleQuantityChange={handleQuantityChange}
          handleOverOrderAttempt={handleOverOrderAttempt}
        />
      ))}
    </div>
  );
};
const ItemDisplay = ({ item, cart, handleQuantityChange, handleOverOrderAttempt }) => {
  const itemInCart = cart[item.id];
  const isMaxQuantity = itemInCart && itemInCart >= item.perOrderAllowance;
  const [localValue, setLocalValue] = useState(cart[item.id] || 0);

  // Synchronize localValue and itemInCart
  useEffect(() => {
    setLocalValue(itemInCart || 0);
  }, [itemInCart]);

  const handleTempInputChange = (e) => {
    const { value } = e.target;
    if (/^\d*$/.test(value)) { // Only allow numbers
      setLocalValue(value);
    }
  };

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
      {itemInCart ? (
        <div className={styles.itemQuantities}>
          <p>{item.unit}</p>
          <div className={styles.itemInputs}>
            {/* Decrement Button */}
            <button
              className={styles.qtyAdjustButton}
              onClick={() => handleQuantityChange(item.id, Math.max(0, parseInt(localValue, 10) - 1))}
            >
                -
            </button>

            {/* Quantity Input */}
            <input
              className={styles.qtyInput}
              style={{ color: parseInt(localValue, 10) > item.perOrderAllowance ? 'red' : '' }}
              type="text"
              value={localValue}
              onChange={handleTempInputChange}
              onBlur={() => {
                const numericLocalValue = parseInt(localValue, 10); // Ensure it's a number
                if (numericLocalValue > item.perOrderAllowance && itemInCart <= item.perOrderAllowance) {
                  handleOverOrderAttempt(item.id, numericLocalValue);
                  setLocalValue(cart[item.id]);
                } else {
                  handleQuantityChange(item.id, numericLocalValue);
                }
              }}
            />

            {/* Increment Button */}
            <button
              className={styles.qtyAdjustButton}
              onClick={() => {
                const numericLocalValue = parseInt(localValue, 10); // Ensure it's a number
                if (numericLocalValue === item.perOrderAllowance) {
                  handleOverOrderAttempt(item.id, numericLocalValue + 1);
                } else {
                  handleQuantityChange(item.id, numericLocalValue + 1);
                }
              }}
              style={{ backgroundColor: isMaxQuantity ? 'red' : '' }}
          >
              +
            </button>
          </div>
        </div>
      ) : (
         <Button onClick={() => handleQuantityChange(item.id, localValue+1)}>Add to Cart</Button>
       )}
    </div>
  );
};
//End Items

const getLocations = async (empId) => {
  return await fetchApiJson(`/supply/destinations/${empId}`).then((body) => body);
};

const getItems = async (locId) => {
  return await fetchApiJson(`/supply/items/orderable/${locId}`).then((body) => body.result);
};

export default function RequisitionFormIndex() {
  const auth = useAuth();
  const [locations, setLocations] = useState([]);
  const [destination, setDestination] = useState(() => {
    try {
      const destinationData = localStorage.getItem('destination');
      return destinationData && destinationData !== "undefined" ? JSON.parse(destinationData) : null;
    } catch (e) {
      console.error('Failed to parse destination from localStorage:', e);
      return null;
    }
  });
  const [tempDestination, setTempDestination] = useState(destination);
  const [items, setItems] = useState([]);
  const itemsPerPage = 16;
  const [currentPage, setCurrentPage] = useState(1);
  const [sortOption, setSortOption] = useState('name');
  // const [isLoading, setIsLoading] = useState(false);
  const [cart, setCart] = useState(() => JSON.parse(localStorage.getItem('cart')) || {});

  useEffect(() => {
    localStorage.removeItem('pending'); //Clean up pending if refresh occured before popup conclusion
    localStorage.removeItem('pendingQuantity'); //Clean up pending if refresh occured before popup conclusion
  }, []);

  useEffect(() => {
    const fetchInitialData = async () => {
      // setIsLoading(true);
      const fetchedLocations = await getLocations(auth.empId());
      setLocations(fetchedLocations.result);
      // setIsLoading(false);
    };
    fetchInitialData();
  }, [auth]);

  useEffect(() => {
    if (destination) {
      const fetchItems = async () => {
        const fetchedItems = await getItems(destination.locId);
        setItems(fetchedItems);
      };
      fetchItems();
    }
  }, [destination]);

  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(cart));
  }, [cart]);

  const handleOverOrderAttempt = (itemId, newQuantity) => {
    localStorage.setItem('pending', JSON.stringify(itemId));
    localStorage.setItem('pendingQuantity', JSON.stringify(newQuantity));
    openOverOrderPopup();
  }
  const handleQuantityChange = (itemId, quantity) => {
    updateItemQuantity(itemId, quantity);
    setCart(JSON.parse(localStorage.getItem('cart')));
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
    localStorage.setItem('destination', JSON.stringify(tempDestination));
  };
  const handleChangeClick = () => {
    setTempDestination(null);
    setDestination(null);
    setItems([]);
    localStorage.removeItem('destination');
  };
  const handleSortChange = (e) => {
    setSortOption(e.target.value);
    setItems(sortItems(items, e.target.value));
  };
  const handlePageChange = (page) => {
    if (page >= 1 && page <= Math.ceil(items.length / itemsPerPage)) {
      setCurrentPage(page);
    }
  };

  //POPUPS START:
  const [isOverOrderPopupOpen, setIsOverOrderPopupOpen] = useState(false);

  const openOverOrderPopup = () => {
    setIsOverOrderPopupOpen(true);
  };
  const closeOverOrderPopup = () => {
    setIsOverOrderPopupOpen(false);
  };
  const handleOverOrderAction = (decision) => {
    if(decision) {
      //retrieve pending:
      let pending = JSON.parse(localStorage.getItem('pending'));
      let pendingQuantity = JSON.parse(localStorage.getItem('pendingQuantity'));
      handleQuantityChange(pending, pendingQuantity);
    }
    localStorage.removeItem('pending');
    localStorage.removeItem('pendingQuantity');
  }

  if (!locations.length || (destination &&!items.length)) {
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
          <ItemsGrid
            items={items}
            currentPage={currentPage}
            itemsPerPage={itemsPerPage}
            cart={cart}
            handleQuantityChange={handleQuantityChange}
            handleOverOrderAttempt={handleOverOrderAttempt}
          />
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
      <OverOrderPopup
        isModalOpen={isOverOrderPopupOpen}
        closeModal={closeOverOrderPopup}
        onAction={handleOverOrderAction}
      />
    </div>
  );
}
