import React, { useState } from 'react';
import styles from './EmployeeSearch.module.css';

export default function EmployeeSearch({ params, onChildDataChange }) {
  const [ searchTerm, setSearchTerm ] = useState('');

  const handleSearch = (event) => {
    setSearchTerm(event.target.value);
    params.name = event.target.value;
    params.offset = 1;
    onChildDataChange(params);
  };

  return (
    <div>
      <label className={styles.labelCheck}>Search by Employee Name</label>
      <input
        type="text"
        value={searchTerm}
        onChange={handleSearch}
        className={styles.inputCheck}
      />
    </div>
  );
};


