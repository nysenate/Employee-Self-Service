import React, { useState } from "react";
import Card from "app/components/Card";
import NoMatchesFound from "app/components/NoMatchesFound";
import { isoToShortDateTime } from "app/utils/dateUtils";
import { setOffset } from "app/views/supply/shared/helpers/supplyFilterActions";
import RequisitionInfoModal from "app/views/supply/shared/componenets/RequisitionInfoModal";
import Pagination from "app/components/Pagination";

export default function RequisitionHistoryResults({ data, filters, dispatch }) {
  const [selectedReq, setSelectedReq] = useState(null);

  if (data.result.length === 0) {
    return <NoMatchesFound />;
  }

  return (
    <Card>
      <div className="p-4">
        <table className="table--sticky table">
          <thead>
            <tr className="table__head__row">
              <th className="table__head__cell">Id</th>
              <th className="table__head__cell">Location</th>
              <th className="table__head__cell">Customer</th>
              <th className="table__head__cell cell--number">Item Count</th>
              <th className="table__head__cell">Ordered Date</th>
              <th className="table__head__cell">Issued By</th>
            </tr>
          </thead>
          <tbody className="table__body table__body--highlight divide-y divide-gray-200/80">
            {data.result.map((r) => (
              <tr
                className="table__row"
                key={r.requisitionId}
                onClick={() => setSelectedReq(r)}
              >
                <td className="table__cell">{r.requisitionId}</td>
                <td className="table__cell">{r.destination.locId}</td>
                <td className="table__cell">{r.customer.lastName}</td>
                <td className="table__cell cell--number">
                  {r.lineItems.length}
                </td>
                <td className="table__cell">
                  {isoToShortDateTime(r.orderedDateTime)}
                </td>
                <td className="table__cell">{r.issuer?.lastName}</td>
              </tr>
            ))}
          </tbody>
        </table>
        <Pagination
          limit={filters.limit}
          offset={filters.offset}
          total={data.total}
          onPageChange={(offset) => dispatch(setOffset(offset))}
        />
      </div>
      <RequisitionInfoModal
        isOpen={selectedReq !== null}
        requisition={selectedReq}
        onResolve={() => setSelectedReq(null)}
      />
    </Card>
  );
}
