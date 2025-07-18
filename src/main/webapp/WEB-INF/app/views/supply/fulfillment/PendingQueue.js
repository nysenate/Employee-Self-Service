import FulfillmentCard from "app/views/supply/fulfillment/FulfillmentCard";
import React, { useState } from "react";
import * as dateUtils from "app/utils/dateUtils";
import EmptyQueueMessage from "app/views/supply/fulfillment/EmptyQueueMessage";
import PickupIcon from "app/views/supply/fulfillment/PickupIcon";
import { useLocationStatistics } from "app/views/supply/fulfillment/useLocationStatistics";
import clsx from "clsx";
import {
  boldRequisitionRow,
  highlightRequisitionRow,
} from "app/views/supply/fulfillment/fulfillmentUtils";
import { useSearchParams } from "react-router-dom";
import { REQUISITION_ID_SEARCH_PARAM } from "app/views/supply/fulfillment/FulfillmentIndex";

export default function PendingQueue({ requisitions }) {
  const [, setSearchParams] = useSearchParams();
  const locationStatisticsQuery = useLocationStatistics(
    new Date().getFullYear(),
    new Date().getMonth() + 1,
  );

  return (
    <div>
      <FulfillmentCard
        title="Pending Requisition Requests"
        bgColorClass="bg-orange-600"
      >
        {!requisitions.length ? (
          <EmptyQueueMessage>No Pending Requests</EmptyQueueMessage>
        ) : (
          <table className="table">
            <thead>
              <tr className="table__head__row">
                <th className="table__head__cell table__head__cell--text"></th>
                <th className="table__head__cell table__head__cell--text">
                  Id
                </th>
                <th className="table__head__cell table__head__cell--text">
                  Location
                </th>
                <th className="table__head__cell table__head__cell--text">
                  Employee
                </th>
                <th className="table__head__cell table__head__cell--number">
                  Item Count
                </th>
                <th className="table__head__cell table__head__cell--text">
                  Order Date
                </th>
                <th className="table__head__cell table__head__cell--text">
                  Issuer
                </th>
              </tr>
            </thead>
            <tbody className="table__body table__body--highlight divide-y divide-gray-200/80">
              {requisitions.map((r) => (
                <tr
                  className={clsx(
                    "table__row",
                    highlightRequisitionRow(r, locationStatisticsQuery.data) &&
                      "bg-red-400/50",
                    boldRequisitionRow(r, locationStatisticsQuery.data) &&
                      "font-semibold",
                  )}
                  key={r.requisitionId}
                  onClick={() =>
                    setSearchParams({
                      [REQUISITION_ID_SEARCH_PARAM]: r.requisitionId,
                    })
                  }
                >
                  <td className="">
                    <div className="flex h-full items-center justify-center">
                      {r.deliveryMethod === "PICKUP" && (
                        <PickupIcon size="20" />
                      )}
                    </div>
                  </td>
                  <td className="table__cell table__cell--text">
                    {r.requisitionId}
                  </td>
                  <td className="table__cell table__cell--text">
                    {r.destination.locId}
                  </td>
                  <td className="table__cell table__cell--text">
                    {r.customer.lastName}
                  </td>
                  <td className="table__cell table__cell--number">
                    {r.lineItems.length}
                  </td>
                  <td className="table__cell table__cell--text">
                    {dateUtils.isoToShortDateTime(r.orderedDateTime)}
                  </td>
                  <td className="table__cell table__cell--text">
                    {r.issuer?.lastName}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </FulfillmentCard>
    </div>
  );
}
