import React, { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import Hero from "app/components/Hero";
import { ChangeDestinationPopup, OverOrderPopup } from "app/components/Popups";
import styles from "../universalStyles.module.css";
import useAuth from "app/contexts/Auth/useAuth";
import LoadingIndicator from "app/components/LoadingIndicator";
import { clearCart, updateItemQuantity } from "../cartUtils";
import { getItems, getLocations } from "../helpers";
import DestinationDetails from "./DestinationDetails";
import SelectDestination from "./SelectDestination";
import CartSummary from "app/views/supply/requisition/CartSummary";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";
import ItemListing from "app/views/supply/requisition/ItemListing";

export default function RequisitionFormIndex({ setCategories }) {
  const auth = useAuth();
  const { destination, deleteDestination } = useSupplyContext();

  const [items, setItems] = useState([]);
  const [cart, setCart] = useState(
    () => JSON.parse(localStorage.getItem("cart")) || {},
  );
  const [sortOption, setSortOption] = useState("name");
  const [filteredItems, setFilteredItems] = useState([]);

  useEffect(() => {
    localStorage.removeItem("pending"); // Clean up pending if refresh occurred before popup conclusion
    localStorage.removeItem("pendingQuantity"); // Clean up pending if refresh occurred before popup conclusion
  }, []);

  useEffect(() => {
    localStorage.setItem("cart", JSON.stringify(cart));
  }, [cart]);

  const handleOverOrderAttempt = (itemId, newQuantity) => {
    localStorage.setItem("pending", JSON.stringify(itemId));
    localStorage.setItem("pendingQuantity", JSON.stringify(newQuantity));
    setIsOverOrderPopupOpen(true);
  };

  const handleQuantityChange = (itemId, quantity) => {
    updateItemQuantity(itemId, quantity);
    setCart(JSON.parse(localStorage.getItem("cart")));
  };

  const handleSortChange = (e) => {
    setSortOption(e.target.value);
    setItems(sortItems(items, e.target.value));
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
    deleteDestination();
    setItems([]);
    setFilteredItems([]);
    setCategories([]);
    localStorage.removeItem("destination");
    localStorage.removeItem("pending");
    localStorage.removeItem("pendingQuantity");
    const newParams = new URLSearchParams();
    //setSearchParams(newParams);
  };

  const [isOverOrderPopupOpen, setIsOverOrderPopupOpen] = useState(false);
  const [isChangeDestinationPopupOpen, setIsChangeDestinationPopupOpen] =
    useState(false);
  const closeOverOrderPopup = () => {
    setIsOverOrderPopupOpen(false);
  };
  const closeChangeDestinationPopup = () => {
    setIsChangeDestinationPopupOpen(false);
  };
  const handleOverOrderAction = (decision) => {
    if (decision) {
      const pending = JSON.parse(localStorage.getItem("pending"));
      const pendingQuantity = JSON.parse(
        localStorage.getItem("pendingQuantity"),
      );
      handleQuantityChange(pending, pendingQuantity);
    }
    localStorage.removeItem("pending");
    localStorage.removeItem("pendingQuantity");
  };
  const handleChangeDestinationAction = (decision) => {
    if (decision) fullWipe();
  };

  if (!destination) {
    return <SelectDestination />;
  }

  if (!destination && !filteredItems.length) {
    return (
      <div>
        <Hero>Requisition Form</Hero>
        <LoadingIndicator />
      </div>
    );
  }

  return (
    <div>
      <div
        className={styles.supplyOrderHero}
        style={{ display: "inline-block", width: "100%" }}
      >
        <h2 className={styles.requisitionTitle}>Requisition Form</h2>
        <a href={"/supply/cart"}>
          <CartSummary cart={cart} />
        </a>
      </div>
      {destination ? (
        <div>
          <DestinationDetails
            destination={destination}
            handleChangeClick={handleChangeClick}
            sortOption={sortOption}
            handleSortChange={handleSortChange}
          />
          <ItemListing
            cart={cart}
            handleQuantityChange={handleQuantityChange}
            handleOverOrderAttempt={handleOverOrderAttempt}
            setCategories={setCategories}
          />
        </div>
      ) : (
        <></>
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

const sortItems = (items, sortOption) => {
  if (sortOption === "name") {
    return [...items].sort((a, b) =>
      a.description.localeCompare(b.description),
    );
  }
  if (sortOption === "category") {
    return [...items].sort((a, b) => a.category.localeCompare(b.category));
  }
  return items;
};
