import React, { useEffect, useReducer, useState } from "react";
import Hero from "app/components/Hero";
import TrainingFilters from "./TrainingFilters";
import EmployeeDetails from "./EmployeeDetails";
import { useSearchTaskAssignments } from "app/api/searchTaskAssignmentsApi";
import {
  CLEAR_TRAININGS,
  TOGGLE_INACTIVE_TRAININGS,
  TOGGLE_TRAINING
} from "app/views/myinfo/personnel/to-do-reporting/actionTypes";


function filterReducer(state, action) {
  console.log(action)
  let taskIds = state.taskId
  switch (action.type) {
    case TOGGLE_INACTIVE_TRAININGS:
      if (action.payload.checked === false) {
        // uncheck all inactive tasks
        taskIds = taskIds.filter(taskId => !action.payload.inactiveTaskIds.includes(taskId))
      }
      return {
        ...state,
        taskActive: action.payload.checked === true ? null : true,
        taskId: [ ...new Set(taskIds) ],
      }
    case TOGGLE_TRAINING:
      if (action.payload.checked === true) {
        taskIds.push(action.payload.taskId)
      } else (
        taskIds = taskIds.filter(taskId => taskId !== action.payload.taskId)
      )
      return {
        ...state,
        taskId: [ ...new Set(taskIds) ] // remove any duplicates
      }
    case CLEAR_TRAININGS:
      return {
        ...state,
        taskId: []
      }
  }
}

const initialState = {
  name: "",
  empActive: true,
  taskId: [],
  contSrvFrom: null,
  taskActive: true,
  completed: null,
  totalCompletion: null,
  respCtrHead: [],
  limit: 10,
  offset: 1,
  sort: [ "NAME:ASC", "OFFICE:ASC" ]
}

export default function ToDoReporting() {
  const [ state, dispatch ] = useReducer(filterReducer, initialState)
  const taskAssignmentsQuery = useSearchTaskAssignments(state)

  const [ allTasks, setAllTasks ] = useState([]);

  const params = {}
  const [ receivedData, setReceivedData ] = useState(null);
  const [ loading, setLoading ] = useState(false);

  const handleDataChange = (data) => {
    setParams({
      ...params,
      ...data
    });
  };

  const handleAllTasks = (tasks) => {
    // setAllTasks(tasks.tasks);
  }

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
        overflow: "auto"
      }}>
        <TrainingFilters
          state={state}
          dispatch={dispatch}
          handleAllTasks={handleAllTasks}/>

        {/*<EmployeeDetails*/}
        {/*  params={params}*/}
        {/*  onChildDataChange={handleDataChange}*/}
        {/*  finalData={receivedData}*/}
        {/*  loading={loading}*/}
        {/*  allTasks={allTasks}/>*/}
      </div>
    </div>
  );
}
