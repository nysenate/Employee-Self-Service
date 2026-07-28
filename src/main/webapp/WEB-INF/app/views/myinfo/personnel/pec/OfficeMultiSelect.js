import React, { useState } from "react";
import { useDebounce } from "use-debounce";
import { ChevronDownIcon, XMarkIcon } from "@heroicons/react/16/solid";
import {
  Combobox,
  ComboboxButton,
  ComboboxInput,
  ComboboxOption,
  ComboboxOptions,
} from "@headlessui/react";
import { useResponsibilityCenterHeadSearch } from "./respctrHeadQueries";
import LoadingCircle from "../../../../components/LoadingCircle";

export default function OfficeMultiSelect({ onChange }) {
  const [selectedOffices, setSelectedOffices] = useState([]);
  const [term, setTerm] = useState("");
  const [debouncedTerm] = useDebounce(term, 500);
  const [filteredOffices, setFilteredOffices] = useState([]);
  const respctrHeadSearchQuery =
    useResponsibilityCenterHeadSearch(debouncedTerm);

  React.useEffect(() => {
    onChange(selectedOffices);
  }, [selectedOffices]);

  React.useEffect(() => {
    // Persist offices matching term so they don't disappear when term is updated.
    if (respctrHeadSearchQuery.isSuccess) {
      setFilteredOffices(respctrHeadSearchQuery.data);
    }
  }, [respctrHeadSearchQuery.data]);

  const removeSelectedOffice = (office) => {
    setSelectedOffices((selectedOffices) =>
      selectedOffices.filter((o) => o.code !== office.code),
    );
  };

  if (
    term === "" &&
    filteredOffices.length === 0 &&
    respctrHeadSearchQuery.isPending
  ) {
    // Display loading circle while waiting for initial load.
    return (
      <div className="my-3">
        <LoadingCircle />
      </div>
    );
  }

  return (
    <div className="mt-2">
      <div className="font-light">Offices</div>
      <div>
        {selectedOffices.length > 0 && (
          <ul>
            {selectedOffices.map((office) => (
              <li
                key={office.code}
                className="my-1 flex w-64 cursor-pointer items-center justify-between rounded bg-teal-600 px-3 text-white"
                onClick={() => removeSelectedOffice(office)}
              >
                <div>{office.name}</div>
                <XMarkIcon className="size-4"></XMarkIcon>
              </li>
            ))}
          </ul>
        )}
      </div>
      <Combobox
        multiple
        immediate
        value={selectedOffices}
        onChange={(offices) => setSelectedOffices(offices)}
        onClose={() => setTerm("")}
      >
        <div className="relative w-64">
          <ComboboxInput
            aria-label="Offices"
            autoComplete="off"
            value={term}
            className="input w-full"
            onChange={(e) => setTerm(e.target.value)}
          />
          <ComboboxButton className="group absolute inset-y-0 right-0 px-2.5">
            <ChevronDownIcon className="size-4 fill-black/60 group-data-[hover]:fill-black" />
          </ComboboxButton>
        </div>
        <ComboboxOptions
          anchor="top"
          className="w-[var(--input-width)] overflow-y-auto bg-white shadow-lg transition duration-100 ease-in [--anchor-max-height:300px] empty:invisible"
        >
          {!respctrHeadSearchQuery.isLoading &&
            respctrHeadSearchQuery.data
              .filter((o) => !selectedOffices.includes(o))
              .map((office) => (
                <div key={office.code}>
                  <hr />
                  <ComboboxOption
                    value={office}
                    className="cursor-pointer px-3 py-2 data-[focus]:bg-gray-100"
                  >
                    <div className="">{office.name}</div>
                    <div className="text-xs font-light">{office.code}</div>
                  </ComboboxOption>
                </div>
              ))}
        </ComboboxOptions>
      </Combobox>
    </div>
  );
}
