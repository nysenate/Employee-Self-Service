import React, { useEffect, useState } from "react";
import Hero from "../../../components/Hero";
import { OverOrderPopup, ChangeDestinationPopup } from "../../../components/Popups";
import styles from './RequisitionFormIndex.module.css';
import { Button } from "../../../components/Button";
import { fetchApiJson } from "app/utils/fetchJson";
import useAuth from "app/contexts/Auth/useAuth";
import LoadingIndicator from "app/components/LoadingIndicator";
import Pagination from "../../../components/Pagination";
import { clearCart, updateItemQuantity, getCartTotalQuantity } from '../cartUtils';
import { getItems, getLocations} from "../helpers";

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
  const [cart, setCart] = useState(() => JSON.parse(localStorage.getItem('cart')) || {});
  const [categories, setCategories] = useState([]);
  // const [totalItems, setTotalItems] = useState(0);

  //Clean temp localStorage on fresh wipe
  useEffect(() => {
    localStorage.removeItem('pending'); //Clean up pending if refresh occured before popup conclusion
    localStorage.removeItem('pendingQuantity'); //Clean up pending if refresh occured before popup conclusion
  }, []);

  useEffect(() => {
    const fetchInitialData = async () => {
      const fetchedLocations = await getLocations(auth.empId());
      setLocations(fetchedLocations.result);
    };
    fetchInitialData();
  }, [auth]);

  //Update Items on new Destination
  useEffect(() => {
    if (destination) {
      const fetchItems = async () => {
        const fetchedItems = await getItems(destination.locId);
        setItems(fetchedItems);
      };
      fetchItems();
    }
  }, [destination]);

  //Update Categories on new Items
  useEffect(() => {
    const uniqueCategories = new Set();
    items.forEach(item => {
      uniqueCategories.add(item.category);
    });
    setCategories(Array.from(uniqueCategories));
  }, [items]);

  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(cart));
    // setTotalItems(getCartTotalQuantity);
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
    if(cart) {
      openChangeDestinationPopup();
    }else{
      setTempDestination(null);
      setDestination(null);
      setItems([]);
      localStorage.removeItem('destination');
    }
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
  const [isChangeDestinationPopupOpen, setIsChangeDestinationPopupOpen] = useState(false);

  const openOverOrderPopup = () => {
    setIsOverOrderPopupOpen(true);
  };
  const closeOverOrderPopup = () => {
    setIsOverOrderPopupOpen(false);
  };
  const openChangeDestinationPopup = () => {
    setIsChangeDestinationPopupOpen(true);
  };
  const closeChangeDestinationPopup = () => {
    setIsChangeDestinationPopupOpen(false);
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
  const handleChangeDestinationAction = (decision) => {
    if(decision) {
      clearCart();
      setTempDestination(null);
      setDestination(null);
      setItems([]);
      localStorage.removeItem('destination');
    }
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
      <Hero>
        Requisition Form
        {/*<CartIconAndCount totalItems={totalItems}/>*/}
      </Hero>
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
      <ChangeDestinationPopup
        isModalOpen={isChangeDestinationPopupOpen}
        closeModal={closeChangeDestinationPopup}
        onAction={handleChangeDestinationAction}
      />
    </div>
  );
}

const CartIcon = () => {
  return (
      <svg version="1.1" className="" xmlns="http://www.w3.org/2000/svg" xmlnsXlink="http://www.w3.org/1999/xlink" width="30px" height="30px" xmlSpace="preserve">
        <path style={{ fill: 'white' }} d="M27.715,10.48l-2.938,6.312c-0.082,0.264-0.477,0.968-1.318,0.968H11.831
        c-0.89,0-1.479-0.638-1.602-0.904l-2.048-6.524C7.629,8.514,8.715,7.933,9.462,7.933c0.748,0,14.915,0,16.805,0
        C27.947,7.933,28.17,9.389,27.715,10.48L27.715,10.48z M9.736,9.619c0.01,0.061,0.026,0.137,0.056,0.226l1.742,6.208
        c0.026,0.017,0.058,0.028,0.089,0.028h11.629l2.92-6.27c0.025-0.073,0.045-0.137,0.053-0.192H9.736L9.736,9.619z M13.544,25.534
        c-0.819,0-1.482-0.662-1.482-1.482s0.663-1.484,1.482-1.484c0.824,0,1.486,0.664,1.486,1.484S14.369,25.534,13.544,25.534
        L13.544,25.534z M23.375,25.534c-0.82,0-1.482-0.662-1.482-1.482s0.662-1.484,1.482-1.484c0.822,0,1.486,0.664,1.486,1.484
        S24.197,25.534,23.375,25.534L23.375,25.534z M24.576,21.575H13.965c-2.274,0-3.179-2.151-3.219-2.244
        c-0.012-0.024-0.021-0.053-0.028-0.076c0,0-3.56-12.118-3.834-13.05c-0.26-0.881-0.477-1.007-1.146-1.007H2.9
        c-0.455,0-0.82-0.364-0.82-0.818s0.365-0.82,0.82-0.82h2.841c1.827,0,2.4,1.103,2.715,2.181
        c0.264,0.898,3.569,12.146,3.821,12.999c0.087,0.188,0.611,1.197,1.688,1.197h10.611c0.451,0,0.818,0.368,0.818,0.818
        C25.395,21.21,25.027,21.575,24.576,21.575L24.576,21.575z"></path>
      </svg>
  );
};

const CartIconAndCount = ({totalItems}) => {
  return (
        <a href="/supply/shopping/cart/cart">
          <div className={styles.cartWidget}>
            <div style={{ width: '100px'}}>
              <div style={{ float: 'left', paddingRight: '20px'}}>
                <CartIcon/>
              </div>
              <div style={{ fontWeight: '700', fontSize: '13px', float: 'left', paddingRight: '20px'}}>
                {totalItems}
                <div style={{display: 'inline'}}> items</div>
              </div >
            </div>
          </div>
        </a>
  );
}