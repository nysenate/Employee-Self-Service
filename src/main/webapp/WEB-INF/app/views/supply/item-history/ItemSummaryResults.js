import Card from "app/components/Card";
import React, { useEffect, useState } from "react";
import { setOffset } from "app/views/supply/shared/lib/supplyFilterActions";
import NoMatchesFound from "app/components/NoMatchesFound";
import clsx from "clsx";
import * as dateUtils from "app/utils/dateUtils";
import Pagination from "app/components/Pagination";
import { useNavigate } from "react-router-dom";

export default function ItemSummaryResults({
  itemSummaries,
  filters,
  dispatch,
}) {
  const [selectedRow, setSelectedRow] = useState(null);
  const [pageData, setPageData] = useState([]);

  useEffect(() => {
    setPageData(
      itemSummaries.slice(
        filters.offset - 1,
        filters.offset - 1 + filters.limit,
      ),
    );
  }, [itemSummaries, filters.limit, filters.offset]);

  if (itemSummaries.length === 0) {
    return <NoMatchesFound />;
  }

  const toggleSelection = (itemId) => {
    if (selectedRow === itemId) {
      setSelectedRow(null);
    } else {
      setSelectedRow(itemId);
    }
  };

  return (
    <Card>
      <div className="p-4">
        <table className="table--sticky table">
          <thead>
            <tr className="table__head__row">
              <th className="table__head__cell w-3/12">Commodity Code</th>
              <th className="table__head__cell w-7/12">Description</th>
              <th className="table__head__cell cell--number w-2/12">
                Quantity
              </th>
            </tr>
          </thead>
          <tbody className="table__body table__body--highlight divide-y divide-gray-200/80">
            {pageData.map((summary) => (
              <React.Fragment key={summary.item.id}>
                <tr
                  className={clsx("table__row", {
                    "bg-gray-75": selectedRow === summary.item.id,
                  })}
                  onClick={() => toggleSelection(summary.item.id)}
                >
                  <td className="table__cell">{summary.item.commodityCode}</td>
                  <td className="table__cell">{summary.item.description}</td>
                  <td className="table__cell cell--number">
                    {summary.totalQuantity}
                  </td>
                </tr>
                {selectedRow === summary.item.id && (
                  <tr>
                    <td colSpan="3" className="bg-gray-75 pb-6 pl-12">
                      <ItemOccurrencesTable occurrences={summary.occurrences} />
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))}
          </tbody>
        </table>
        <Pagination
          limit={filters.limit}
          offset={filters.offset}
          total={itemSummaries.length}
          onPageChange={(offset) => dispatch(setOffset(offset))}
        />
      </div>
    </Card>
  );
}

function ItemOccurrencesTable({ occurrences }) {
  const navigate = useNavigate();
  return (
    <table className="table">
      <thead>
        <tr className="table__head__row text-purple-700">
          <th className="table__head__cell">Requisition Id</th>
          <th className="table__head__cell">Location</th>
          <th className="table__head__cell">Ordered By</th>
          <th className="table__head__cell">Completed Date</th>
          <th className="table__head__cell cell--number">Quantity</th>
        </tr>
      </thead>
      <tbody className="table__body table__body--highlight divide-y divide-gray-200/80">
        {occurrences.map((occurrence) => (
          <tr
            key={occurrence.itemId}
            className="table__row"
            onClick={() =>
              navigate(`/supply/orders/${occurrence.requisition.requisitionId}`)
            }
          >
            <td className="table__cell">
              {occurrence.requisition.requisitionId}
            </td>
            <td className="table__cell">
              {occurrence.requisition.destination.locId}
            </td>
            <td className="table__cell">
              {occurrence.requisition.customer.lastName}
            </td>
            <td className="table__cell">
              {dateUtils.isoToShortDateTime(
                occurrence.requisition.completedDateTime,
              )}
            </td>
            <td className="table__cell cell--number">{occurrence.quantity}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
