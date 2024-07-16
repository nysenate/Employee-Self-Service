import React, { useEffect, useState } from "react";
import Hero from "app/components/Hero";
import TrainingFilters from "app/views/myinfo/personnel/to-do-reporting/TrainingFilters";
import EmployeeDetails from "app/views/myinfo/personnel/to-do-reporting/EmployeeDetails";

/**
 * This is the first component where TO-DO-Reporting starts
 * @returns {Element}
 * @constructor
 */
export default function ToDoReporting() {
  const [ allTasks, setAllTasks ] = useState([]);

  /**
   * This is params, which is a state variable, used to interact with Personal Task Assignment Table.
   */
  const [ params, setParams ] = useState({
    name: "",
    empActive: true,
    taskId: [],
    contSrvFrom: null,
    taskActive: true,
    completed: null,
    totalCompletion: null,
    respCtrHead: [],
    limit: 10,
    offset: 1,
    sort: [ "NAME:ASC", "OFFICE:ASC" ]
  });
  const [ receivedData, setReceivedData ] = useState(null);
  const [ loading, setLoading ] = useState(false);

  useEffect(() => {
    const fetchEmployeeResults = async () => {
      setLoading(true);
      try {
        const keyValuePairs = [];
        for (const key in params) {
          if (params[key] !== '' && params[key] !== null) {
            if (Array.isArray(params[key]) && params[key].length > 0) {
              params[key].forEach(value => {
                keyValuePairs.push(key + '=' + value);
              });
            } else if (!Array.isArray(params[key])) {
              keyValuePairs.push(key + '=' + params[key]);
            }
          }
        }
        let queryString = keyValuePairs.join('&');
        const init = {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
          },
          cache: 'no-store',
        };

        const response = await fetch(`/api/v1/personnel/task/emp/search?${queryString}`, init);
        if (!response.ok) {
          throw new Error('Failed to fetch data');
        }
        const data = await response.json();
        setReceivedData(data); // Set received data after successful fetch
      } catch (error) {
        console.error('Error fetching task details:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchEmployeeResults();
  }, [ params ]);

  const handleDataChange = (data) => {
    setParams((prev) => ({
      ...prev,
      ...data
    }));
    console.log("Received Data", data);
  };

  const handleAllTasks = (tasks) => {
    setAllTasks(tasks.tasks);
  }


  return (
    <div>
      <Hero>Personnel To-Do Reporting</Hero>
      <div style={{
        width: "100%",
        display: "flex",
        background: "#fefefe",
        position: "relative",
        boxShadow: "0 1px 2px #aaa",
        marginTop: "20px",
        overflow: "auto"
      }}>
        <TrainingFilters
          params={params}
          onChildDataChange={handleDataChange}
          handleAllTasks={handleAllTasks}/>

        <EmployeeDetails
          params={params}
          onChildDataChange={handleDataChange}
          finalData={receivedData}
          loading={loading}
          allTasks={allTasks}/>
      </div>
    </div>
  );
}
