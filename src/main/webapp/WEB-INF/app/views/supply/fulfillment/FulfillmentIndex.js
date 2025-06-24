import React, { useEffect, useState } from "react";
import Hero from "app/components/Hero";
import PendingQueue from "app/views/supply/fulfillment/PendingQueue";
import { useRequisitionSocket } from "app/views/supply/fulfillment/useRequisitionSocket";
import { endOfDay, formatISO, startOfDay } from "date-fns";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useRequisitionSearch } from "app/views/supply/useRequisitionSearch";
import { useQueryClient } from "@tanstack/react-query";
import ProcessingQueue from "app/views/supply/fulfillment/ProcessingQueue";
import CompletedQueue from "app/views/supply/fulfillment/CompletedQueue";
import ApprovedQueue from "app/views/supply/fulfillment/ApprovedQueue";

const requisitionParams = {
  from: formatISO(new Date(2000, 0, 1)),
  to: formatISO(endOfDay(new Date())),
  status: ["PENDING", "PROCESSING", "COMPLETED", "APPROVED"],
  reconciled: false,
  dateField: "ordered_date_time",
  limit: "ALL",
};

const rejectedRequisitionParams = {
  from: formatISO(startOfDay(new Date())),
  to: formatISO(endOfDay(new Date())),
  status: ["REJECTED"],
  dateField: "rejected_date_time",
  limit: "ALL",
};

export default function FulfillmentIndex() {
  const queryClient = useQueryClient();
  const requisitionQuery = useRequisitionSearch(requisitionParams);
  const socket = useRequisitionSocket((req) => {
    console.log("Received Event: " + req);
    queryClient.invalidateQueries(["supply", "requisition", "list"]);
  });

  if (requisitionQuery.isPending) {
    return <LoadingIndicator />;
  }

  return (
    <div>
      <Hero>Fulfillment</Hero>
      <div className="my-4">
        <PendingQueue
          requisitions={requisitionQuery.data.result.filter(
            (r) => r.status === "PENDING",
          )}
        />
      </div>
      <div className="my-4">
        <ProcessingQueue
          requisitions={requisitionQuery.data.result.filter(
            (r) => r.status === "PROCESSING",
          )}
        />
        <div className="my-4">
          <CompletedQueue
            requisitions={requisitionQuery.data.result.filter(
              (r) => r.status === "COMPLETED",
            )}
          />
        </div>
        <div className="my-4">
          <ApprovedQueue
            requisitions={requisitionQuery.data.result.filter(
              (r) => r.status === "APPROVED",
            )}
          />
        </div>
      </div>
    </div>
  );
}
