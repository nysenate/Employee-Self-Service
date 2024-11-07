import React, { useEffect, useState } from "react";
import { useTrainings } from "app/views/myinfo/personnel/to-do-reporting/trainingsApi";
import {
  updateCompletionStatus,
  clearTrainings,
  toggleInactiveTrainings, toggleTraining
} from "app/views/myinfo/personnel/to-do-reporting/todoReportingActions";
import { Button } from "app/components/Button";


/**
 * This component fetches all trainings and presents various options for filtering by trainings.
 */
export default function TrainingFilters({ state, dispatch }) {
  const [ activeTrainings, setActiveTrainings ] = useState([]);
  const [ inactiveTrainings, setInactiveTrainings ] = useState([]);
  const [ inactiveTrainingIds, setInactiveTrainingIds ] = useState([]);
  const trainingsQuery = useTrainings(false);

  useEffect(() => {
    if (trainingsQuery.isSuccess) {
      setActiveTrainings(trainingsQuery.data.filter(task => task.active))
      setInactiveTrainings(trainingsQuery.data.filter(task => !task.active))
      setInactiveTrainingIds(trainingsQuery.data.filter(task => !task.active).map(task => task.taskId))
    }
  }, [ trainingsQuery.data ]);

  if (trainingsQuery.isPending) {
    return <></>
  }

  return (
    <div>
      <span className="text-lg font-semibold">Training Filters</span>
      <div className="mt-3">
        <div>
          <IncludeInactiveTrainingsInput state={state}
                                         dispatch={dispatch}
                                         inactiveTrainingIds={inactiveTrainingIds}/>
        </div>
        <div>
          <Button variant="text" color="link" onClick={() => dispatch(clearTrainings())}>
            Clear selected trainings
          </Button>
        </div>
        <hr/>
        <TrainingsList trainings={activeTrainings} state={state} dispatch={dispatch}/>
        {!state.taskActive && (
          <>
            <hr/>
            <TrainingsList trainings={inactiveTrainings} state={state} dispatch={dispatch}/>
          </>
        )}
        &nbsp;
        <div>
          <label className="font-light">Completion Status for Selected Training(s)</label>
          <CompletionStatusSelect state={state} dispatch={dispatch}/>
        </div>
      </div>
    </div>
  )
}

function TrainingsList({ trainings, state, dispatch }) {
  return (
    <>
      {trainings.map(item => (
        <div key={item.taskId}>
          <TrainingInput state={state} dispatch={dispatch} item={item}/>
        </div>
      ))}
    </>
  )
}

function IncludeInactiveTrainingsInput({ state, dispatch, inactiveTrainingIds }) {
  return (
    <label className="font-light flex items-center gap-1" htmlFor="includeInactiveTrainings">
      <input
        id="includeInactiveTrainings"
        name="includeInactiveTrainings"
        type="checkbox"
        checked={!state.taskActive}
        onChange={(e) => dispatch(toggleInactiveTrainings(e.target.checked, inactiveTrainingIds))}
      />
      Include inactive trainings
    </label>
  )
}

function TrainingInput({ state, dispatch, item }) {
  return (
    <label className="font-light flex items-start gap-1" htmlFor={item.taskId}>
      <input
        id={item.taskId}
        name={item.taskId}
        type="checkbox"
        className="mt-[3px]"
        checked={state.taskId.includes(item.taskId)}
        onChange={(e) => dispatch(toggleTraining(item.taskId, e.target.checked))}
      />
      {item.title}
    </label>
  )
}

function CompletionStatusSelect({ state, dispatch }) {
  const options = [
    { label: "Any", value: "" },
    { label: "All Incomplete", value: "ALL_INCOMPLETE" },
    { label: "Some Incomplete", value: "SOME_INCOMPLETE" },
    { label: "All Complete", value: "ALL_COMPLETE" },
  ]

  return (
    <select className="select mt-1"
            value={state.totalCompletion}
            onChange={(e) => dispatch(updateCompletionStatus(e.target.value))}>
      {options.map((opt) =>
        <option key={opt.label} value={opt.value}>
          {opt.label}
        </option>
      )}
    </select>
  )
}