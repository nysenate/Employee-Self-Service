import PickupIcon from "app/views/supply/fulfillment/components/PickupIcon";
import React from "react";
import * as dateUtils from "app/utils/dateUtils";
import { CheckIcon, XCircleIcon } from "@heroicons/react/16/solid";

export const QUEUE_COLUMNS = {
  DELIVERY_METHOD_ICON: {
    id: "DELIVERY_METHOD_ICON",
    renderHeader: () => <th className="table__head__cell"></th>,
    renderCell: (requisition) => (
      <td>
        <div className="flex h-full items-center justify-center">
          {requisition.deliveryMethod === "PICKUP" && <PickupIcon size="20" />}
        </div>
      </td>
    ),
  },
  ID: {
    id: "ID",
    renderHeader: () => <th className="table__head__cell">Id</th>,
    renderCell: (requisition) => (
      <td className="table__cell">{requisition.requisitionId}</td>
    ),
  },
  LOCATION: {
    id: "LOCATION",
    renderHeader: () => <th className="table__head__cell">Location</th>,
    renderCell: (requisition) => (
      <td className="table__cell">{requisition.destination.locId}</td>
    ),
  },
  CUSTOMER: {
    id: "CUSTOMER",
    renderHeader: () => <th className="table__head__cell">Customer</th>,
    renderCell: (requisition) => (
      <td className="table__cell">{requisition.customer.lastName}</td>
    ),
  },
  ITEM_COUNT: {
    id: "ITEM_COUNT",
    renderHeader: () => (
      <th className="table__head__cell cell--number">Item Count</th>
    ),
    renderCell: (requisition) => (
      <td className="table__cell cell--number">
        {requisition.lineItems.length}
      </td>
    ),
  },
  ORDERED_DATE: {
    id: "ORDERED_DATE",
    renderHeader: () => <th className="table__head__cell">Order Date</th>,
    renderCell: (requisition) => (
      <td className="table__cell">
        {dateUtils.isoToShortDateTime(requisition.orderedDateTime)}
      </td>
    ),
  },
  APPROVED_DATE: {
    id: "APPROVED_DATE",
    renderHeader: () => <th className="table__head__cell">Approved Date</th>,
    renderCell: (requisition) => (
      <td className="table__cell">
        {dateUtils.isoToShortDateTime(requisition.approvedDateTime)}
      </td>
    ),
  },
  ISSUER: {
    id: "ISSUER",
    renderHeader: () => <th className="table__head__cell">Issuer</th>,
    renderCell: (requisition) => (
      <td className="table__cell">{requisition.issuer?.lastName}</td>
    ),
  },
  SYNC_STATUS_ICON: {
    id: "SYNC_STATUS_ICON",
    renderHeader: () => <th className="table__head__cell">Sync Status</th>,
    renderCell: (requisition) => (
      <td className="">
        <div className="flex h-full items-center justify-center">
          {getSyncStatusIcon(requisition)}
        </div>
      </td>
    ),
  },
};

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
