import React, { useEffect, useState } from "react";
import styles from "./InputFilters.module.css";
import Dropdown from "./Dropdown";
import {
  CLEAR_TRAININGS,
  TOGGLE_INACTIVE_TRAININGS,
  TOGGLE_TRAINING
} from "app/views/myinfo/personnel/to-do-reporting/actionTypes";
import { useAssignments } from "app/views/myinfo/personnel/to-do-reporting/assignmentsApi";


export default function InputFilters({ state, dispatch }) {
  const [ activeTasks, setActiveTasks ] = useState([]);
  const [ inactiveTasks, setInactiveTasks ] = useState([]);
  const [ inactiveTaskIds, setInactiveTaskIds ] = useState([]);
  const assignmentQuery = useAssignments(false);

  const options = {
    "ANY": "Any",
    "ALL_INCOMPLETE": "All Incomplete",
    "SOME_INCOMPLETE": "Some Incomplete",
    "ALL_COMPLETE": "All Complete"
  };

  useEffect(() => {
    if (assignmentQuery.isSuccess) {
      setActiveTasks(assignmentQuery.data.filter(task => task.active))
      setInactiveTasks(assignmentQuery.data.filter(task => !task.active))
      setInactiveTaskIds(assignmentQuery.data.filter(task => !task.active).map(task => task.taskId))
    }
  }, [ assignmentQuery.data ]);

  if (assignmentQuery.isPending) {
    return <></>
  }

  return (
    <div className={styles.card}>
      <label className={styles.labelCheck}>
        <input
          className={styles.inputCheck}
          type="checkbox"
          onChange={(e) => dispatch({
            type: TOGGLE_INACTIVE_TRAININGS,
            payload: {
              checked: e.target.checked,
              inactiveTaskIds: inactiveTaskIds,
            }
          })}
          checked={!state.taskActive}
        />
        Include inactive trainings
      </label>
      <a className={styles.atag} href="#" onClick={() => dispatch({ type: CLEAR_TRAININGS })}>
        Clear selected trainings
      </a>
      <hr/>
      {activeTasks.map(item => (
        <div key={item.taskId}>
          <label className={styles.labelCheck} htmlFor={item.title}>
            <input
              className={styles.inputCheck}
              type="checkbox"
              id={item.taskId}
              checked={state.taskId.includes(item.taskId)}
              onChange={(e) => dispatch({
                type: TOGGLE_TRAINING,
                payload: { taskId: item.taskId, checked: e.target.checked }
              })}
            />
            {item.title}
          </label>
        </div>
      ))}
      {!state.taskActive && (
        <>
          <hr/>
          {inactiveTasks.map((item) => (
            <div key={item.taskId}>
              <label className={styles.labelCheck} htmlFor={item.title}>
                <input
                  className={styles.inputCheck}
                  type="checkbox"
                  id={item.taskId}
                  checked={state.taskId.includes(item.taskId)}
                  onChange={(e) => dispatch({
                    type: TOGGLE_TRAINING,
                    payload: { taskId: item.taskId, checked: e.target.checked }
                  })}
                />
                {item.title}
              </label>
            </div>
          ))}
        </>
      )}
      &nbsp;
      <div>
        <label className={styles.labelCheck1}>Completion Status for Selected Training(s)</label>
        {/*<Dropdown*/}
        {/*  options={options}*/}
        {/*  selectedValue={selectedValue}*/}
        {/*  onSelectedValueChange={handleSelectedValueChange}*/}
        {/*/>*/}
      </div>
      <hr/>
    </div>

  );
}
