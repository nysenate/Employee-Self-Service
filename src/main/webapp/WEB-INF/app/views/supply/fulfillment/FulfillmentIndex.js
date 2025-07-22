import React, { useEffect, useState } from "react";
import Hero from "app/components/Hero";
import { useRequisitionSocket } from "app/views/supply/fulfillment/useRequisitionSocket";
import { endOfDay, formatISO, startOfDay } from "date-fns";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useRequisitionSearch } from "app/views/supply/useRequisitionSearch";
import { useQueryClient } from "@tanstack/react-query";
import { useSearchParams } from "react-router-dom";
import RequisitionEditModal from "app/views/supply/fulfillment/modal/RequisitionEditModal";
import FulfillmentQueue from "app/views/supply/fulfillment/FilfillmentQueue";
import { QUEUE_COLUMNS } from "app/views/supply/fulfillment/queueUtils";
import RequisitionInfoModal from "app/views/supply/RequisitionInfoModal";

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

  // The Requisition modal is controlled by the search params. The modal is displayed when
  // REQUISITION_ID_SEARCH_PARAM is set and the relevant requisition is loaded.
  // e.g. /fulfillment?requisitionId=23 will display the modal for requisition 23.
  const isModalOpen = () =>
    searchParams.get(REQUISITION_ID_SEARCH_PARAM) !== null;

  // The full requisition object for the requisition specified in search params or undefined.
  const selectedRequisition = () =>
    requisitionQuery.data.result.find(
      (r) =>
        r.requisitionId ===
        parseInt(searchParams.get(REQUISITION_ID_SEARCH_PARAM)),
    );

  // Display either the info or editable modal depending on the status of the requisition.
  const displayRequisitionModal = (req) => {
    if (req.status === "APPROVED" || req.status === "REJECTED") {
      return (
        <RequisitionInfoModal
          isOpen={isModalOpen()}
          onResolve={() => setSearchParams({})}
          requisition={req}
        />
      );
    }
    return (
      <RequisitionEditModal
        isOpen={isModalOpen()}
        onResolve={() => setSearchParams({})}
        requisition={req}
      />
    );
  };

  if (requisitionQuery.isPending) {
    return <LoadingIndicator />;
  }

  return (
    <div>
      <Hero>Fulfillment</Hero>
      <div className="my-4">
        <FulfillmentQueue
          requisitions={requisitionQuery.data.result.filter(
            (r) => r.status === "PENDING",
          )}
          title="Pending Requisition Requests"
          colorClass="bg-orange-600"
          columns={[
            QUEUE_COLUMNS.DELIVERY_METHOD_ICON,
            QUEUE_COLUMNS.ID,
            QUEUE_COLUMNS.LOCATION,
            QUEUE_COLUMNS.CUSTOMER,
            QUEUE_COLUMNS.ITEM_COUNT,
            QUEUE_COLUMNS.ORDERED_DATE,
            QUEUE_COLUMNS.ISSUER,
          ]}
        />
      </div>
      <div className="my-4">
        <FulfillmentQueue
          requisitions={requisitionQuery.data.result.filter(
            (r) => r.status === "PROCESSING",
          )}
          title="Processing Requisition Requests"
          colorClass="bg-teal-500"
          columns={[
            QUEUE_COLUMNS.DELIVERY_METHOD_ICON,
            QUEUE_COLUMNS.ID,
            QUEUE_COLUMNS.LOCATION,
            QUEUE_COLUMNS.CUSTOMER,
            QUEUE_COLUMNS.ITEM_COUNT,
            QUEUE_COLUMNS.ORDERED_DATE,
            QUEUE_COLUMNS.ISSUER,
          ]}
        />
        <div className="my-4">
          <FulfillmentQueue
            requisitions={requisitionQuery.data.result.filter(
              (r) => r.status === "COMPLETED",
            )}
            title="Completed Requisition Requests"
            colorClass="bg-green-600"
            columns={[
              QUEUE_COLUMNS.DELIVERY_METHOD_ICON,
              QUEUE_COLUMNS.ID,
              QUEUE_COLUMNS.LOCATION,
              QUEUE_COLUMNS.CUSTOMER,
              QUEUE_COLUMNS.ITEM_COUNT,
              QUEUE_COLUMNS.ORDERED_DATE,
              QUEUE_COLUMNS.ISSUER,
            ]}
          />
        </div>
        <div className="my-4">
          <FulfillmentQueue
            requisitions={requisitionQuery.data.result.filter(
              (r) => r.status === "APPROVED",
            )}
            title="Approved Requisition Requests"
            colorClass="bg-purple-600"
            columns={[
              QUEUE_COLUMNS.DELIVERY_METHOD_ICON,
              QUEUE_COLUMNS.ID,
              QUEUE_COLUMNS.LOCATION,
              QUEUE_COLUMNS.CUSTOMER,
              QUEUE_COLUMNS.ITEM_COUNT,
              QUEUE_COLUMNS.APPROVED_DATE,
              QUEUE_COLUMNS.ISSUER,
              QUEUE_COLUMNS.SYNC_STATUS_ICON,
            ]}
          />
        </div>
      </div>

      {isModalOpen() && displayRequisitionModal(selectedRequisition())}
    </div>
  );
}
