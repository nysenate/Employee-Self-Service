// Assuming you're in a React component
import React, { useState, useEffect } from 'react';

const GetTaskDetails = () => {
  const [taskDetails, setTaskDetails] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchTaskDetails = async () => {
      try {
        const response = await fetch(`api/v1/personnel/task`);
        if (!response.ok) {
          throw new Error('Failed to fetch data');
        }
        const data = await response.json();
        setTaskDetails(data); // Assuming the response is JSON and setting it to state
        setLoading(false);
      } catch (error) {
        setError(error.message);
        setLoading(false);
      }
    };

    fetchTaskDetails().then(r => r);
  }, []); // Empty array means this effect runs only once after the initial render

  if (loading) {
    return <p>Loading...</p>;
  }

  if (error) {
    return <p>Error: {error}</p>;
  }

  // Assuming taskDetails is an object with properties you want to display
  return (
    <div>
      <h2>Task Details</h2>
      <p>Title: {taskDetails.title}</p>
      <p>Description: {taskDetails.description}</p>
      {/* Display other details as needed */}
    </div>
  );
};

export default GetTaskDetails;
