import React, { useEffect, useReducer, useState } from "react";
import Hero from "app/components/Hero";
import LoadingIndicator from "app/components/LoadingIndicator";
import SelectDestination from "app/views/supply/store/shop/components/SelectDestination";
import CartSummary from "app/views/supply/store/shop/components/CartSummary";
import { useSupplyContext } from "app/views/supply/store/useSupplyContext";
import ItemListing from "app/views/supply/store/shop/components/ItemListing";
import RequisitionFilters from "app/views/supply/store/shop/components/RequisitionFilters";
import Controls from "app/components/Controls";
import {
  CLEAR_CATEGORIES,
  RESET_FILTERS,
  SET_OFFSET,
  SET_SORT,
  SET_TERM,
  TOGGLE_CATEGORY,
} from "app/views/supply/store/shop/utils/itemFilterActions";
import CategoryCard from "app/views/supply/store/shop/components/CategoryCard";
import Navigation from "app/components/Navigation";
import { Link } from "react-router-dom";
import { createPortal } from "react-dom";

const initFilterState = {
  term: "",
  categories: [],
  sort: "Name",
  limit: 16,
  offset: 1,
};

function itemFilterReducer(state, action) {
  switch (action.type) {
    case SET_TERM:
      return {
        ...state,
        term: action.payload.term,
        offset: 1,
      };
    case SET_OFFSET:
      return {
        ...state,
        offset: action.payload.offset,
      };
    case SET_SORT:
      return {
        ...state,
        sort: action.payload.sort,
        offset: 1,
      };
    case RESET_FILTERS:
      return {
        ...initFilterState,
      };
    case TOGGLE_CATEGORY:
      let updatedCategories;
      if (action.payload.checked) {
        updatedCategories = [...state.categories, action.payload.category];
      } else {
        updatedCategories = state.categories.filter(
          (c) => c !== action.payload.category,
        );
      }
      return {
        ...state,
        categories: [...new Set(updatedCategories)], // Remove any duplicates.
        offset: 1,
      };
    case CLEAR_CATEGORIES:
      return {
        ...state,
        categories: [],
        offset: 1,
      };
    default:
      return {
        ...state,
      };
  }
}

export default function ShopIndex() {
  const { cart, destination, deleteDestination } = useSupplyContext();
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
    <>
      <div>
        <Hero>
          Requisition Form
          <Link to="/supply/cart" aria-label="Go to Shopping Cart">
            <CartSummary cart={cart} />
          </Link>
        </Hero>
        <div>
          <Controls>
            <RequisitionFilters filterState={filterState} dispatch={dispatch} />
          </Controls>
        </div>
        <ItemListing filterState={filterState} dispatch={dispatch} />
      </div>
      <div className="absolute top-[370px] left-[20px]">
        <Categories filterState={filterState} dispatch={dispatch} />
      </div>
    </>
  );
}

function Categories({ filterState, dispatch }) {
  const [domReady, setDomReady] = React.useState(false);

  React.useEffect(() => {
    setDomReady(true);
  }, []);

  return domReady
    ? createPortal(
        <Navigation>
          <Navigation.Title>Categories</Navigation.Title>
          <CategoryCard filterState={filterState} dispatch={dispatch} />
        </Navigation>,
        document.getElementById("categories-portal"),
      )
    : null;
}
