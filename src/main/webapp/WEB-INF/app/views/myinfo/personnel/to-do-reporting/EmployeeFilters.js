import React, { useState } from 'react';
import styles from "./EmployeeFilters.module.css";
import Dropdown from "./Dropdown";
import DatePicker from "app/views/myinfo/personnel/to-do-reporting/DatePicker";

export default function EmployeeFilters() {

  const [ active, setActive ] = useState(true);
  const [selectedValue, setSelectedValue] = useState('');
  const options = {"ANY": "Any",
    "PAST_TWO_WEEKS": "Past Two Weeks",
    "CUSTOM_DATE": "Custom Date"};
  const [startDate, setStartDate] = useState(new Date());

  const handleSelectedValueChange = (value) => {
    setSelectedValue(value);
  };

  return (
      <div className={styles.filterBlock}>
        <label className={styles.labelCheck}>
          <input className={styles.inputCheck} type="checkbox" onChange={()=> setActive(!active)}/>
          Include inactive trainings
        </label>
        &nbsp; &nbsp;
        <div>
          <label className={styles.labelCheck1}>Completion Status for Selected Training(s)</label>
          <Dropdown
            options={options}
            selectedValue={selectedValue}
            onSelectedValueChange={(value)=> setSelectedValue(value)}
          />
          {selectedValue === "Custom Date" &&(
            // <input datepicker className={styles.date}  onChange={()=> setSelectedValue(value)}></input>
            <DatePicker/>
          )}
        </div>
      </div>
  );
}