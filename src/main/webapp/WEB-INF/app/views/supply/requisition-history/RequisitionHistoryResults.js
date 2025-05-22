import React from "react";
import Card from "app/components/Card";
import NoMatchesFound from "app/components/NoMatchesFound";

export default function RequisitionHistoryResults({
  results,
  filters,
  dispatch,
}) {
  if (results.length === 0) {
    return <NoMatchesFound />;
  }

  console.log(results);
  return (
    <Card>
      <ul>
        {results.map((r) => (
          <li key={r.requisitionId}>{r.requisitionId}</li>
        ))}
      </ul>
    </Card>
  );
}
