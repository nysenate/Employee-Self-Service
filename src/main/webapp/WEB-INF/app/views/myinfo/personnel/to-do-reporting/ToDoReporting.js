import React, { useEffect, useReducer, useState } from "react";
import Hero from "app/components/Hero";
import TrainingFilters from "./TrainingFilters";
import { useSearchTaskAssignments } from "app/api/searchTaskAssignmentsApi";
import Card from "app/components/Card";
import {
  CLEAR_TRAININGS, COMPLETION_STATUS,
  TOGGLE_INACTIVE_TRAININGS,
  TOGGLE_TRAINING
} from "app/views/myinfo/personnel/to-do-reporting/todoReportingActions";
import AssignmentsSummary from "app/views/myinfo/personnel/to-do-reporting/AssignmentsSummary";


function filterReducer(state, action) {
  console.log(action)
  let taskIds = state.taskId
  switch (action.type) {
    case TOGGLE_INACTIVE_TRAININGS:
      if (action.payload.checked === false) {
        // If only active training should be shown, uncheck all inactive tasks
        taskIds = taskIds.filter(taskId => !action.payload.inactiveTrainingIds.includes(taskId))
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
    case COMPLETION_STATUS:
      return {
        ...state,
        totalCompletion: action.payload.completionStatus
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
  totalCompletion: "",
  respCtrHead: [],
  limit: 10,
  offset: 1,
  sort: [ "NAME:ASC", "OFFICE:ASC" ]
}

export default function ToDoReporting() {
  const [ state, dispatch ] = useReducer(filterReducer, initialState)
  const taskAssignmentsQuery = useSearchTaskAssignments(state)

  return (
    <React.Fragment>
      <Hero>Personnel To-Do Reporting</Hero>
      <Card className="mt-3">
        <div className="grid grid-cols-5 gap-4 p-4">
          <div className="col-span-2">
            <TrainingFilters state={state} dispatch={dispatch}/>
          </div>
          <div className="col-span-3">
            <AssignmentsSummary taskAssignmentQuery={taskAssignmentsQuery} state={state} dispatch={dispatch}/>
          </div>
        </div>
      </Card>
      {/*<TrainingFilters*/}
      {/*  state={state}*/}
      {/*  dispatch={dispatch}*/}
      {/*  handleAllTasks={handleAllTasks}/>*/}

      {/*<EmployeeDetails*/}
      {/*  params={params}*/}
      {/*  onChildDataChange={handleDataChange}*/}
      {/*  finalData={receivedData}*/}
      {/*  loading={loading}*/}
      {/*  allTasks={allTasks}/>*/}
    </React.Fragment>
  );
}
