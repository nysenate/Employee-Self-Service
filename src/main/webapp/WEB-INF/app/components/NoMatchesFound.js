import React from "react";
import Card from "app/components/Card";

export default function NoMatchesFound({ className }) {
  return (
    <Card className={className}>
      <div className="p-4 text-center text-2xl">No Matches Found</div>
    </Card>
  );
}
