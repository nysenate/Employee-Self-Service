import React from "react";
import { isoToShortDateTime } from "app/utils/dateUtils";

export default function OrderHistoryTable({ query }) {
  return (
    <table className="table--sticky table">
      <thead>
        <tr className="table__head__row">
          <th className="table__head__cell">Id</th>
          <th className="table__head__cell">Customer</th>
          <th className="table__head__cell">Destination</th>
          <th className="table__head__cell">Ordered Date</th>
          <th className="table__head__cell">Status</th>
        </tr>
      </thead>
      <tbody className="table__body table__body--highlight divide-y divide-gray-200/80">
        {query.data.result.map((order) => (
          <tr
            className="table__row"
            key={order.requisitionId}
            onClick={() => console.log(order)}
          >
            <td className="table__cell">{order.requisitionId}</td>
            <td className="table__cell">{order.customer.lastName}</td>
            <td className="table__cell">{order.destination.locId}</td>
            <td className="table__cell">
              {isoToShortDateTime(order.orderedDateTime)}
            </td>
            <td
              className={`table__cell font-semibold text-white ${statusBackgroundColor(order.status)}`}
            >
              {order.status}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

function statusBackgroundColor(status) {
  switch (status) {
    case "REJECTED":
      return "bg-red-600";
    case "PENDING":
      return "bg-orange-600";
    case "PROCESSING":
      return "bg-teal-600";
    case "COMPLETED":
    case "APPROVED":
      return "bg-green-600";
    default:
      console.error(`Unknown status: ${status}`);
  }
}
