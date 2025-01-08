import React from 'react';
import { useTrainings } from "../useTrainings";
import { Button } from "../../../../../components/Button";
import { clearTrainings, toggleTraining } from "./todoAssignmentActions";


export default function TrainingFilters({ state, dispatch }) {
  const trainingsQuery = useTrainings(true)

  if (trainingsQuery.isPending) {
    return <></>
  }

  return (
    <div>
      <span className="text-lg font-semibold">Training Filters</span>
      <div className="mt-1">
        <Button variant="text"
                color="link"
                onClick={() => dispatch(clearTrainings())}>
          Clear selected trainings
        </Button>
        <hr className="my-1"/>
        {trainingsQuery.data.map((item) => (
          <div key={item.taskId}>
            <TrainingInput state={state} dispatch={dispatch} item={item}/>
          </div>
        ))}
      </div>
    </div>
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
        onChange={(e) =>
          dispatch(toggleTraining(item.taskId, e.target.checked))
        }
      />
      {item.title}
    </label>
  );
}