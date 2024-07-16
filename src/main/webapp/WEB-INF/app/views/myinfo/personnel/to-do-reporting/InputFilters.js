import React, { useEffect, useState } from "react";
import styles from "./InputFilters.module.css";
import Dropdown from "./Dropdown";


export default function InputFilters({ params, onChildDataChange, handleAllTasks }) {
  const [ activeTasks, setActiveTasks ] = useState([]);
  const [ inActiveTasks, setInActiveTasks ] = useState([]);
  const [ selectedValue, setSelectedValue ] = useState('');
  const [ active, setActive ] = useState(false);
  const options = {
    "ANY": "Any",
    "ALL_INCOMPLETE": "All Incomplete",
    "SOME_INCOMPLETE": "Some Incomplete",
    "ALL_COMPLETE": "All Complete"
  };

  // Fetch task details and initialize state
  useEffect(() => {
    const fetchTaskDetails = async () => {
      try {
        const init = {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
          },
          cache: 'no-store',
        };
        const response = await fetch(`/api/v1/personnel/task`, init);
        if (!response.ok) {
          throw new Error('Failed to fetch data');
        }
        const data = await response.json();
        handleAllTasks(data);
        // Filter and set active tasks
        const updatedActiveTasks = data.tasks.filter(task => task.active).map(task => ({
          ...task,
          checked: false
        }));
        setActiveTasks(updatedActiveTasks);
        // Filter and set inactive tasks if active is false
        const updatedInActiveTasks = data.tasks.filter(task => !task.active).map(task => ({
          ...task,
          checked: false
        }));
        setInActiveTasks(updatedInActiveTasks);
      } catch (error) {
        console.error('Error fetching task details:', error);
      }
    };
    fetchTaskDetails();
  }, []);

  // Handle clearing all checks
  const handleRemoveAllChecks = (e) => {
    e.preventDefault();

    // Update active tasks
    const updatedActiveTasks = activeTasks.map(task => ({
      ...task,
      checked: false
    }));
    setActiveTasks(updatedActiveTasks);

    // Update inactive tasks if active is false
    const updatedInActiveTasks = inActiveTasks.map(task => ({
      ...task,
      checked: false
    }));
    setInActiveTasks(updatedInActiveTasks);

    params.taskId.length = 0;
    onChildDataChange(params);
  };

  // Toggle active state
  const toggleActive = () => {

    if (!active) {
      params.taskActive = null;
    } else {
      params.taskActive = active;
    }
    setActive(!active);

    onChildDataChange(params);
  };

  // Handle checkbox change
  const handleCheckboxChange = (taskId) => {

    // Update active tasks
    const updatedActiveTasks = buildTasks(activeTasks, taskId)
    setActiveTasks(updatedActiveTasks);

    // Update inactive tasks if active is false
    const updatedInActiveTasks = buildTasks(inActiveTasks, taskId)
    setInActiveTasks(updatedInActiveTasks);

    onChildDataChange(params);
  };

  const buildTasks = (tasks, taskId) => {
    return tasks.map(task => {
      if (task.taskId === taskId) {
        task.checked = !task.checked;
        if (task.checked) {
          params.taskId.push(task.taskId);
        } else {
          params.taskId = params.taskId.filter(item => item !== taskId)
        }
      }
      return task;
    });
  }

  // Handle selected value change
  const handleSelectedValueChange = (value) => {
    setSelectedValue(value);
    params.totalCompletion = value;
    if (value === "ANY") {
      params.totalCompletion = null;
    }
    onChildDataChange(params);
  };

  return (
    <div className={styles.card}>
      <label className={styles.labelCheck}>
        <input
          className={styles.inputCheck}
          type="checkbox"
          onChange={toggleActive}
          checked={active}
        />
        Include inactive trainings
      </label>
      <a className={styles.atag} href="#" onClick={handleRemoveAllChecks}>
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
              checked={item.checked}
              onChange={() => handleCheckboxChange(item.taskId)}
            />
            {item.title}
          </label>
        </div>
      ))}
      {active && (
        <>
          <hr/>
          {inActiveTasks.map((item) => (
            <div key={item.taskId}>
              <label className={styles.labelCheck} htmlFor={item.title}>
                <input
                  className={styles.inputCheck}
                  type="checkbox"
                  id={item.taskId}
                  checked={item.checked}
                  onChange={() => handleCheckboxChange(item.taskId)}
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
        <Dropdown
          options={options}
          selectedValue={selectedValue}
          onSelectedValueChange={handleSelectedValueChange}
        />
      </div>
      <hr/>
    </div>

  );
}
