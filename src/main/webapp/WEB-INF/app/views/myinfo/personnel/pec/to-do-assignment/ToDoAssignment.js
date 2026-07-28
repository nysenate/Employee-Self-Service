import React, { useReducer } from "react";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import TrainingFilters from "./TrainingFilters";
import {
  CLEAR_TRAININGS,
  SET_EMP_NAME,
  SET_OFFSET,
  SET_RESP_CTR_HEADS,
  setEmpName,
  TOGGLE_EXCLUDE_MEMBERS,
  TOGGLE_TRAINING,
} from "./todoAssignmentActions";
import { useSearchPotentialAssignments } from "../useTaskAssignment";
import PotentialAssignmentsSummary from "./PotentialAssignmentsSummary";
import EmployeeFilters from "./EmployeeFilters";
import InputDebounced from "app/components/InputDebounced";

function filterReducer(state, action) {
  let taskIds = state.taskId;
  switch (action.type) {
    case CLEAR_TRAININGS:
      return {
        ...state,
        taskId: [],
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
    case TOGGLE_EXCLUDE_MEMBERS:
      return {
        ...state,
        isSenator: !action.payload.checked,
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
  isSenator: false,
  taskId: [],
  respCtrHead: [],
  limit: 10,
  offset: 1,
  empActive: true,
  taskActive: true,
};

export default function ToDoAssignment() {
  const [state, dispatch] = useReducer(filterReducer, initialState);
  const potentialTaskAssignmentsQuery = useSearchPotentialAssignments(state);

  return (
    <>
      <Hero>Personnel To-Do Assignment</Hero>
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
            <PotentialAssignmentsSummary
              query={potentialTaskAssignmentsQuery}
              state={state}
              dispatch={dispatch}
            />
          </div>
        </div>
      </Card>
    </>
  );
}
