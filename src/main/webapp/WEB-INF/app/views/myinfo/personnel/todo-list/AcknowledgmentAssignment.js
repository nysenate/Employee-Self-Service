import React, { useEffect, useState } from 'react';
import { Link, useParams } from "react-router-dom";
import Hero from "app/components/Hero";
import Card from "app/components/Card";

export default function AcknowledgmentAssignment() {

  const { taskId } = useParams();
  const [ taskInfo, setTaskInfo ] = useState({});
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
        // console.log(data, taskId);
        const taskInfo = data.tasks.filter(task =>
          task.taskId === Number(taskId)
        );
        setTaskInfo(taskInfo[0]);

      } catch (error) {
        console.error('Error fetching task details:', error);
      }
    };
    fetchTaskDetails();
  }, []);

  return (
    <>
      <Hero>{taskInfo.title}</Hero>
      <Card className="mt-5">
        <div className="py-1 mx-[10em] text-center">
          You acknowledged this policy/document on October 29, 2019
        </div>
        <hr/>
        <div style={{
          display: "flex",
          flexDirection: "row",
          justifyContent: "space-between",
          fontFamily: "inherit",
          boxSizing: "border-box",
          marginTop: "1em"
        }}>
          <Link to="/myinfo/personnel/todo"
                className={"text-base text-teal-500 flex-1 ml-6 p-1 text-left"}>
            Return to Personnel To-Do List</Link>
          <span className={"flex-1"}></span>
          <a
            className={"text-base text-teal-500 mr-6 flex-1 p-1 float-right text-right"}
            href="#"
            target="_blank"
            rel="noopener noreferrer"
          >
            Open Printable view
          </a>
        </div>
      </Card>
    </>
  );
}