import React, { useState } from 'react';
import styles from "./EmployeeFilters.module.css";
import Dropdown from "./Dropdown";
import RespectiveHead from "app/views/myinfo/personnel/to-do-reporting/RespectiveHead";

export default function EmployeeFilters() {

  const [ active, setActive ] = useState(true);
  const [ selectedValue, setSelectedValue ] = useState('');
  const options = {
    "ANY": "Any",
    "PAST_TWO_WEEKS": "Past Two Weeks",
    "CUSTOM_DATE": "Custom Date"
  };
  const [ startDate, setStartDate ] = useState(new Date());

  return (
    <div className={styles.filterBlock}>
      <label className={styles.labelCheck}>
        <input className={styles.inputCheck} type="checkbox" onChange={() => setActive(!active)}/>
        Include Inactive Employees
      </label>
      <div>
        <label className={styles.labelCheck1}>Continuous Service Start Date</label>
        <Dropdown
          options={options}
          selectedValue={selectedValue}
          onSelectedValueChange={(value) => setSelectedValue(value)}
        />
        {selectedValue === "Custom Date" && (
          <input className={styles.date}
                 type="date" defaultValue={new Date().toISOString().substring(0, 10)}
                 onChange={(e) => setStartDate(new Date(e.target.value))}/>
        )}
      </div>
      <label className={styles.labelCheck1}>Offices</label>
      {/*<a className={styles.atag} href="#" onClick={handleRemoveAllChecks}>*/}
       <a> Clear Selected Offices
      </a>
      <RespectiveHead/>
    </div>
  );
}