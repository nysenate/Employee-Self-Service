import React, { useEffect, useState } from "react";
import styles from "./InputFilters.module.css";
import Dropdown from "./Dropdown";
import EmployeeFilters from "app/views/myinfo/personnel/to-do-reporting/EmployeeFilters";

export default function InputFilters() {
  const [ taskDetails, setTaskDetails ] = useState([]);
  const [ loading, setLoading ] = useState(true);
  const [ error, setError ] = useState(null);
  const [ active, setActive ] = useState(true);
  const [ activeTasks, setActiveTasks ] = useState([]);
  const [ inActiveTasks, setInActiveTasks ] = useState([]);
  const [selectedValue, setSelectedValue] = useState('');
  const options = {"ANY": "Any",
    "ALL_INCOMPLETE": "All Incomplete",
    "SOME_INCOMPLETE": "Some Incomplete",
    "ALL_COMPLETE": "All Complete"};

  const handleSelectedValueChange = (value) => {
    setSelectedValue(value);
  };

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
        }
        const response = await fetch(`/api/v1/personnel/task`, init);
        setLoading(true);
        if (!response.ok) {
          throw new Error('Failed to fetch data');
        }
        const data = await response.json();
        setActiveTasks(data.tasks.filter(task => task.active));
        setInActiveTasks(data.tasks.filter(task => !task.active));
        setLoading(false);
      } catch (error) {
        setError(error.message);
        setLoading(false);
      }
    };
    fetchTaskDetails();
  }, []);
  return (
    <div className={styles.card}>
      <label className={styles.labelCheck}>
        <input className={styles.inputCheck} type="checkbox" onChange={()=> setActive(!active)}/>
        Include inactive trainings
      </label>
      <a className={styles.atag} href="">Clear selected trainings</a>
      <hr/>
      {activeTasks.map(item => (
        <div key={item.title}>
          <label className={styles.labelCheck} htmlFor={item.title}>
            <input className={styles.inputCheck} type="checkbox" id={item.title}/>
            {`${item.title}`}
          </label>
        </div>
      ))}
      {!active && (
        inActiveTasks.map(item => (
          <div key={item.title}>
            <label className={styles.labelCheck} htmlFor={item.title}>
              <input className={styles.inputCheck} type="checkbox" id={item.title}/>
              {`${item.title}`}
            </label>
          </div>
        )))}
      &nbsp; &nbsp;
      <div>
        <label className={styles.labelCheck1}>Completion Status for Selected Training(s)</label>
        <Dropdown
          options={options}
          selectedValue={selectedValue}
          onSelectedValueChange={handleSelectedValueChange}
        />
      </div>
      &nbsp;
      <hr/>
    </div>

  )
}