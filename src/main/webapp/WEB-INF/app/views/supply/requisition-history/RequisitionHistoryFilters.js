import React from "react";
import InputDebounced from "app/components/InputDebounced";
import { useItems } from "app/views/supply/useItems";
import {
  setDateRange,
  setFilter,
} from "app/views/supply/requisition-history/RequisitionHistoryActions";
import { useLocations } from "app/views/supply/useLocations";
import InputAutocomplete from "app/components/InputAutocomplete";
import { useIssuers } from "app/views/supply/requisition-history/useIssuers";

export default function RequisitionHistoryFilters({ filters, dispatch }) {
  const itemsQuery = useItems();
  const locationQuery = useLocations();
  const issuersQuery = useIssuers();

  const labelClasses = "block font-semibold";

  return (
    <div className="grid grid-cols-3 gap-4">
      <div>
        <label htmlFor="fromDate" className={labelClasses}>
          From Date
        </label>
        <InputDebounced
          id="fromDate"
          value={filters.fromDate}
          type="date"
          onChange={(value) => dispatch(setDateRange(value, filters.toDate))}
          max={filters.toDate}
        />
      </div>
      <div>
        <label htmlFor="toDate" className={labelClasses}>
          To Date
        </label>
        <InputDebounced
          id="toDate"
          value={filters.toDate}
          type="date"
          onChange={(value) => dispatch(setDateRange(filters.fromDate, value))}
          min={filters.fromDate}
        />
      </div>
      <div></div>
      <div>
        <label htmlFor="destinationCode" className={labelClasses}>
          Destination Code
        </label>
        <InputAutocomplete
          id="destinationCode"
          name="destinatioinCode"
          value={filters.destination}
          onChange={(value) => dispatch(setFilter("destination", value))}
          options={locationQuery.data ?? []}
          displayValue={(loc) => loc?.locId}
          renderOption={(loc) => (
            <div>
              <div>{loc.locId}</div>
              <div className="text-xs font-light">
                {loc.locationDescription}
              </div>
            </div>
          )}
        />
      </div>
      <div>
        <label htmlFor="commodityCode" className={labelClasses}>
          Commodity Code
        </label>
        <InputAutocomplete
          id="commodityCode"
          name="commodityCode"
          value={filters.item}
          onChange={(value) => dispatch(setFilter("item", value))}
          options={itemsQuery.data ?? []}
          displayValue={(item) => item?.commodityCode}
        />
      </div>
      <div>
        <label htmlFor="issuer" className={labelClasses}>
          Issuer
        </label>
        <select
          id="issuer"
          name="issuer"
          className="select"
          value={filters.issuerId ?? ""}
          onChange={(e) => dispatch(setFilter("issuerId", e.target.value))}
        >
          <option>All</option>
          {issuersQuery.data?.map((issuer) => (
            <option key={issuer.employeeId} value={issuer.employeeId}>
              {issuer.fullName}
            </option>
          ))}
        </select>
      </div>
    </div>
  );
}
