import React, { useEffect, useState } from 'react';

export default function RespectiveHead(){
  const [activeTasks, setActiveTasks] = useState([]);
  const [inActiveTasks, setInActiveTasks] = useState([]);

  useEffect(() => {
    const fetchRespectiveHeadDetails = async () => {
      try {
        const init = {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
          },
          cache: 'no-store',
        };
        const response = await fetch(`/api/v1/respctr/head/search`, init);
        if (!response.ok) {
          throw new Error('Failed to fetch data');
        }
        const data = await response.json();
        console.log(data.result);
      } catch (error) {
        console.error('Error fetching task details:', error);
      }
    };
    fetchRespectiveHeadDetails();
  }, []);
  return <div>I am done</div>;
}