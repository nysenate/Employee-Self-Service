// import React, { useEffect, useState } from 'react';
// import { Multiselect } from "multiselect-react-dropdown";
//
// export default function RespectiveHead() {
//   const [ offices, setOffices ] = useState([]);
//   const [ selection, setSelection ] = useState([]);
//   const [ searchTerm, setSearchTerm ] = useState('');
//
//   const handleSelectChange = (selectedItems) => {
//     setSelection(selectedItems);
//   };
//
//   useEffect(() => {
//     const fetchRespectiveHeadDetails = async () => {
//       try {
//         const init = {
//           method: "GET",
//           headers: {
//             "Content-Type": "application/json",
//             "Accept": "application/json",
//           },
//           cache: 'no-store',
//         };
//         const response = await fetch(`/api/v1/respctr/head/search?limit=20`, init);
//         if (!response.ok) {
//           throw new Error('Failed to fetch data');
//         }
//         const data = await response.json();
//         // console.log(data.result);
//         setOffices(data.result);
//       } catch (error) {
//         console.error('Error fetching task details:', error);
//       }
//     };
//     fetchRespectiveHeadDetails();
//   }, []);
//
//   return (
//     <div>
//       <Multiselect
//         options={offices}
//         displayValue="name"
//         style={{
//           chips: {
//             background: '#F5F5F5',
//             borderRadius: '0px',
//             color: 'black',
//           },
//           searchBox: {
//             border: '1px solid #999',
//             borderRadius: '0px',
//             height: '25px',
//             width: '200px',
//             display: 'block',
//             fontFamily: 'inherit',
//           },
//           optionContainer: {
//             maxHeight: '200px',
//             overflow: 'scroll',
//           },
//           option: {
//             fontFamily: 'inherit',
//             border: '1px solid #999',
//             backgroundColor: '#fff',
//             color: '#000',
//           },
//         }}
//       />
//     </div>
//   );
// }

import React, { useState } from "react";
import { Listbox, Transition } from "@headlessui/react";

const people = [
  "Wade Cooper",
  "Arlene Mccoy",
  "Devon Webb",
  "Tom Cook",
  "Tanya Fox",
  "Hellen Schmidt",
  "Caroline Schultz",
  "Mason Heaney",
  "Claudie Smitham",
  "Emil Schaefer"
];

export default function RespectiveHead() {
  const [ isOpen, setIsOpen ] = useState(false);
  const [ selectedPersons, setSelectedPersons ] = useState([]);

  function isSelected(value) {
    return selectedPersons.find((el) => el === value) ? true : false;
  }

  function handleSelect(value) {
    if (!isSelected(value)) {
      const selectedPersonsUpdated = [
        ...selectedPersons,
        people.find((el) => el === value)
      ];
      setSelectedPersons(selectedPersonsUpdated);
    } else {
      handleDeselect(value);
    }
    setIsOpen(true);
  }

  function handleDeselect(value) {
    const selectedPersonsUpdated = selectedPersons.filter((el) => el !== value);
    setSelectedPersons(selectedPersonsUpdated);
    setIsOpen(true);
  }

  return (
    <div className="flex items-center justify-center p-12">
      <div className="w-full max-w-xs mx-auto">
        <Listbox
          as="div"
          className="space-y-1"
          value={selectedPersons}
          onChange={(value) => handleSelect(value)}
          open={isOpen}
        >
          {() => (
            <>
              <Listbox.Label className="block text-sm leading-5 font-medium text-gray-700">
                Assigned to
              </Listbox.Label>
              <div className="relative">
                <span className="inline-block w-full rounded-md shadow-sm">
                  <Listbox.Button
                    className="cursor-default relative w-full rounded-md border border-gray-300 bg-white pl-3 pr-10 py-2 text-left focus:outline-none focus:shadow-outline-blue focus:border-blue-300 transition ease-in-out duration-150 sm:text-sm sm:leading-5"
                    onClick={() => setIsOpen(!isOpen)}
                    open={isOpen}
                  >
                    <span className="block truncate">
                      {selectedPersons.length < 1
                       ? "Select persons"
                       : `Selected persons (${selectedPersons.length})`}
                    </span>
                    <span className="absolute inset-y-0 right-0 flex items-center pr-2 pointer-events-none">
                      <svg
                        className="h-5 w-5 text-gray-400"
                        viewBox="0 0 20 20"
                        fill="none"
                        stroke="currentColor"
                      >
                        <path
                          d="M7 7l3-3 3 3m0 6l-3 3-3-3"
                          strokeWidth="1.5"
                          strokeLinecap="round"
                          strokeLinejoin="round"
                        />
                      </svg>
                    </span>
                  </Listbox.Button>
                </span>

                <Transition
                  unmount={false}
                  show={isOpen}
                  leave="transition ease-in duration-100"
                  leaveFrom="opacity-100"
                  leaveTo="opacity-0"
                  className="absolute mt-1 w-full rounded-md bg-white shadow-lg"
                >
                  <Listbox.Options
                    static
                    className="max-h-60 rounded-md py-1 text-base leading-6 shadow-xs overflow-auto focus:outline-none sm:text-sm sm:leading-5"
                  >
                    {people.map((person) => {
                      const selected = isSelected(person);
                      return (
                        <Listbox.Option key={person} value={person}>
                          {({ active }) => (
                            <div
                              className={`${
                                active
                                ? "text-white bg-blue-600"
                                : "text-gray-900"
                              } cursor-default select-none relative py-2 pl-8 pr-4`}
                            >
                              <span
                                className={`${
                                  selected ? "font-semibold" : "font-normal"
                                } block truncate`}
                              >
                                {person}
                              </span>
                              {selected && (
                                <span
                                  className={`${
                                    active ? "text-white" : "text-blue-600"
                                  } absolute inset-y-0 left-0 flex items-center pl-1.5`}
                                >
                                  <svg
                                    className="h-5 w-5"
                                    xmlns="http://www.w3.org/2000/svg"
                                    viewBox="0 0 20 20"
                                    fill="currentColor"
                                  >
                                    <path
                                      fillRule="evenodd"
                                      d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                                      clipRule="evenodd"
                                    />
                                  </svg>
                                </span>
                              )}
                            </div>
                          )}
                        </Listbox.Option>
                      );
                    })}
                  </Listbox.Options>
                </Transition>
                <div className="pt-1 text-sm">
                  {selectedPersons.length > 0 && (
                    <>Selected persons: {selectedPersons.join(", ")}</>
                  )}
                </div>
              </div>
            </>
          )}
        </Listbox>
      </div>
    </div>
  );
}



