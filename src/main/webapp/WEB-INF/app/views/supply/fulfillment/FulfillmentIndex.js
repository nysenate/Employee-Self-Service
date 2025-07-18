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
import { useSearchParams } from "react-router-dom";
import RequisitionEditModal from "app/views/supply/fulfillment/modal/RequisitionEditModal";

export const REQUISITION_ID_SEARCH_PARAM = "requisitionId";

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
  const [searchParams, setSearchParams] = useSearchParams();
  const queryClient = useQueryClient();
  const requisitionQuery = useRequisitionSearch(requisitionParams);
  const rejectedRequisitionsQuery = useRequisitionSearch(
    rejectedRequisitionParams,
  );
  const socket = useRequisitionSocket((req) => {
    queryClient.invalidateQueries(["supply", "requisition", "list"]);
  });

  // The Requisition edit modal is controlled by the search params. The modal is displayed when
  // REQUISITION_ID_SEARCH_PARAM is set and the relevant requisition is loaded.
  // e.g. /fulfillment?requisitionId=23 will display the modal for requisition 23.
  const isEditModalOpen = () =>
    searchParams.get(REQUISITION_ID_SEARCH_PARAM) !== null;

  // The full requisition object for the requisition specified in search params or undefined.
  const selectedRequisition = () =>
    requisitionQuery.data.result.find(
      (r) =>
        r.requisitionId ===
        parseInt(searchParams.get(REQUISITION_ID_SEARCH_PARAM)),
    );

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

      {isEditModalOpen() && (
        <RequisitionEditModal
          isOpen={isEditModalOpen()}
          onResolve={() => setSearchParams({})}
          requisition={selectedRequisition()}
        />
      )}
    </div>
  );
}
