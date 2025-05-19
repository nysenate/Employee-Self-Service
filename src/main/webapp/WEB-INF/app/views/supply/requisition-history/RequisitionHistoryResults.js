import React from "react";
import Card from "app/components/Card";
import NoMatchesFound from "app/components/NoMatchesFound";

export default function RequisitionHistoryResults({ data, filters, dispatch }) {
  if (data.result.length === 0) {
    return <NoMatchesFound />;
  }

  return (
    <Card>
      <ul>
        {data.result.map((r) => (
          <li key={r.requistionId}>{r}</li>
        ))}
      </ul>
    </Card>
  );
}
