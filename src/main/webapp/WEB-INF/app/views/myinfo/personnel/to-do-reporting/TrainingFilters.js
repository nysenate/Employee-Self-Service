import React, { useState } from "react";
import InputFilters from "./InputFilters";
import EmployeeFilters from "./EmployeeFilters";

/**
 * This component is used for Training Filters
 * @param params
 * @param onChildDataChange
 * @param handleAllTasks
 * @returns {Element}
 * @constructor
 */
export default function TrainingFilters({ state, dispatch, handleAllTasks }) {
  return (
    <div style={{
      flex: "0 0 40%",
      width: "40%",
      float: "left",
      padding: "2em",
    }}>
      <span className="text-lg font-semibold">Training Filters</span>
      <InputFilters
        state={state}
        dispatch={dispatch}
        handleAllTasks={handleAllTasks}/>

      <span className="text-lg font-semibold">Employee Filters</span>
      {/*<EmployeeFilters*/}
      {/*  params={params}*/}
      {/*  setParams={setParams}/>*/}
    </div>
  )
}
