import React, { useEffect, useState } from 'react';
import styles from './RespectiveHead.module.css';

export default function RespectiveHead({ params, onChildDataChange }) {
  const [ options, setOptions ] = useState([]);
  const [ searchTerm, setSearchTerm ] = useState('');
  const [ selectedOptions, setSelectedOptions ] = useState([]);
  const [ dropdownOpen, setDropdownOpen ] = useState(false);

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
        const response = await fetch(`/api/v1/respctr/head/search?limit=ALL&offset=1`, init);
        if (!response.ok) {
          throw new Error('Failed to fetch data');
        }
        const data = await response.json();
        setOptions(data.result);
      } catch (error) {
        console.error('Error fetching task details:', error);
      }
    };
    fetchRespectiveHeadDetails();
  }, []);

  const toggleDropdown = () => {
    setDropdownOpen(!dropdownOpen);
    setSearchTerm(''); // Clear search term when toggling dropdown
  };

  // Function to handle when an option is selected
  const handleOptionClick = (option) => {
    setSelectedOptions([ ...selectedOptions, option ]);
    setOptions(options.filter(item => item.code !== option.code));
    setSearchTerm('');
    if (options.length > 0) {
      setDropdownOpen(true);
    } else {
      setDropdownOpen(false);
    }
    params.respCtrHead.push(option.code);
    onChildDataChange(params);
  };

  const handleChipRemove = (option) => {
    setSelectedOptions(selectedOptions.filter(item => item.code !== option.code));

    let insertIndex = options.findIndex(item => item.code > option.code);
    if (insertIndex === -1) {
      insertIndex = options.length; // Append at the end if not found
    }

    setOptions(prevOptions => [
      ...prevOptions.slice(0, insertIndex),
      option,
      ...prevOptions.slice(insertIndex)
    ]);

    params.respCtrHead = params.respCtrHead.filter(code => code !== option.code);
    onChildDataChange(params);
  };

  // Function to filter options based on search term
  const filterOptions = () => {
    return options.filter(option => {
      if (option.name !== null) {
        return option.name.toLowerCase().includes(searchTerm.toLowerCase()) || option.code.toLowerCase().includes(searchTerm.toLowerCase());
      } else {

      }
    });
  };

  const handleRemoveAllChecks = (e) => {
    e.preventDefault();
    console.log("Clear Selected Offices");
    setSelectedOptions([]);
    params.respCtrHead.length = 0;
    onChildDataChange(params);
  };

  return (
    <>
      <a className={"text-teal-600 font-normal"} href="#" onClick={handleRemoveAllChecks}>
        Clear selected offices
      </a>
      <div className={styles.Testing}>
        <div className={styles.searchContainer}>
          <div className={styles.chipsContainer}>
            {selectedOptions.map(option => (
              <div key={option.code} className={styles.chip} onClick={() => handleChipRemove(option)}>
                <span>{option.name}</span><span className={styles.close}>×</span>
              </div>
            ))}
            <input
              type="text"
              className={styles.searchInput}
              placeholder="Search an office"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onClick={toggleDropdown}
            />
          </div>
          {(dropdownOpen || searchTerm.length > 0) && (
            <div className={styles.dropdown}>
              {filterOptions().map(option => (
                <div
                  key={option.code}
                  className={styles.option}
                  onClick={() => handleOptionClick(option)}
                >
                  <span>{option.name}</span>
                  <br/>
                  <span>{option.code}</span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </>
  );
}
