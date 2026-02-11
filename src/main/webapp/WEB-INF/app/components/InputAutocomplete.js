import React, { forwardRef } from "react";
import { useState } from "react";
import { Combobox } from "@headlessui/react";
import { twMerge } from "tailwind-merge";
import { useDebounce } from "use-debounce";
import clsx from "clsx";

const InputAutocomplete = forwardRef(function InputAutocomplete(
  {
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
  },
  ref,
) {
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
    <div className={twMerge(className)}>
      <Combobox value={value} onChange={onChange}>
        <div className="relative">
          <Combobox.Input
            ref={ref}
            id={id}
            name={name}
            className="input w-full"
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
              "absolute z-20 max-h-60 overflow-y-auto border border-gray-200 bg-white p-1",
              "[--anchor-gap:2px] [--anchor-max-height:20rem] empty:invisible",
              "transition duration-100 ease-in data-leave:data-closed:opacity-0",
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
});

export default InputAutocomplete;
