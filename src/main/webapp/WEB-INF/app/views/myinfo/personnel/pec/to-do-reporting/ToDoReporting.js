import React, { useEffect, useReducer, useState } from "react";
import Hero from "app/components/Hero";
import TrainingFilters from "./TrainingFilters";
import Card from "app/components/Card";
import {
  CLEAR_TRAININGS,
  COMPLETION_STATUS,
  SET_EMP_NAME,
  SET_OFFSET,
  SET_RESP_CTR_HEADS,
  TOGGLE_INACTIVE_TRAININGS,
  TOGGLE_TRAINING,
  UPDATE_CONT_SERV_DATE,
} from "app/views/myinfo/personnel/pec/to-do-reporting/todoReportingActions";
import EmployeeFilters from "app/views/myinfo/personnel/pec/to-do-reporting/EmployeeFilters";
import { setEmpName } from "./todoReportingActions";
import InputDebounced from "../InputDebounced";
import { TOGGLE_INACTIVE_EMPLOYEES } from "./todoReportingActions";
import { useSearchTaskAssignments } from "../useTaskAssignment";
import AssignmentsSummary from "./AssignmentsSummary";

function filterReducer(state, action) {
  let taskIds = state.taskId;
  switch (action.type) {
    case TOGGLE_INACTIVE_TRAININGS:
      if (action.payload.checked === false) {
        // If only active training should be shown, uncheck all inactive tasks
        taskIds = taskIds.filter(
          (taskId) => !action.payload.inactiveTrainingIds.includes(taskId),
        );
      }
      return {
        ...state,
        taskActive: action.payload.checked === true ? null : true,
        taskId: [...new Set(taskIds)],
        offset: 1,
      };
    case TOGGLE_TRAINING:
      if (action.payload.checked === true) {
        taskIds.push(action.payload.taskId);
      } else
        taskIds = taskIds.filter((taskId) => taskId !== action.payload.taskId);
      return {
        ...state,
        taskId: [...new Set(taskIds)], // remove any duplicates
        offset: 1,
      };
    case CLEAR_TRAININGS:
      return {
        ...state,
        taskId: [],
        offset: 1,
      };
    case COMPLETION_STATUS:
      return {
        ...state,
        totalCompletion: action.payload.completionStatus,
        offset: 1,
      };
    case TOGGLE_INACTIVE_EMPLOYEES:
      return {
        ...state,
        empActive: action.payload.checked === true ? null : true,
        offset: 1,
      };
    case UPDATE_CONT_SERV_DATE:
      return {
        ...state,
        contSrvFrom: action.payload.date,
        offset: 1,
      };
    case SET_RESP_CTR_HEADS:
      return {
        ...state,
        respCtrHead: action.payload.respCtrHead,
        offset: 1,
      };
    case SET_EMP_NAME:
      return {
        ...state,
        name: action.payload.name,
        offset: 1,
      };
    case SET_OFFSET:
      return {
        ...state,
        offset: action.payload.offset,
      };
    default:
      return {
        ...state,
      };
  }
}

const initialState = {
  name: "",
  empActive: true,
  taskId: [],
  contSrvFrom: "",
  taskActive: true,
  completed: null,
  totalCompletion: "",
  respCtrHead: [],
  limit: 10,
  offset: 1,
  sort: ["NAME:ASC", "OFFICE:ASC"],
};

export default function ToDoReporting() {
  const [state, dispatch] = useReducer(filterReducer, initialState);
  const taskAssignmentsQuery = useSearchTaskAssignments(state);

  return (
    <React.Fragment>
      <Hero>Personnel To-Do Reporting</Hero>
      <Card className="mt-3">
        <div className="grid grid-cols-5 gap-4 p-4">
          <div className="col-span-2">
            <TrainingFilters state={state} dispatch={dispatch} />
            <EmployeeFilters state={state} dispatch={dispatch} />
          </div>
          <div className="col-span-3">
            <label htmlFor="empNameSearch" className="font-light">
              Search by Employee Name
            </label>
            <InputDebounced
              id="empNameSearch"
              value={state.name}
              onChange={(name) => dispatch(setEmpName(name))}
              className="w-64"
            />
            <AssignmentsSummary
              taskAssignmentQuery={taskAssignmentsQuery}
              state={state}
              dispatch={dispatch}
            />
          </div>
        </div>
      </Card>
    </React.Fragment>
  );
}
