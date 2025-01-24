import React from "react";
import { useContext, useEffect, useState } from "react";
import useLocalStorage from "app/views/useLocalStorage";


const SupplyContext = React.createContext()
const SUPPLY_DESTINATION_KEY = "supplyDestination"

// TODO add Cart
export function SupplyContextProvider({ children }) {
  const storage = useLocalStorage()
  const [destination, setDestination] = useState(() =>
    storage.load(SUPPLY_DESTINATION_KEY)
  );

  useEffect(() => {
    if (destination !== undefined) {
      storage.save(SUPPLY_DESTINATION_KEY, destination);
    }
  }, [destination]);

  const value = {
    destination,
    setDestination,
    deleteDestination: () => setDestination(null)
  };

  return (
    <SupplyContext.Provider value={value}>
      {children}
    </SupplyContext.Provider>
  );
}

export function useSupplyContext() {
  const context = useContext(SupplyContext);
  if (context === undefined) {
    throw new Error('useSupplyContext must be used within a SupplyContextProvider');
  }
  return context;
}