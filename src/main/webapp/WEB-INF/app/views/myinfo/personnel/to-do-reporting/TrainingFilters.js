import React from "react";
import styles from "./TrainingFilters.module.css"
import Card from "app/components/Card";
import InputFilters from "app/views/myinfo/personnel/to-do-reporting/InputFilters";
import EmployeeFilters from "app/views/myinfo/personnel/to-do-reporting/EmployeeFilters";
export default function TrainingFilters(){
  return (
    <Card className={styles.card}>
      <div className={styles.trainingFilters}>
        <span className={styles.filterName}>Training Filters</span>
        <InputFilters/>
        <span className={styles.filterName}>Employee Filters</span>
        <EmployeeFilters/>
      </div>
    </Card>
  )
}