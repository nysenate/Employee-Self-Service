import React from "react";
import { toCurrency } from "app/utils/textUtils";
import InfoPopover from "app/views/travel/shared/components/InfoPopover";

/**
 * Displays an Info icon which when clicked reveals a popover containing
 * transportation expense details.
 * If there is no info to display, an empty space is rendered instead of the Info icon.
 * @param amendment
 * @returns {JSX.Element}
 * @constructor
 */
export default function TransportationDetailsPopover({ amendment }) {
  const mileagePerDiem = amendment?.mileagePerDiems ?? {};
  const rows = mileagePerDiem.requestedPerDiems ?? [];

  if (rows.length === 0) {
    return <span>&nbsp;</span>;
  }

  return (
    <div className="flex items-center">
      <InfoPopover label="Transportation Summary">
        <table className="table">
          <thead>
            <tr className="table__head__row">
              <th className="table__head__cell px-3">From</th>
              <th className="table__head__cell px-3">To</th>
              <th className="table__head__cell cell--number px-3">Rate</th>
              <th className="table__head__cell cell--number px-3">Miles</th>
              <th className="table__head__cell cell--number px-3">
                Allowance
              </th>
            </tr>
          </thead>
          <tbody className="table__body">
            {rows.map((row) => (
              <tr key={row.id} className="table__row">
                <td className="table__cell px-3 whitespace-normal">
                  {row.from.formattedAddressWithCounty}
                </td>
                <td className="table__cell px-3 whitespace-normal">
                  {row.to.formattedAddressWithCounty}
                </td>
                <td className="table__cell cell--number px-3">
                  {row.mileageRate}
                </td>
                <td className="table__cell cell--number px-3">{row.miles}</td>
                <td className="table__cell cell--number px-3">
                  {toCurrency(row.requestedPerDiem) || "-"}
                </td>
              </tr>
            ))}
          </tbody>
          <tfoot>
            <tr className="table__totals">
              <td className="table__cell px-3" colSpan={3}>
                Total
              </td>
              <td className="table__cell cell--number px-3">
                {mileagePerDiem.totalMileage}
              </td>
              <td className="table__cell cell--number px-3">
                {toCurrency(mileagePerDiem.totalPerDiem) || "-"}
              </td>
            </tr>
          </tfoot>
        </table>
      </InfoPopover>
    </div>
  );
}
