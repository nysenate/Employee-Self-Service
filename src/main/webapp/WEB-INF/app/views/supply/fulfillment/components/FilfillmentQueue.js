import { useSearchParams } from "react-router-dom";
import { useLocationStatistics } from "app/views/supply/fulfillment/hooks/useLocationStatistics";
import FulfillmentCard from "app/views/supply/fulfillment/components/FulfillmentCard";
import EmptyQueueMessage from "app/views/supply/fulfillment/components/EmptyQueueMessage";
import clsx from "clsx";
import {
  boldRequisitionRow,
  highlightRequisitionRow,
} from "app/views/supply/fulfillment/utils/fulfillmentUtils";
import { REQUISITION_ID_SEARCH_PARAM } from "app/views/supply/fulfillment/FulfillmentIndex";
import React from "react";

export default function FulfillmentQueue({
  requisitions,
  title,
  colorClass,
  columns,
}) {
  const [, setSearchParams] = useSearchParams();
  const locationStatisticsQuery = useLocationStatistics(
    new Date().getFullYear(),
    new Date().getMonth() + 1,
  );

  return (
    <div>
      <FulfillmentCard title={title} bgColorClass={colorClass}>
        {!requisitions.length ? (
          <EmptyQueueMessage>No Pending Requests</EmptyQueueMessage>
        ) : (
          <table className="table">
            <thead>
              <tr className="table__head__row">
                {columns.map((column) => (
                  <React.Fragment key={column.id}>
                    {column.renderHeader()}
                  </React.Fragment>
                ))}
              </tr>
            </thead>
            <tbody className="table__body table__body--highlight divide-y divide-gray-200/80">
              {requisitions.map((r) => (
                <tr
                  className={clsx("table__row", {
                    "bg-red-400/40":
                      showHighlighting(r) &&
                      highlightRequisitionRow(r, locationStatisticsQuery.data),
                    "font-semibold":
                      showHighlighting(r) &&
                      boldRequisitionRow(r, locationStatisticsQuery.data),
                  })}
                  key={r.requisitionId}
                  onClick={() =>
                    setSearchParams({
                      [REQUISITION_ID_SEARCH_PARAM]: r.requisitionId,
                    })
                  }
                >
                  {columns.map((column) => (
                    <React.Fragment key={column.id}>
                      {column.renderCell(r)}
                    </React.Fragment>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </FulfillmentCard>
    </div>
  );
}

// Only show highlights if still being processed.
function showHighlighting(requisition) {
  return (
    requisition.status === "PENDING" ||
    requisition.status === "PROCESSING" ||
    requisition.status === "COMPLETED"
  );
}
