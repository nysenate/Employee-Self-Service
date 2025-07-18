import clsx from "clsx";
import React from "react";
import {
  boldItemRow,
  boldRequisitionRow,
  highlightItemRow,
  highlightRequisitionRow,
} from "app/views/supply/fulfillment/fulfillmentUtils";
import { useLocationStatistics } from "app/views/supply/fulfillment/useLocationStatistics";

export default function EditableLineItems({
  register,
  fields,
  errors,
  destination,
}) {
  const locationStatisticsQuery = useLocationStatistics(
    new Date().getFullYear(),
    new Date().getMonth() + 1,
  );

  return (
    <div>
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell">Commodity Code</th>
            <th className="table__head__cell">Description</th>
            <th className="table__head__cell">Quantity</th>
          </tr>
        </thead>
        <tbody className="table__body divide-y divide-gray-200/80">
          {fields.map((li, index) => (
            <tr
              key={li.id}
              className={clsx(
                "table__row",
                highlightItemRow(
                  li,
                  locationStatisticsQuery.data,
                  destination,
                ) && "bg-red-400/50",
                boldItemRow(li, locationStatisticsQuery.data, destination) &&
                  "font-semibold",
              )}
            >
              <td className="table__cell">{li.item.commodityCode}</td>
              <td className="table__cell">{li.item.description}</td>
              <td className="table__cell cell--number">
                <input
                  {...register(`lineItems.${index}.quantity`, {
                    required: "Line Item must have a quantity.",
                  })}
                  className={clsx(
                    "input w-16",
                    errors?.lineItems?.[index] && "input--invalid",
                  )}
                  type="number"
                  autoComplete="off"
                />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <div></div>
    </div>
  );
}
