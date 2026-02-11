import React from "react";
import InfoPopover from "app/views/travel/shared/components/InfoPopover";
import { isoToShortDate } from "app/utils/dateUtils";
import { toCurrency } from "app/utils/textUtils";

export default function MealDetailsPopover({ amendment }) {
  const mealPerDiem = amendment?.mealPerDiems ?? {};
  const rows = mealPerDiem.requestedMealPerDiems ?? [];

  if (rows.length === 0) {
    return <span>&nbsp;</span>;
  }

  return (
    <div className="flex items-center">
      <InfoPopover label="Meal Summary">
        <table className="table">
          <thead>
            <tr className="table__head__row">
              <th className="table__head__cell px-3">Date</th>
              <th className="table__head__cell px-3">Address</th>
              <th className="table__head__cell cell--number px-3">
                Breakfast
              </th>
              <th className="table__head__cell cell--number px-3">Dinner</th>
              <th className="table__head__cell cell--number px-3">Total</th>
            </tr>
          </thead>
          <tbody className="table__body">
            {rows.map((row) => (
              <tr key={row.id} className="table__row">
                <td className="table__cell px-3">
                  {isoToShortDate(row.date)}
                </td>
                <td className="table__cell px-3 whitespace-normal">
                  {row.address.formattedAddressWithCounty}
                </td>
                <td className="table__cell cell--number px-3">
                  {toCurrency(row.breakfast) || "-"}
                </td>
                <td className="table__cell cell--number px-3">
                  {toCurrency(row.dinner) || "-"}
                </td>
                <td
                  className={`table__cell cell--number px-3 ${
                    mealPerDiem.isOverridden ? "line-through" : ""
                  }`}
                >
                  {toCurrency(row.total) || "-"}
                </td>
              </tr>
            ))}
          </tbody>
          <tfoot>
            <tr className="table__totals">
              <td className="table__cell px-3" colSpan={4}>
                {mealPerDiem.isOverridden ? "Overridden to" : "Total"}
              </td>
              <td className="table__cell cell--number px-3">
                {toCurrency(mealPerDiem.totalPerDiem) || "-"}
              </td>
            </tr>
          </tfoot>
        </table>
      </InfoPopover>
    </div>
  );
}
