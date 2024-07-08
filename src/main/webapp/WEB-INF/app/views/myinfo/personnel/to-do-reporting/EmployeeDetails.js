import React, { useEffect, useState } from 'react';
import EmployeeSearch from "app/views/myinfo/personnel/to-do-reporting/EmployeeSearch";
import styles from './EmployeeSearch.module.css';
import EmployeeCount from "app/views/myinfo/personnel/to-do-reporting/EmployeeCount";
import Pagination from "app/views/myinfo/personnel/to-do-reporting/PaginationRange";
import Employees from "app/views/myinfo/personnel/to-do-reporting/Employees";

export default function EmployeeDetails() {

  const [params, setParams] = useState({
    name: "",
    empActive: true,
    taskId: null,
    contServFrom: null,
    taskActive: true,
    completed: true,
    totalCompletion: null,
    respCtrHead: null,
    sort: []
  });
  useEffect(() => {
    const fetchEmployeeResults = async () => {
      try {
        const init = {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
          },
          cache: 'no-store',
        };

        const keyValuePairs = [];
        console.log(params)
        for (const key in params) {
          console.log(keyValuePairs);
          if (params[key] !== ''){
            keyValuePairs.push(encodeURIComponent(key) + '=' + encodeURIComponent(params[key]));
          }
        }
        const string = keyValuePairs.join('&');
        console.log(string);

        const response = await fetch(`/api/v1/personnel/task/emp/search?`+string, init);
        if (!response.ok) {
          throw new Error('Failed to fetch data');
        }
        const data = await response.json();
        console.log(data);
        // Filter and set active tasks
        // const updatedActiveTasks = data.tasks.filter(task => task.active).map(task => ({
        //   ...task,
        //   checked: false
        // }));
        // setActiveTasks(updatedActiveTasks);
        // // Filter and set inactive tasks if active is false
        // const updatedInActiveTasks = data.tasks.filter(task => !task.active).map(task => ({
        //   ...task,
        //   checked: false
        // }));
        // setInActiveTasks(updatedInActiveTasks);
      } catch (error) {
        console.error('Error fetching task details:', error);
      }
    };
    fetchEmployeeResults();
  }, []);
  return <div className={styles.card}>
    <EmployeeSearch/>
    &nbsp; &nbsp;
    <EmployeeCount/>
    &nbsp;
    <Pagination/>
    <Employees/>
  </div>;
}
