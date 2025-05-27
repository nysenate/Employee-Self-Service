import React from "react";
import { useState } from "react";
import { Combobox } from "@headlessui/react";
import { twMerge } from "tailwind-merge";
import { useDebounce } from "use-debounce";
import clsx from "clsx";

export default function Autocomplete({
  id = "",
  name = "",
  value,
  onChange,
  options,
  displayValue = (item) => item,
  renderOption,
  placeholder = "",
  debounceDelay = 300,
  className = "",
}) {
  const [query, setQuery] = useState("");
  const [debouncedQuery] = useDebounce(query, debounceDelay);

  const filteredOptions =
    debouncedQuery === ""
      ? []
      : options.filter((option) =>
          displayValue(option)
            .toLowerCase()
            .includes(debouncedQuery.toLowerCase()),
        );

  return (
    <div className={twMerge("w-72", `${className}`)}>
      <Combobox value={value} onChange={onChange}>
        <div className="relative">
          <Combobox.Input
            id={id}
            name={name}
            className="input"
            onChange={(e) => setQuery(e.target.value)}
            displayValue={displayValue}
            placeholder={placeholder}
            autoComplete="off"
          />
        </div>
        {filteredOptions.length > 0 && (
          <Combobox.Options
            anchor="bottom"
            transition
            className={clsx(
              "z-20 max-h-60 overflow-y-auto border border-gray-200 bg-white p-1",
              "[--anchor-gap:2px] [--anchor-max-height:20rem] empty:invisible",
              "data-leave:data-closed:opacity-0 transition duration-100 ease-in",
            )}
          >
            {filteredOptions.map((option, index) => (
              <Combobox.Option
                key={index}
                value={option}
                className={clsx(
                  "ui-active:bg-blue-500 ui-active:text-white",
                  "cursor-pointer p-2 text-gray-900 hover:bg-blue-100",
                )}
              >
                {renderOption ? renderOption(option) : displayValue(option)}
              </Combobox.Option>
            ))}
          </Combobox.Options>
        )}
      </Combobox>
    </div>
  );
}
