import React, { useState } from 'react';
import styles from "./EmployeeFilters.module.css";
import RespectiveHead from "app/views/myinfo/personnel/to-do-reporting/RespectiveHead";
import moment from "moment";

export default function EmployeeFilters({ params, onChildDataChange }) {

  const [ active, setActive ] = useState(true);
  const [ selectedValue, setSelectedValue ] = useState('');
  const contSrvDateValues = {
    any: {
      label: 'Any',
      getValue: () => null
    },
    twoWeeks: {
      label: 'Past Two Weeks',
      getValue: () => moment().subtract(2, 'weeks').format('YYYY-MM-DD')
    },
    custom: {
      label: 'Custom Date',
      getValue: () => params.contSrvFrom
    }
  };

  const contSrvDateOpts = Object.keys(contSrvDateValues);

  const handleSelect = (option) => {
    setSelectedValue(option);
    if (option === 'custom') {
      setSelectedValue('custom');
    } else {
      const value = contSrvDateValues[option].getValue();
      params.contSrvFrom = value;
      onChildDataChange(params);
    }
  };

  const handleCustomDateChange = (value) => {
    params.contSrvFrom = value;
    onChildDataChange(params);
  };


  const handleActive = () => {
    console.log(active);
    if (active) {
      params.empActive = null;
    } else {
      params.empActive = !active;
    }
    setActive(!active);
    onChildDataChange(params);
  }

  return (
    <div className={styles.filterBlock}>
      <label className={styles.labelCheck}>
        <input className={styles.inputCheck} type="checkbox" onChange={handleActive}/>
        Include Inactive Employees
      </label>
      <br/>
      <label className={styles.labelCheck}>
        Continuous Service Start Date
      </label>
      <select className={styles.select} onChange={(e) => handleSelect(e.target.value)}>
        {contSrvDateOpts.map((option) => (
          <option key={option} value={option}>
            {contSrvDateValues[option].label}
          </option>
        ))}
      </select>
      {selectedValue === "custom" && (
        <div>
          <input className={styles.date}
                 type="date"
                 onChange={(e) => handleCustomDateChange(e.target.value)}/>
        </div>
      )}
      <label className={styles.labelCheck1}>Offices</label>
      <RespectiveHead params={params} onChildDataChange={onChildDataChange}/>
    </div>
  )
    ;
}