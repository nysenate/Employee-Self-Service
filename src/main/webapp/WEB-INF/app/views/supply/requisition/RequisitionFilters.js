import React, { useState } from "react";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";
import { Button } from "app/components/Button";
import {
  resetFilters,
  setSort,
  setTerm,
} from "app/views/supply/requisition/itemFilterActions";

export default function RequisitionFilters({ filterState, dispatch }) {
  const [dirtyTerm, setDirtyTerm] = useState(filterState.term);
  const { destination, deleteDestination } = useSupplyContext();

  const handleInputChange = (e) => {
    setDirtyTerm(e.target.value);
  };

  const handleSave = () => {
    dispatch(setTerm(dirtyTerm));
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      handleSave();
    }
  };

  const reset = () => {
    setDirtyTerm("");
    dispatch(resetFilters());
  };

  return (
    <div className="flex justify-between items-baseline p-2">
      <div className="my-auto">
        <span className="font-bold text-purple-500 mr-3">Destination:</span>
        <Button variant="text" onClick={() => deleteDestination()}>
          [change]
        </Button>
        <br />
        {destination.code} ({destination.locationDescription})
      </div>

      <div className="my-auto">
        <label>
          Item name:
          <input
            id="term"
            name="term"
            type="text"
            className="input mx-2"
            value={dirtyTerm}
            onChange={handleInputChange}
            onKeyDown={handleKeyDown}
          />
        </label>
        <Button color="success" onClick={handleSave}>
          Search
        </Button>
        <Button color="secondary" className="mx-2" onClick={() => reset()}>
          Reset
        </Button>
      </div>

      <div className="my-auto">
        <label>
          Sort By:
          <select
            id="sort"
            name="sort"
            value={filterState.sort}
            className="select mx-2"
            onChange={(e) => dispatch(setSort(e.target.value))}
          >
            <option value="Name">Name</option>
            <option value="Category">Category</option>
          </select>
        </label>
      </div>
    </div>
  );
}
