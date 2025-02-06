import { Button } from "app/components/Button";
import React, { useEffect, useState } from "react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import useAuth from "app/contexts/Auth/useAuth";
import { useSupplyDestinations } from "app/views/supply/requisition/useSupplyDestinations";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useEmployee } from "app/views/useEmployee";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";
import {
  Combobox,
  ComboboxButton,
  ComboboxInput,
  ComboboxOption,
  ComboboxOptions,
} from "@headlessui/react";
import { ChevronDownIcon } from "@heroicons/react/16/solid";
import { useDebounce } from "use-debounce";

export default function SelectDestination() {
  const auth = useAuth();
  const employeeQuery = useEmployee(auth.empId());
  const validDestinationsQuery = useSupplyDestinations(auth.empId());
  const { setDestination } = useSupplyContext();
  const [dirtyDestination, setDirtyDestination] = useState({});

  // Attempt to initialize the dirtyDestination to the employees work location.
  React.useEffect(() => {
    if (validDestinationsQuery.data && employeeQuery.data) {
      const workLocationId = employeeQuery.data.empWorkLocation.locId;
      let initDestination = validDestinationsQuery.data.find(
        (d) => d.locId === workLocationId,
      );
      if (!initDestination) {
        initDestination = validDestinationsQuery.data[0];
      }
      setDirtyDestination(initDestination);
    }
  }, [validDestinationsQuery.data, employeeQuery.data]);

  if (validDestinationsQuery.isPending || employeeQuery.isPending) {
    return <LoadingIndicator />;
  }

  return (
    <div>
      <Hero>Requisition Form</Hero>
      <Controls className="text-center p-3">
        <label
          className="text-purple-700 font-semibold mr-2"
          htmlFor="destination"
        >
          Please select a destination:
        </label>
        <DestinationSearchBox
          value={dirtyDestination}
          destinations={validDestinationsQuery.data}
          onChange={(newVal) => setDirtyDestination(newVal)}
        />
        <span className="mx-2">
          <Button
            onClick={() => setDestination(dirtyDestination)}
            disabled={!dirtyDestination}
          >
            Confirm
          </Button>
        </span>
      </Controls>
    </div>
  );
}

function DestinationSearchBox({ value, destinations, onChange }) {
  const [term, setTerm] = useState("");
  const [debouncedTerm] = useDebounce(term, 500);
  const [filteredDestinations, setFilteredDestinations] = useState([]);

  useEffect(() => {
    const searchTerm = debouncedTerm.toLowerCase();
    const filtered = destinations.filter((dest) => {
      return (
        dest.locId.toLowerCase().includes(searchTerm) ||
        dest.locationDescription.toLowerCase().includes(searchTerm)
      );
    });
    setFilteredDestinations(filtered);
  }, [destinations, debouncedTerm]);

  return (
    <div className="inline-block">
      <Combobox
        value={value}
        virtual={{ options: filteredDestinations }}
        onChange={(newVal) => onChange(newVal)}
        onClose={() => setTerm("")}
      >
        <div className="relative w-64">
          <ComboboxInput
            aria-label="Destination"
            autoComplete="off"
            className="input w-full"
            onChange={(e) => setTerm(e.target.value)}
            displayValue={(dest) => dest?.locId}
          />
          <ComboboxButton className="group absolute inset-y-0 right-0 px-2.5">
            <ChevronDownIcon className="size-4 fill-black/60 group-data-[hover]:fill-black" />
          </ComboboxButton>
        </div>
        <ComboboxOptions
          anchor="bottom"
          className="py-1 w-[var(--input-width)] [--anchor-max-height:300px] overflow-y-auto shadow-lg bg-white
                         empty:invisible transition duration-100 ease-in"
        >
          {({ option: dest }) => (
            <ComboboxOption
              value={dest}
              key={dest.locId}
              className="px-3 py-2 w-full cursor-pointer data-[focus]:bg-gray-100"
            >
              {dest.locId} ({dest.locationDescription})
            </ComboboxOption>
          )}
        </ComboboxOptions>
      </Combobox>
    </div>
  );
}
