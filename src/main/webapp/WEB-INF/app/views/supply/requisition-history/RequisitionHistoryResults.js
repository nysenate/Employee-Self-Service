import React, { useState } from "react";
import Card from "app/components/Card";
import NoMatchesFound from "app/components/NoMatchesFound";
import { isoToShortDateTime } from "app/utils/dateUtils";
import PaginationComponent from "app/components/PaginationComponent";
import { setOffset } from "app/views/supply/requisition-history/RequisitionHistoryActions";
import RequisitionInfoModal from "app/views/supply/RequisitionInfoModal";

export default function RequisitionHistoryResults({ data, filters, dispatch }) {
  const [selectedReq, setSelectedReq] = useState(null);
  const results = data.result;
  const currentPage = (filters.offset + filters.limit - 1) / filters.limit;
  const totalPages = Math.ceil(data.total / filters.limit);

  if (results.length === 0) {
    return <NoMatchesFound />;
  }

  return (
    <Card>
      <div className="p-4">
        <table className="table--sticky table">
          <thead>
            <tr className="table__head__row">
              <th className="table__head__cell table__head__cell--text">Id</th>
              <th className="table__head__cell table__head__cell--text">
                Location
              </th>
              <th className="table__head__cell table__head__cell--text">
                Customer
              </th>
              <th className="table__head__cell table__head__cell--number">
                Item Count
              </th>
              <th className="table__head__cell table__head__cell--text">
                Ordered Date
              </th>
              <th className="table__head__cell table__head__cell--text">
                Issued By
              </th>
            </tr>
          </thead>
          <tbody className="table__body table__body--highlight divide-y divide-gray-200/80">
            {results.map((r) => (
              <tr
                className="table__row"
                key={r.requisitionId}
                onClick={() => setSelectedReq(r)}
              >
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
                  {isoToShortDateTime(r.orderedDateTime)}
                </td>
                <td className="table__cell table__cell--text">
                  {r.issuer?.lastName}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <PaginationComponent
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={(page) =>
            dispatch(setOffset(page * filters.limit - filters.limit + 1))
          }
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
