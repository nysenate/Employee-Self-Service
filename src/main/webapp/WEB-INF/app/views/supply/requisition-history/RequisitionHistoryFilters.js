import React from "react";
import InputDebounced from "app/components/InputDebounced";
import { useItemsMap } from "app/views/supply/shared/hooks/useItems";
import {
  setDateRange,
  setFilter,
} from "app/views/supply/shared/lib/supplyFilterActions";
import { useLocations } from "app/views/supply/shared/hooks/useLocations";
import InputAutocomplete from "app/components/InputAutocomplete";
import { useIssuers } from "app/views/supply/requisition-history/useIssuers";

export default function RequisitionHistoryFilters({ filters, dispatch }) {
  const itemsQuery = useItemsMap();
  const locationQuery = useLocations();
  const issuersQuery = useIssuers();

  const labelClasses = "block font-semibold";

  const selectedDestination =
    locationQuery.data?.find((loc) => loc.locId === filters.destinationId) ??
    null;

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
        <label htmlFor="destinationId" className={labelClasses}>
          Destination Code
        </label>
        <InputAutocomplete
          id="destinationId"
          name="destinatioinId"
          value={selectedDestination}
          onChange={(value) =>
            dispatch(setFilter("destinationId", value?.locId))
          }
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
          className="w-44"
        />
      </div>
      <div>
        <label htmlFor="itemId" className={labelClasses}>
          Commodity Code
        </label>
        <InputAutocomplete
          id="itemId"
          name="itemId"
          value={itemsQuery.data?.get(filters.itemId) || null}
          onChange={(value) => dispatch(setFilter("itemId", value?.id))}
          options={Array.from(itemsQuery.data?.values() || [])}
          displayValue={(item) => item?.commodityCode}
          className="w-44"
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
