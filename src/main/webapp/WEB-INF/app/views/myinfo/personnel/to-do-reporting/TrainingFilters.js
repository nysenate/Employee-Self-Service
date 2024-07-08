import React from "react";
import styles from "./TrainingFilters.module.css"
import Card from "app/components/Card";
import InputFilters from "app/views/myinfo/personnel/to-do-reporting/InputFilters";
import EmployeeFilters from "app/views/myinfo/personnel/to-do-reporting/EmployeeFilters";

export default function TrainingFilters(){
  return (
      <div className={styles.trainingFilters}>
        <span className="text-lg font-semibold">Training Filters</span>
        <InputFilters/>
        <span className="text-lg font-semibold">Employee Filters</span>
        <EmployeeFilters/>
      </div>
  )
}
