import React, { useEffect } from 'react';

export default function EmployeeCount() {
  const count = 10;

  const apiUrl = '/api/v1/personnel/task/emp/search/report';
  const queryParams = {
    empActive: true,
    name: '',
    taskActive: true
  };

  // Function to build the full URL with query parameters
  const buildDownloadUrl = () => {
    const queryString = Object.keys(queryParams)
      .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(queryParams[key])}`)
      .join('&');

    return `${apiUrl}?${queryString}`;
  };

  return (
    <div style={{
      display: "flex", flexDirection: "row", justifyContent: "space-between", alignItems: "center", fontFamily: "inherit", boxSizing: "border-box", marginTop: "1em"
    }}>
    <span className={"text-base font-semibold flex-1 padding-10 text-align-center"}>
    {count} Matching Employees</span>
      <span className={"flex-1"}></span>
      <a
        className={"text-base text-teal-500 flex-1 padding-10 float-left text-align-center"}
        href={buildDownloadUrl()}
        target="_blank"
        rel="noopener noreferrer"
      >
        Download results as CSV
      </a>
    </div>
  );
}