import React, { useEffect, useState } from "react";
import styles from "./InputFilters.module.css"
import { useQuery } from "@tanstack/react-query";
import { fetchApiJson } from "app/utils/fetchJson";

export default function InputFilters(){
  const [taskDetails, setTaskDetails] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchTaskDetails = async () => {
      console.log("okokok I am here")
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
        console.log("okokok I am here also", response)
        if (!response.ok) {
          throw new Error('Failed to fetch data');
        }
        const data = await response.json();
        console.log(data)
        setTaskDetails(data); // Assuming the response is JSON and setting it to state
        setLoading(false);
      } catch (error) {
        setError(error.message);
        setLoading(false);
      }
    };
    fetchTaskDetails().then(r => console.log(r));
  }, []);
  return (
    <div className={styles.card}>
      <label className={styles.labelCheck}>
        <input className={styles.inputCheck} type="checkbox"/>
        Include inactive trainings
      </label>
      <a className={styles.atag} href="">Clear selected trainings</a>
      <hr/>
      <label className={styles.labelCheck}>
        <input className={styles.inputCheck} type="checkbox"/>
        Include inactive trainings
      </label>
      <label className={styles.labelCheck}>
        <input className={styles.inputCheck} type="checkbox"/>
        Include inactive trainings
      </label>
      <label className={styles.labelCheck}>
        <input className={styles.inputCheck} type="checkbox"/>
        Include inactive trainings
      </label>
      <label className={styles.labelCheck}>
        <input className={styles.inputCheck} type="checkbox"/>
        Include inactive trainings
      </label>
      <label className={styles.labelCheck}>
        <input className={styles.inputCheck} type="checkbox"/>
        Include inactive trainings
      </label>
    </div>
  )
}