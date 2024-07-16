import React from 'react';
import styles from './Dropdown.module.css';

function Dropdown({ options, selectedValue, onSelectedValueChange }) {
  const keys = Object.keys(options);
  return (
    <select
      className={styles.select}
      value={selectedValue}
      onChange={(e) => onSelectedValueChange(e.target.value)}
    >
      {keys.map((key) => (
        <option key={key} value={key}>
          {options[key]}
        </option>
      ))}
    </select>
  );
}

export default Dropdown;
