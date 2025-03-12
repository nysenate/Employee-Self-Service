import React, { useEffect, useReducer, useState } from "react";
import Hero from "app/components/Hero";
import styles from "../universalStyles.module.css";
import LoadingIndicator from "app/components/LoadingIndicator";
import SelectDestination from "./SelectDestination";
import CartSummary from "app/views/supply/requisition/CartSummary";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";
import ItemListing from "app/views/supply/requisition/ItemListing";
import RequisitionFilters from "app/views/supply/requisition/RequisitionFilters";
import Controls from "app/components/Controls";
import {
  CLEAR_CATEGORIES,
  RESET_FILTERS,
  SET_PAGE,
  SET_SORT,
  SET_TERM,
  TOGGLE_CATEGORY,
} from "app/views/supply/requisition/itemFilterActions";
import CategoryCard from "app/views/supply/requisition/CategoryCard";
import Navigation from "app/components/Navigation";

const initFilterState = {
  term: "",
  categories: [],
  sort: "Name",
  limit: 16,
  offset: 1,
  page: 1,
};

function itemFilterReducer(state, action) {
  switch (action.type) {
    case SET_TERM:
      return {
        ...state,
        term: action.payload.term,
      };
    case SET_PAGE:
      return {
        ...state,
        page: action.payload.page,
        offset: action.payload.page * state.limit - state.limit + 1,
      };
    case SET_SORT:
      return {
        ...state,
        sort: action.payload.sort,
      };
    case RESET_FILTERS:
      return {
        ...initFilterState,
      };
    case TOGGLE_CATEGORY:
      let categories = state.categories;
      if (action.payload.checked) {
        categories.push(action.payload.category);
      } else {
        categories = categories.filter((c) => c !== action.payload.category);
      }
      return {
        ...state,
        categories: [...new Set(categories)], // Remove any duplicates.
      };
    case CLEAR_CATEGORIES:
      return {
        ...state,
        categories: [],
      };
    default:
      return {
        ...state,
      };
  }
}

export default function RequisitionFormIndex({ setCategories }) {
  const { cart, destination, deleteDestination } = useSupplyContext();
  const [items, setItems] = useState([]);
  const [filteredItems, setFilteredItems] = useState([]);

  const [filterState, dispatch] = useReducer(
    itemFilterReducer,
    initFilterState,
  );

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
        <div className="">
          <Controls>
            <RequisitionFilters filterState={filterState} dispatch={dispatch} />
          </Controls>
        </div>
        <ItemListing
          filterState={filterState}
          dispatch={dispatch}
          setCategories={setCategories}
        />
        {/*<OverOrderPopup*/}
        {/*  isModalOpen={isOverOrderPopupOpen}*/}
        {/*  closeModal={closeOverOrderPopup}*/}
        {/*  onAction={handleOverOrderAction}*/}
        {/*/>*/}
        {/*<ChangeDestinationPopup*/}
        {/*  isModalOpen={isChangeDestinationPopupOpen}*/}
        {/*  closeModal={closeChangeDestinationPopup}*/}
        {/*  onAction={handleChangeDestinationAction}*/}
        {/*/>*/}
      </div>
      <div className="absolute left-0 top-[370px]">
        <Navigation>
          <Navigation.Title>Categories</Navigation.Title>
          <CategoryCard filterState={filterState} dispatch={dispatch} />
        </Navigation>
      </div>
    </div>
  );
}
