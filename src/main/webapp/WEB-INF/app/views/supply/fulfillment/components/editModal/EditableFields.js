import { useLocations } from "app/views/supply/shared/hooks/useLocations";
import { useSupplyEmployees } from "app/views/supply/fulfillment/hooks/useSupplyEmployees";
import { Controller } from "react-hook-form";
import InputAutocomplete from "app/components/InputAutocomplete";
import { isoToShortDateTime } from "app/utils/dateUtils";
import { Link } from "react-router-dom";
import React from "react";

export default function EditableFields({ requisition, register, control }) {
  const locationQuery = useLocations();
  const supplyEmployeesQuery = useSupplyEmployees();
  return (
    <div className="grid grid-cols-2 gap-2">
      <div className="font-semibold">Status:</div>
      <div>{requisition.status}</div>

      <div className="font-semibold">Destination:</div>
      <div>
        <Controller
          name="destinationId"
          control={control}
          render={({ field }) => (
            <InputAutocomplete
              {...field}
              id="destinationId"
              name="destinationId"
              options={locationQuery.data?.map((loc) => loc.locId)}
            />
          )}
        />
      </div>

      <div className="font-semibold">Customer:</div>
      <div>{requisition.customer.fullName}</div>

      <div className="font-semibold">Delivery Method:</div>
      <div>
        <select className="select" {...register("deliveryMethod")}>
          <option value="DELIVERY">DELIVERY</option>
          <option value="PICKUP">PICKUP</option>
        </select>
      </div>

      <div className="font-semibold">Special Instructions:</div>
      <div>{requisition.specialInstructions || "None"}</div>

      <div className="font-semibold">Ordered Date:</div>
      <div>{isoToShortDateTime(requisition.orderedDateTime)}</div>

      <div className="font-semibold">Assigned To:</div>
      <div>
        <select
          className="select w-full"
          {...register("issuerEmpId", {
            setValueAs: (value) => (value === "" ? null : parseInt(value, 10)),
          })}
        >
          <option></option>
          {supplyEmployeesQuery.data?.map((emp) => (
            <option value={emp.employeeId} key={emp.employeeId}>
              {emp.fullName}
            </option>
          ))}
        </select>
      </div>

      {requisition.status === "APPROVED" && (
        <>
          <div className="font-semibold">Completed Date:</div>
          <div>{isoToShortDateTime(requisition.completedDateTime)}</div>
        </>
      )}

      {requisition.status === "REJECTED" && (
        <>
          <div className="font-semibold">Rejected By:</div>
          <div>{requisition.modifiedBy.lastName}</div>

          <div className="font-semibold">Rejected Date:</div>
          <div>{isoToShortDateTime(requisition.rejectedDateTime)}</div>
        </>
      )}

      <div className="font-semibold">Actions:</div>
      <div>
        <div>
          <Link to={`/supply/orders/${requisition.requisitionId}`}>
            View full history
          </Link>
        </div>
        <div>
          <Link to={`/supply/orders/${requisition.requisitionId}?print=true`}>
            Print Requisition
          </Link>
        </div>
      </div>
    </div>
  );
}
