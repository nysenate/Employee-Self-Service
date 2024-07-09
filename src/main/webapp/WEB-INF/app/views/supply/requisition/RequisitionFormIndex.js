import React, { useEffect, useState } from "react";
import { useSearchParams } from 'react-router-dom';
import Hero from "../../../components/Hero";
import { OverOrderPopup, ChangeDestinationPopup } from "../../../components/Popups";
import styles from '../universalStyles.module.css';
import { Button } from "../../../components/Button";
import useAuth from "app/contexts/Auth/useAuth";
import LoadingIndicator from "app/components/LoadingIndicator";
import Pagination from "../../../components/Pagination";
import { clearCart, updateItemQuantity, getCartTotalQuantity } from '../cartUtils';
import { getItems, getLocations } from "../helpers";
import DestinationDetails from "./DestinationDetails";
import SelectDestination from "./SelectDestination";
import ItemsGrid from "./ItemsGrid";




export default function RequisitionFormIndex({ setCategories }) {
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
  const [items, setItems] = useState([]);
  const itemsPerPage = 16;
  const [currentPage, setCurrentPage] = useState(1);
  const [cart, setCart] = useState(() => JSON.parse(localStorage.getItem('cart')) || {});
  const [sortOption, setSortOption] = useState('name');
  const [searchParams, setSearchParams] = useSearchParams();
  const [filteredItems, setFilteredItems] = useState([]);
  const selectedCategories = searchParams.getAll('category');

  useEffect(() => {
    localStorage.removeItem('pending'); // Clean up pending if refresh occurred before popup conclusion
    localStorage.removeItem('pendingQuantity'); // Clean up pending if refresh occurred before popup conclusion
  }, []);

  useEffect(() => {
    const fetchInitialData = async () => {
      const fetchedLocations = await getLocations(auth.empId());
      setLocations(fetchedLocations.result);
    };
    fetchInitialData();
  }, [auth]);

  useEffect(() => {
    if (destination) {
      const fetchItems = async () => {
        const fetchedItems = await getItems(destination.locId);
        setItems(fetchedItems);
        const categories = extractCategoriesFromItems(fetchedItems);
        setCategories(categories);
      };
      fetchItems();
    }
  }, [destination]);

  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(cart));
  }, [cart]);

  useEffect(() => {
    if(selectedCategories.length === 0) {
      setFilteredItems(items);
      return;
    }
    const updatedFilteredItems = items.filter(item => selectedCategories.includes(item.category));
    if (!arraysAreEqual(filteredItems, updatedFilteredItems)) {
      setFilteredItems(updatedFilteredItems);
    }
  }, [selectedCategories, items]);


  const handleOverOrderAttempt = (itemId, newQuantity) => {
    localStorage.setItem('pending', JSON.stringify(itemId));
    localStorage.setItem('pendingQuantity', JSON.stringify(newQuantity));
    setIsOverOrderPopupOpen(true);
  };

  const handleQuantityChange = (itemId, quantity) => {
    updateItemQuantity(itemId, quantity);
    setCart(JSON.parse(localStorage.getItem('cart')));
  };

  const handleSortChange = (e) => {
    setSortOption(e.target.value);
    setItems(sortItems(items, e.target.value));
  };

  const handleConfirmClick = (tempDestination) => {
    setDestination(tempDestination);
    setCurrentPage(1);
    localStorage.setItem('destination', JSON.stringify(tempDestination));
  };

  const handleChangeClick = () => {
    const isCartEmpty = cart && Object.keys(cart).length === 0;
    if (!isCartEmpty) {
      setIsChangeDestinationPopupOpen(true);
    } else {
      fullWipe();
    }
  };

  const fullWipe = () => {
    clearCart();
    setCart({});
    setDestination(null);
    setItems([]);
    setFilteredItems([]);
    setCategories([]);
    localStorage.removeItem('destination');
    localStorage.removeItem('pending');
    localStorage.removeItem('pendingQuantity');
    const newParams = new URLSearchParams();
    setSearchParams(newParams);
  };

  const handlePageChange = (page) => {
    if (page >= 1 && page <= Math.ceil(items.length / itemsPerPage)) {
      setCurrentPage(page);
    }
  };

  const [isOverOrderPopupOpen, setIsOverOrderPopupOpen] = useState(false);
  const [isChangeDestinationPopupOpen, setIsChangeDestinationPopupOpen] = useState(false);
  const closeOverOrderPopup = () => {
    setIsOverOrderPopupOpen(false);
  };
  const closeChangeDestinationPopup = () => {
    setIsChangeDestinationPopupOpen(false);
  };
  const handleOverOrderAction = (decision) => {
    if (decision) {
      const pending = JSON.parse(localStorage.getItem('pending'));
      const pendingQuantity = JSON.parse(localStorage.getItem('pendingQuantity'));
      handleQuantityChange(pending, pendingQuantity);
    }
    localStorage.removeItem('pending');
    localStorage.removeItem('pendingQuantity');
  };
  const handleChangeDestinationAction = (decision) => { if (decision) fullWipe(); };

  if (!locations.length || (destination && !filteredItems.length)) {
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
        </Hero>
        {destination ? (
            <div>
              <DestinationDetails
                  destination={destination}
                  handleChangeClick={handleChangeClick}
                  sortOption={sortOption}
                  handleSortChange={handleSortChange}
              />
              {filteredItems.length > itemsPerPage && (
                <Pagination
                  currentPage={currentPage}
                  totalPages={Math.ceil(filteredItems.length / itemsPerPage)}
                  onPageChange={handlePageChange}
                />
              )}
              <ItemsGrid
                  items={filteredItems}
                  currentPage={currentPage}
                  itemsPerPage={itemsPerPage}
                  cart={cart}
                  handleQuantityChange={handleQuantityChange}
                  handleOverOrderAttempt={handleOverOrderAttempt}
              />
              {filteredItems.length > itemsPerPage && (
                <div className={styles.contentContainer}>
                  <Pagination
                    currentPage={currentPage}
                    totalPages={Math.ceil(filteredItems.length / itemsPerPage)}
                    onPageChange={handlePageChange}
                  />
                </div>
              )}
            </div>
        ) : (
            <SelectDestination
                locations={locations}
                currentDestination={destination}
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

function extractCategoriesFromItems(items) {
  const uniqueCategories = new Set();
  items.forEach(item => {
    uniqueCategories.add(item.category);
  });
  const categoriesArray = Array.from(uniqueCategories);
  return categoriesArray;
}

const arraysAreEqual = (arr1, arr2) => {
  if (arr1.length !== arr2.length) return false;
  for (let i = 0; i < arr1.length; i++) {
    if (arr1[i] !== arr2[i]) return false;
  }
  return true;
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