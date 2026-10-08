import ErrorAlert from "app/components/ErrorAlert";
import LoadingStatus from "app/components/LoadingStatus";
import React from "react";
import { useTrainings } from "app/views/myinfo/personnel/pec/useTrainings";
import {
  clearTrainings,
  toggleTraining,
} from "app/views/myinfo/personnel/pec/to-do-reporting/todoReportingActions";
import Button from "app/components/Button";

export default function TrainingFilters({ state, dispatch }) {
  const trainingsQuery = useTrainings(true);

  if (trainingsQuery.isPending) {
    return (
      <LoadingStatus
        message="Loading trainings…"
        layout="centered"
        className="p-6"
      />
    );
  }

  if (!trainingsQuery.data)
    return (
      <ErrorAlert title="Unable to load trainings">
        Please try again.
      </ErrorAlert>
    );

  return (
    <div>
      {trainingsQuery.isError && (
        <ErrorAlert title="Unable to refresh trainings">
          Please try again.
        </ErrorAlert>
      )}
      <span className="text-lg font-semibold">Training Filters</span>
      {trainingsQuery.isFetching && (
        <LoadingStatus message="Refreshing trainings…" />
      )}
      <div className="mt-1">
        <Button variant="link" onPress={() => dispatch(clearTrainings())}>
          Clear selected trainings
        </Button>
        <hr className="my-1" />
        {trainingsQuery.data.map((item) => (
          <div key={item.taskId}>
            <TrainingInput state={state} dispatch={dispatch} item={item} />
          </div>
        ))}
      </div>
    </div>
  );
}

function TrainingInput({ state, dispatch, item }) {
  return (
    <label className="flex items-start gap-1 font-light" htmlFor={item.taskId}>
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
