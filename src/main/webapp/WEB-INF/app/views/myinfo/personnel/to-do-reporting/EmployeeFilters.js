import React, { useState } from "react";
import { setRespCtrHeads, toggleInactiveEmployees, updateContServDate } from "./todoReportingActions";
import { formatISO, sub } from "date-fns";
import { Combobox, ComboboxButton, ComboboxInput, ComboboxOption, ComboboxOptions } from '@headlessui/react'
import { useResponsibilityCenterHeadSearch } from "app/views/myinfo/personnel/to-do-reporting/respctrHeadSearchApi";
import { ChevronDownIcon, XMarkIcon } from "@heroicons/react/16/solid";
import LoadingCircle from "app/components/LoadingCircle";
import { useDebounce } from "use-debounce";


export default function EmployeeFilters({ state, dispatch }) {
  return (
    <div className="mt-4">
      <span className="text-lg font-semibold">Employee Filters</span>
      <div className="mt-1">
        <IncludeInactiveEmployeesCheckbox state={state} dispatch={dispatch}/>
        <ContServiceDateFilter state={state} dispatch={dispatch}/>
        <OfficeFilter state={state} dispatch={dispatch}/>
      </div>
    </div>
  );
}

function IncludeInactiveEmployeesCheckbox({ state, dispatch }) {
  return (
    <label
      className="font-light flex items-center gap-1"
      htmlFor="includeInactiveEmployees"
    >
      <input
        id="includeInactiveEmployees"
        name="includeInactiveEmployees"
        type="checkbox"
        checked={!state.empActive}
        onChange={(e) =>
          dispatch(toggleInactiveEmployees(e.target.checked))
        }
      />
      Include Inactive Employees
    </label>
  )
}

function ContServiceDateFilter({ state, dispatch }) {
  const [customDate, setCustomDate] = useState(formatISO(sub(new Date(), { weeks: 2 }), { representation: "date" }))

  const onCustomDateChange = date => {
    setCustomDate(date);
    dispatch(updateContServDate(date));
  }

  return (
    <fieldset className="mt-2">
      <legend className="font-light">Continuous Service Start Date</legend>
      <label className="font-light flex items-center gap-1" htmlFor="Any">
        <input
          id="Any"
          name="contServDate"
          type="radio"
          value=""
          checked={state.contSrvFrom === ""}
          onChange={e => dispatch(updateContServDate(e.target.value))}
        />
        Any
      </label>

      <label className="font-light flex items-center gap-1" htmlFor="Custom">
        <input
          id="Custom"
          name="contServDate"
          type="radio"
          value={customDate}
          checked={state.contSrvFrom !== ""}
          onChange={e => dispatch(updateContServDate(e.target.value))}
        />
        Custom
        {state.contSrvFrom !== "" &&
          <input
            type="date"
            className="ml-2"
            value={customDate}
            onChange={e => onCustomDateChange(e.target.value)}
          />
        }
      </label>
    </fieldset>
  )
}

function OfficeFilter({ state, dispatch }) {
  const [term, setTerm] = useState("")
  const [debouncedTerm] = useDebounce(term, 500);
  const [filteredOffices, setFilteredOffices] = useState([])
  const respctrHeadSearchQuery = useResponsibilityCenterHeadSearch(debouncedTerm)

  React.useEffect(() => {
    // Persist offices matching term so they don't disappear when term is updated.
    if (respctrHeadSearchQuery.isSuccess) {
      setFilteredOffices(respctrHeadSearchQuery.data)
    }
  }, [respctrHeadSearchQuery.data])

  const removeSelectedOffice = office => {
    dispatch(setRespCtrHeads(state.respCtrHead.filter(o => o.code !== office.code)))
  }

  if (term === "" && filteredOffices.length === 0 && respctrHeadSearchQuery.isPending) {
    // Display loading circle while waiting for initial load.
    return (
      <div className="my-3">
        <LoadingCircle/>
      </div>
    )
  }


  return (
    <div className="mt-2">
      <div className="font-light">Offices</div>
      <div>
        {state.respCtrHead.length > 0 && (
          <ul>
            {state.respCtrHead.map((office) => (
              <li key={office.code}
                  className="flex justify-between items-center cursor-pointer w-64 my-1 px-3 text-white bg-teal-600 rounded"
                  onClick={() => removeSelectedOffice(office)}>
                <div>
                  {office.name}
                </div>
                <XMarkIcon className="size-4"></XMarkIcon>
              </li>
            ))}
          </ul>
        )}
      </div>
      <Combobox multiple
                immediate
                value={state.respCtrHead}
                onChange={(offices) => dispatch(setRespCtrHeads(offices))}
                onClose={() => setTerm("")}>
        <div className="relative w-64">
          <ComboboxInput
            aria-label="Offices"
            autoComplete="off"
            value={term}
            className="input w-full"
            onChange={(e) => setTerm(e.target.value)}
          />
          <ComboboxButton className="group absolute inset-y-0 right-0 px-2.5">
            <ChevronDownIcon className="size-4 fill-black/60 group-data-[hover]:fill-black"/>
          </ComboboxButton>
        </div>
        <ComboboxOptions anchor="top"
                         className="w-[var(--input-width)] [--anchor-max-height:300px] overflow-y-auto shadow-lg bg-white
                         empty:invisible transition duration-100 ease-in">
          {filteredOffices.filter(o => !state.respCtrHead.includes(o)).map((office) => (
            <div key={office.code}>
              <hr/>
              <ComboboxOption value={office}
                              className="px-3 py-2 cursor-pointer data-[focus]:bg-gray-100">
                <div className="">
                  {office.name}
                </div>
                <div className="font-light text-xs">
                  {office.code}
                </div>
              </ComboboxOption>
            </div>
          ))}
        </ComboboxOptions>
      </Combobox>
    </div>
  )
}
