import React, { useContext, useEffect, useReducer, useState } from "react";
import useLocalStorage from "app/views/useLocalStorage";

const SupplyContext = React.createContext();
const SUPPLY_DESTINATION_KEY = "supplyDestination";
const SUPPLY_CART_KEY = "supplyCart";

const initialCartState = {
  items: {},
  totalItems: 0,
  specialInstructions: undefined,
};

const INCREMENT_ITEM = "INCREMENT";
const DECREMENT_ITEM = "DECREMENT_ITEM";
const UPDATE_QUANTITY = "UPDATE_QUANTITY";

const cartReducer = (state, action) => {
  switch (action.type) {
    case INCREMENT_ITEM: {
      const { itemId } = action.payload;
      const currentQuantity = state.items[itemId] || 0;
      const newItems = {
        ...state.items,
        [itemId]: currentQuantity + 1,
      };
      return {
        ...state,
        items: newItems,
        totalItems: calculateTotalItems(newItems),
      };
    }
    case DECREMENT_ITEM: {
      const { itemId } = action.payload;
      const newQuantity = (state.items[itemId] || 0) - 1;
      const newItems = {
        ...state.items,
        [itemId]: newQuantity,
      };
      if (newQuantity <= 0) {
        delete newItems[itemId];
      }
      return {
        ...state,
        items: newItems,
        totalItems: calculateTotalItems(newItems),
      };
    }
    case UPDATE_QUANTITY: {
      const { itemId, quantity } = action.payload;
      const newItems = {
        ...state.items,
        [itemId]: quantity,
      };
      if (quantity <= 0) {
        delete newItems[itemId];
      }
      return {
        ...state,
        items: newItems,
        totalItems: calculateTotalItems(newItems),
      };
    }
    default: {
      return state;
    }
  }
};

const calculateTotalItems = (items) => {
  return Object.values(items).reduce((total, quantity) => total + quantity, 0);
};

export function SupplyContextProvider({ children }) {
  const storage = useLocalStorage();
  const [destination, setDestination] = useState(() =>
    storage.load(SUPPLY_DESTINATION_KEY),
  );

  const getInitialCartState = () => {
    const savedCart = storage.load(SUPPLY_CART_KEY);
    return savedCart || initialCartState;
  };

  const [cart, dispatch] = useReducer(cartReducer, null, getInitialCartState);

  useEffect(() => {
    if (destination !== undefined) {
      storage.save(SUPPLY_DESTINATION_KEY, destination);
    }
  }, [destination]);

  useEffect(() => {
    storage.save(SUPPLY_CART_KEY, cart);
  }, [cart]);

  // Cart Actions
  const incrementItem = (itemId) => {
    dispatch({ type: INCREMENT_ITEM, payload: { itemId } });
  };

  const decrementItem = (itemId) => {
    dispatch({ type: DECREMENT_ITEM, payload: { itemId } });
  };

  const updateQuantity = (itemId, quantity) => {
    dispatch({ type: UPDATE_QUANTITY, payload: { itemId, quantity } });
  };

  const value = {
    destination,
    setDestination,
    deleteDestination: () => setDestination(null),
    cart,
    incrementItem,
    decrementItem,
    updateQuantity,
  };

  return (
    <SupplyContext.Provider value={value}>{children}</SupplyContext.Provider>
  );
}

export function useSupplyContext() {
  const context = useContext(SupplyContext);
  if (context === undefined) {
    throw new Error(
      "useSupplyContext must be used within a SupplyContextProvider",
    );
  }
  return context;
}

function Cart() {}
