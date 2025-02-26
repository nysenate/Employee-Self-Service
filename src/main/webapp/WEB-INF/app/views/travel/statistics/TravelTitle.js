import React from "react"
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import DatePicker from "app/views/travel/statistics/DatePicker";

export default function TravelTitle() {
  return (
    <>
      <Hero>Travel Statistics</Hero>
      <Card className="mt-5">
        <Card.Header>
          <DatePicker/>
        </Card.Header>
        <Card>
          Values
        </Card>
      </Card>
    </>
  )
}
