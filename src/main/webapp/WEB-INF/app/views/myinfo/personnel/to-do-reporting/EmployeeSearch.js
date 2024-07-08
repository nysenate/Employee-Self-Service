import React, { useState } from 'react';
import styles from './EmployeeSearch.module.css';

const EmployeeSearch = () => {
  const [searchTerm, setSearchTerm] = useState('');

  const handleSearch = (event) => {
    setSearchTerm(event.target.value);
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

export default EmployeeSearch;
