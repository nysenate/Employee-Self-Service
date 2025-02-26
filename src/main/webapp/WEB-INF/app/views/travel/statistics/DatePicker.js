import React, { useState } from "react";

export default function DatePicker() {

  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");

  const handleFetchStatistics = async () => {
    if (!fromDate) {
      alert("Please select a 'From Date'");
      return;
    }

    try {
      const response = await fetch(`/api/v1/travel/applications/statistics?fromDate=${fromDate}&toDate=${toDate}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Accept": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error("Failed to fetch data");
      }

      const data = await response.json();
      console.log("API Response:", data);
    } catch (error) {
      console.error("Error fetching statistics:", error);
    }
  };

  return (
    <>
      <div className="flex flex-items justify-center px-4 gap-3">
        <label className="font-bold p-2">
          From Date:
        </label>
          <input
            type="date"
            value={fromDate}
            onChange={(e) => setFromDate(e.target.value)}
            className="border rounded p-2 w-100"
          />

        <label className="font-bold p-2">
          To Date:
        </label>
          <input
            type="date"
            value={toDate}
            onChange={(e) => setToDate(e.target.value)}
            className="border rounded p-2 w-100"
          />

        <button
          onClick={handleFetchStatistics}
          className="bg-yellow-500 float-right text-white px-2 rounded box-border shadow-amber-600"
        >
          Get Statistics
        </button>
      </div>
    </>

  )
}