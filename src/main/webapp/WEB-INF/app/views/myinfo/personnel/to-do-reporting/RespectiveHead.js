import React, { useEffect, useState } from 'react';
import { Multiselect } from "multiselect-react-dropdown";
import styles from "./EmployeeFilters.module.css";

export default function RespectiveHead(){
  const [offices, setOffices] = useState([]);
  const [selection, setSelection] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');

  const handleSelectChange = (selectedItems) => {
    setSelection(selectedItems);
  };

  useEffect(() => {
    const fetchRespectiveHeadDetails = async () => {
      try {
        const init = {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
          },
          cache: 'no-store',
        };
        const response = await fetch(`/api/v1/respctr/head/search?limit=20`, init);
        if (!response.ok) {
          throw new Error('Failed to fetch data');
        }
        const data = await response.json();
        console.log(data.result);
        setOffices(data.result);
      } catch (error) {
        console.error('Error fetching task details:', error);
      }
    };
    fetchRespectiveHeadDetails();
  }, []);

  return (
    <div>
      <select
        multiple
        value={selection}
        onChange={(e) => handleSelectChange(Array.from(e.target.selectedOptions, option => option.value))}
      >
        {offices.map(rch => (
          <option key={rch.code} value={rch.code}>
            {rch.name}({rch.shortName})
          </option>
        ))}
      </select>
      <input
        type="text"
        placeholder="Search..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
      />
      <Multiselect
        options={offices}
        displayValue="name"
        style={{
          chips: {
            background: 'white',
            borderRadius: '0px',
            color: '#000',
          },
          searchBox: {
            border: '1px solid #999',
            borderRadius: '0px',
            height: '25px',
            width: '200px',
            display: 'block',
            fontFamily: 'inherit',
          },
          optionContainer: {
            maxHeight: '200px',
          },
          option: {
            fontFamily: 'inherit',
            border: '1px solid #999',
            backgroundColor: '#fff',
            color: '#000',
          },
        }}
      />
    </div>
  );
}

