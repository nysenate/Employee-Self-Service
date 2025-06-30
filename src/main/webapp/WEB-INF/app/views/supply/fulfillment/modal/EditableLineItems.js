import clsx from "clsx";
import React from "react";

export default function EditableLineItems({ register, fields, errors }) {
  return (
    <div>
      <table className="table">
        <thead>
          <tr className="table__head__row">
            <th className="table__head__cell table__head__cell--text">
              Commodity Code
            </th>
            <th className="table__head__cell table__head__cell--text">
              Description
            </th>
            <th className="table__head__cell table__head__cell--text">
              Quantity
            </th>
          </tr>
        </thead>
        <tbody className="table__body divide-y divide-gray-200/80">
          {fields.map((li, index) => (
            <tr className="table__row" key={li.id}>
              <td className="table__cell table__cell--text">
                {li.item.commodityCode}
              </td>
              <td className="table__cell table__cell--text">
                {li.item.description}
              </td>
              <td className="table__cell table__cell--number">
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
