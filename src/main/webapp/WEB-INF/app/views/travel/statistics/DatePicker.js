import React, { useState } from "react";

export default function DatePicker({ onFetchStatistics }) {

  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");

  const handleFetchStatistics = async () => {

    if (!fromDate) {
      alert("Please select from date.");
      return;
    }
    onFetchStatistics(fromDate, toDate);
  };

  return (
    <>
      <div className="flex flex-items justify-center px-4 gap-3">
        <label className="font-bold p-2">
          From Date:
        </label>
        <input
          required={true}
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