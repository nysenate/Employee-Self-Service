import React from "react";
import InfoPopover from "app/views/travel/shared/components/InfoPopover";
import { isoToShortDate } from "app/utils/dateUtils";
import { toCurrency } from "app/utils/textUtils";

export default function LodgingDetailsPopover({ amendment }) {
  const lodgingPerDiem = amendment?.lodgingPerDiems ?? {};
  const rows = lodgingPerDiem.requestedLodgingPerDiems ?? [];

  if (rows.length === 0) {
    return <span>&nbsp;</span>;
  }

  return (
    <div className="flex items-center">
      <InfoPopover label="Lodging Summary">
        <table className="table">
          <thead>
            <tr className="table__head__row">
              <th className="table__head__cell px-3">Date</th>
              <th className="table__head__cell px-3">Address</th>
              <th className="table__head__cell cell--number px-3">
                Lodging Per Diem
              </th>
            </tr>
          </thead>
          <tbody className="table__body">
            {rows.map((row) => (
              <tr key={row.id} className="table__row">
                <td className="table__cell px-3">{isoToShortDate(row.date)}</td>
                <td className="table__cell px-3 whitespace-normal">
                  {row.address.formattedAddressWithCounty}
                </td>
                <td
                  className={`table__cell cell--number px-3 ${
                    lodgingPerDiem.isOverridden ? "line-through" : ""
                  }`}
                >
                  {toCurrency(row.rate) || "-"}
                </td>
              </tr>
            ))}
          </tbody>
          <tfoot>
            <tr className="table__totals">
              <td className="table__cell px-3" colSpan={2}>
                {lodgingPerDiem.isOverridden ? "Overridden to" : "Total"}
              </td>
              <td className="table__cell cell--number px-3">
                {toCurrency(lodgingPerDiem.totalPerDiem) || "-"}
              </td>
            </tr>
          </tfoot>
        </table>
      </InfoPopover>
    </div>
  );
}
