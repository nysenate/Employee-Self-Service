import React, { useState } from "react"
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import DatePicker from "app/views/travel/statistics/DatePicker";
import StatisticsSummary from "app/views/travel/statistics/StatisticsSummary";

export default function Travel() {

  const [statistics, setStatistics] = useState([]);


  const fetchStatistics = async (fromDate, toDate) => {

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
      setStatistics(data);
      console.log(data);
    } catch (error) {
      console.error("Error fetching statistics:", error);
    }
  };

  return (
    <>
      <Hero>Travel Statistics</Hero>
      <Card className="mt-5">
        <Card.Header>
          <DatePicker onFetchStatistics={fetchStatistics}/>
        </Card.Header>
        <Card>
          <StatisticsSummary statistics={statistics} />
        </Card>
      </Card>
    </>
  )
}
