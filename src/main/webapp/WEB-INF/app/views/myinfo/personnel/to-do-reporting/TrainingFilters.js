import React, { useState } from "react";
import styles from "./TrainingFilters.module.css"
import Card from "app/components/Card";
import InputFilters from "app/views/myinfo/personnel/to-do-reporting/InputFilters";
import EmployeeFilters from "app/views/myinfo/personnel/to-do-reporting/EmployeeFilters";

export default function TrainingFilters({params, onChildDataChange, handleAllTasks}){
  const [receivedData, setReceivedData] = useState(params);

  const handleChildDataChange = (data) => {
    setReceivedData(data);
    onChildDataChange(receivedData)
  };

  return (
      <div className={styles.trainingFilters}>
        <span className="text-lg font-semibold">Training Filters</span>
        <InputFilters params= {receivedData} onChildDataChange={handleChildDataChange} handleAllTasks={handleAllTasks}/>
        <span className="text-lg font-semibold">Employee Filters</span>
        <EmployeeFilters params= {receivedData} onChildDataChange={handleChildDataChange}/>
      </div>
  )
}
