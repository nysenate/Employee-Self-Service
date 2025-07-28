import Card from "app/components/Card";
import React from "react";

export default function FulfillmentCard({ title, bgColorClass, children }) {
  return (
    <Card>
      <Card.Header
        className={`${bgColorClass} border-b-0 text-xl font-medium text-white`}
      >
        {title}
      </Card.Header>
      {children}
    </Card>
  );
}
