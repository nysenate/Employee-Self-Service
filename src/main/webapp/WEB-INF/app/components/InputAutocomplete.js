import React from "react";
import { useState } from "react";
import { Combobox } from "@headlessui/react";
import { twMerge } from "tailwind-merge";
import { useDebounce } from "use-debounce";

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
        <Combobox.Input
          id={id}
          name={name}
          className="input"
          onChange={(e) => setQuery(e.target.value)}
          displayValue={displayValue}
          placeholder={placeholder}
          autoComplete="off"
        />
        {filteredOptions.length > 0 && (
          <Combobox.Options className="z-10 mt-1 max-h-60 overflow-y-auto border border-gray-200 bg-white shadow-lg transition">
            {filteredOptions.map((option, index) => (
              <Combobox.Option
                key={index}
                value={option}
                className="ui-active:bg-blue-500 ui-active:text-white cursor-pointer p-2 text-gray-900 hover:bg-blue-100"
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
