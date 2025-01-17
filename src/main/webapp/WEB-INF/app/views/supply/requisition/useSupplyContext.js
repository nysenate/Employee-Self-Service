import React from "react";
import * as essLocalStorage from "app/utils/essLocalStorage";
import { useContext, useEffect, useState } from "react";


const SupplyContext = React.createContext()

const SUPPLY_DESTINATION_KEY = "supplyDestination"

// TODO add Cart
export function SupplyContextProvider({ children }) {
  const [destination, setDestination] = useState(() =>
    essLocalStorage.load(SUPPLY_DESTINATION_KEY)
  );

  useEffect(() => {
    if (destination !== undefined) {
      essLocalStorage.save(SUPPLY_DESTINATION_KEY, destination);
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