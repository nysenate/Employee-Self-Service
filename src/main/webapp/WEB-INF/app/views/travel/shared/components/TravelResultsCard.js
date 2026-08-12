import React from "react";
import Card from "app/components/Card";
import Pagination from "app/components/Pagination";
import TravelResultsContent from "app/views/travel/shared/components/TravelResultsContent";
import TravelResultsHeader from "app/views/travel/shared/components/TravelResultsHeader";

export default function TravelResultsCard({
  children,
  count,
  status,
  limit,
  offset,
  total,
  itemLabel,
  onResetFilters,
  onPageChange,
}) {
  return (
    <Card className="mt-6">
      <div className="p-4">
        <TravelResultsHeader
          count={count}
          status={status}
          offset={offset}
          total={total}
          itemLabel={itemLabel}
          onResetFilters={onResetFilters}
        />
        <TravelResultsContent status={status}>{children}</TravelResultsContent>
        <Pagination
          limit={limit}
          offset={offset}
          total={total}
          onPageChange={onPageChange}
        />
      </div>
    </Card>
  );
}
