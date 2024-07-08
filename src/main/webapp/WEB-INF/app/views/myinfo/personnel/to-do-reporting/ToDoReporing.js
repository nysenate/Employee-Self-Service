import React from "react";
import Hero from "app/components/Hero";
import TrainingFilters from "app/views/myinfo/personnel/to-do-reporting/TrainingFilters";
import EmployeeDetails from "app/views/myinfo/personnel/to-do-reporting/EmployeeDetails";

export default function ToDoReporting() {
  return (
    <div>
      <Hero>Personnel To-Do Reporting</Hero>
      <div style={{
        width: "100%",
        display: "flex",
        background: "#fefefe",
        position: "relative",
        boxShadow: "0 1px 2px #aaa",
        marginTop: "20px",
        overflow: "auto",
      }}>
        <TrainingFilters/>
        <EmployeeDetails/>
      </div>
    </div>
  )
}