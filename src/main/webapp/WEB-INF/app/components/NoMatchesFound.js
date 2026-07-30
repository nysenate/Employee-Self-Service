import React from "react";
import Card from "app/components/Card";

export default function NoMatchesFound({
  children = "No Matches Found",
  className,
}) {
  return (
    <Card className={className}>
      <div className="p-4 text-center text-2xl">{children}</div>
    </Card>
  );
}
