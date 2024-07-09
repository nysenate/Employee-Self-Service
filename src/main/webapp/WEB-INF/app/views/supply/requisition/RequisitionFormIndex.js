import React, { useEffect, useState } from "react";
import { useSearchParams } from 'react-router-dom';
import Hero from "../../../components/Hero";
import { OverOrderPopup, ChangeDestinationPopup } from "../../../components/Popups";
import styles from './RequisitionFormIndex.module.css';
import { Button } from "../../../components/Button";
import useAuth from "app/contexts/Auth/useAuth";
import LoadingIndicator from "app/components/LoadingIndicator";
import Pagination from "../../../components/Pagination";
import { clearCart, updateItemQuantity, getCartTotalQuantity } from '../cartUtils';
import { getItems, getLocations } from "../helpers";
import DestinationDetails from "./DestinationDetails";
import SelectDestination from "./SelectDestination";
import ItemsGrid from "./ItemsGrid";

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
  const [items, setItems] = useState([]);
  const itemsPerPage = 16;
  const [currentPage, setCurrentPage] = useState(1);
  const [cart, setCart] = useState(() => JSON.parse(localStorage.getItem('cart')) || {});
  // const [searchParams] = useSearchParams();
  const [filteredItems, setFilteredItems] = useState([]);
  // const selectedCategories = searchParams.getAll('category');

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
      };
      fetchItems();
    }
  }, [destination]);

  useEffect(() => {
    setFilteredItems(items);
    const uniqueCategories = new Set();
    items.forEach(item => {
      uniqueCategories.add(item.category);
    });
    const categoriesArray = Array.from(uniqueCategories);
    console.log(categoriesArray);
  }, [items]);

  // useEffect(() => {
  //   const updatedFilteredItems = items.filter(item => selectedCategories.includes(item.category));
  //   setFilteredItems(updatedFilteredItems);
  // }, [selectedCategories, items]);

  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(cart));
  }, [cart]);

  const handleOverOrderAttempt = (itemId, newQuantity) => {
    localStorage.setItem('pending', JSON.stringify(itemId));
    localStorage.setItem('pendingQuantity', JSON.stringify(newQuantity));
    setIsOverOrderPopupOpen(true);
  };

  const handleQuantityChange = (itemId, quantity) => {
    updateItemQuantity(itemId, quantity);
    setCart(JSON.parse(localStorage.getItem('cart')));
  };

  const handleConfirmClick = (tempDestination) => {
    setDestination(tempDestination);
    setCurrentPage(1);
    localStorage.setItem('destination', JSON.stringify(tempDestination));
  };

  const handleChangeClick = () => {
    if (cart) {
      setIsChangeDestinationPopupOpen(true);
    } else {
      setDestination(null);
      setItems([]);
      localStorage.removeItem('destination');
    }
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
  const handleChangeDestinationAction = (decision) => {
    if (decision) {
      clearCart();
      setDestination(null);
      setItems([]);
      localStorage.removeItem('destination');
    }
  };

  if (!locations.length || (destination && !items.length)) {
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
                  setItems={setItems}
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
