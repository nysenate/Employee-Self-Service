import FulfillmentCard from "app/views/supply/fulfillment/FulfillmentCard";
import React, { useState } from "react";
import * as dateUtils from "app/utils/dateUtils";
import EmptyQueueMessage from "app/views/supply/fulfillment/EmptyQueueMessage";
import RequisitionInfoModal from "app/views/supply/RequisitionInfoModal";
import { CheckIcon, XCircleIcon } from "@heroicons/react/16/solid";
import PickupIcon from "app/views/supply/fulfillment/PickupIcon";

export default function ApprovedQueue({ requisitions }) {
  const [selectedReq, setSelectedReq] = useState(null);

  const getSyncStatusIcon = (requisition) => {
    // Return nothing if no sync has been attempted
    if (!requisition.lastSfmsSyncDateTime) {
      return "";
    }
    return requisition.savedInSfms ? (
      <CheckIcon className="size-6 text-green-600" />
    ) : (
      <XCircleIcon className="size-4 text-red-600" />
    );
  };

  return (
    <div>
      <FulfillmentCard
        title="Approved Requisition Requests"
        bgColorClass="bg-purple-600"
      >
        {!requisitions.length ? (
          <EmptyQueueMessage>No Approved Requests</EmptyQueueMessage>
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
                  Approved Date
                </th>
                <th className="table__head__cell table__head__cell--text">
                  Issuer
                </th>
                <th className="table__head__cell table__head__cell--text">
                  Sync Status
                </th>
              </tr>
            </thead>
            <tbody className="table__body table__body--highlight divide-y divide-gray-200/80">
              {requisitions.map((r) => (
                <tr
                  className="table__row"
                  key={r.requisitionId}
                  onClick={() => setSelectedReq(r)}
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
                    {dateUtils.isoToShortDateTime(r.approvedDateTime)}
                  </td>
                  <td className="table__cell table__cell--text">
                    {r.issuer?.lastName}
                  </td>
                  <td className="">
                    <div className="flex h-full items-center justify-center">
                      {getSyncStatusIcon(r)}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </FulfillmentCard>
      <RequisitionInfoModal
        isOpen={selectedReq !== null}
        requisition={selectedReq}
        onResolve={() => setSelectedReq(null)}
      />
    </div>
  );
}
