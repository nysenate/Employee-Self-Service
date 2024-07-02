import React, { useState } from 'react';
import Select from 'react-select';
import Highlighter from 'react-highlight-words';
import styles from '../views/supply/universalStyles.module.css'; // Adjust the import to your actual styles file

/**
 * Displays a dropdown input search bar to select items.
 *
 * @param {Object} props The props for the component.
 * @param {Array} props.data The data array to be displayed.
 * @param {string} props.valueField The field name for the value.
 * @param {string} props.labelField The field name for the label.
 * @param {Object} props.initialItem The initial item to be displayed (optional).
 * @param {Function} [props.handleSelect] Optional function that takes in selectedItem to handle selection.
 * @returns {JSX.Element} The dropdown input search bar.
 */
const FilterSelect = ({ data, valueField, labelField, initialItem, handleSelect }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedItem, setSelectedItem] = useState(
    initialItem
    ? {
        value: initialItem[valueField],
        label: initialItem[valueField],
        item: initialItem
      }
    : null
  );

  const handleInputChange = (inputValue) => {
    setSearchTerm(inputValue);
  };

  const handleChange = (selectedOption) => {
    setSelectedItem({
      value: selectedOption.value,
      label: selectedOption.value, // Display only the valueField
      item: selectedOption.item
    });
    if (handleSelect) handleSelect(selectedOption.item);
  };

  const options = data.map(item => ({
    value: item[valueField],
    label: (
      <div className={styles.searchOptions__option}>
        <Highlighter
          searchWords={[searchTerm]}
          textToHighlight={item[valueField]}
          className={styles.commodityCode}
        />
        <br />
        <small className={styles.description}>
          <Highlighter
            searchWords={[searchTerm]}
            textToHighlight={item[labelField]}
          />
        </small>
      </div>
    ),
    item
  }));

  const filteredOptions = options.filter(option =>
    option.item[valueField].toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className={styles.searchContentContainer}>
      {/*<label>*/}
      {/*  Add Commodity Code:*/}
      {/*</label>*/}
      <span style={{ textAlign: 'left' }}>
        <Select
          className={styles.searchBar}
          value={selectedItem}
          onChange={handleChange}
          onInputChange={handleInputChange}
          options={filteredOptions}
          classNamePrefix={styles.searchOptions__option}
        />
      </span>
    </div>
  );
};

export default FilterSelect;
