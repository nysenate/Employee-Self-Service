import React, { useState } from "react";
import { useSupplyContext } from "app/views/supply/store/useSupplyContext";
import Button from "app/components/Button";
import {
  resetFilters,
  setSort,
  setTerm,
} from "app/views/supply/store/shop/utils/itemFilterActions";
import Modal from "app/components/Modal";

export default function RequisitionFilters({ filterState, dispatch }) {
  const [dirtyTerm, setDirtyTerm] = useState(filterState.term);
  const { destination, deleteDestination } = useSupplyContext();
  const { isOpen, setOpen, onResolve, onReject } =
    useChangeDestinationModal(dispatch);

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
    <>
      <div className="flex items-baseline justify-between p-2">
        <div className="my-auto">
          <span className="mr-3 font-bold text-purple-500">Destination:</span>
          <Button variant="text" onClick={() => setOpen(true)}>
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
              className="select mx-2 cursor-pointer"
              onChange={(e) => dispatch(setSort(e.target.value))}
            >
              <option value="Name">Name</option>
              <option value="Category">Category</option>
            </select>
          </label>
        </div>
      </div>
      <ChangeDestinationModal
        isOpen={isOpen}
        onResolve={onResolve}
        onReject={onReject}
      />
    </>
  );
}

function useChangeDestinationModal(dispatch) {
  const { clearCart, deleteDestination } = useSupplyContext();
  const [isChangeDestinationModalOpen, setIsChangeDestinationModalOpen] =
    useState(false);

  const onResolve = () => {
    dispatch(resetFilters());
    clearCart();
    deleteDestination();
  };

  const onReject = () => {
    setIsChangeDestinationModalOpen(false);
  };

  return {
    isOpen: isChangeDestinationModalOpen,
    setOpen: setIsChangeDestinationModalOpen,
    onResolve,
    onReject,
  };
}

function ChangeDestinationModal({ isOpen, onResolve, onReject }) {
  return (
    <Modal isOpen={isOpen}>
      <Modal.Title>Change Destination</Modal.Title>
      <Modal.Body>
        You are about to change your destination.
        <br />
        Please note that your shopping cart will be emptied as a result of this
        operation.
        <br />
        Would you like to continue?
      </Modal.Body>
      <Modal.Buttons>
        <Button color="secondary" onClick={onReject}>
          Cancel
        </Button>
        <Button color="success" onClick={onResolve}>
          Yes
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
