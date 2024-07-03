import React, { useState } from 'react';
import Select from 'react-select';
import Highlighter from 'react-highlight-words';
import styles from '../views/supply/universalStyles.module.css';

/**
 * Displays a dropdown input search bar to select items.
 *
 * @param {Object} props The props for the component.
 * @param {Array} props.data The data array to be displayed.
 * @param {String} props.valueField The field name for the value.
 * @param {String} props.labelField The field name for the label.
 * @param {Object} [props.initialItem] The initial item to be displayed (optional).
 * @param {Function} [props.handleSelect] Optional function to handle selection.
 * @param {Number} [props.minWidth] Optional minimum width for the input.
 * @returns {JSX.Element} The dropdown input search bar.
 */
const FilterSelect = ({ data, valueField, labelField, initialItem, handleSelect, minWidth = 200 }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedItem, setSelectedItem] = useState(
    initialItem
    ? {
        value: getNestedProperty(initialItem, valueField),
        label: getNestedProperty(initialItem, valueField),
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
    value: getNestedProperty(item, valueField),
    label: (
      <div className={styles.searchOptions__option}>
        <Highlighter
          searchWords={[searchTerm]}
          textToHighlight={getNestedProperty(item, valueField)}
          className={styles.commodityCode}
        />
        <br />
        <small className={styles.description}>
          <Highlighter
            searchWords={[searchTerm]}
            textToHighlight={getNestedProperty(item, labelField)}
          />
        </small>
      </div>
    ),
    item
  }));

  const customStyles = {
    control: (provided, state) => ({
      ...provided,
      background: '#fff',
      borderColor: '#9e9e9e',
      minWidth: `${minWidth}px`,
      minHeight: '26px',
      height: '26px',
      boxShadow: state.isFocused ? null : null,
      borderRadius: '0px',
    }),

    valueContainer: (provided, state) => ({
      ...provided,
      height: '26px',
      padding: '0 6px'
    }),

    input: (provided, state) => ({
      ...provided,
      margin: '0px',
    }),
    indicatorSeparator: state => ({
      display: 'none',
    }),
    indicatorsContainer: (provided, state) => ({
      ...provided,
      height: '26px',
    }),
  };

  const filteredOptions = options.filter(option =>
    option.item[valueField].toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className={styles.searchContentContainer}>
      <Select
        className={styles.searchBar}
        value={selectedItem}
        onChange={handleChange}
        onInputChange={handleInputChange}
        options={filteredOptions}
        classNamePrefix={styles.searchOptions__option}
        styles={customStyles}
      />
    </div>
  );
};

/**
 * Gets a nested property from an object using a dot-separated path.
 *
 * @param {Object} obj The object to get the property from.
 * @param {String} path The dot-separated path of the property.
 * @returns {*} The value of the nested property.
 */
const getNestedProperty = (obj, path) => {
  return path.split('.').reduce((o, p) => (o ? o[p] : undefined), obj);
};

export default FilterSelect;
