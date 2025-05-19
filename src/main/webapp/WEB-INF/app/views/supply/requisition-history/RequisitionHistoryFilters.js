import React from "react";
import InputDebounced from "app/views/myinfo/personnel/pec/InputDebounced";
import { useItems } from "app/views/supply/useItems";
import {
  setDateRange,
  setFilter,
} from "app/views/supply/requisition-history/RequisitionHistoryActions";
import { useLocations } from "app/views/supply/useLocations";

export default function RequisitionHistoryFilters({ filters, dispatch }) {
  const itemsQuery = useItems();
  const locationQuery = useLocations();
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
        <select
          id="destinationCode"
          name="destinationCode"
          className="select"
          value={filters.destinationCode ?? ""}
          onChange={(e) => {
            dispatch(setFilter("destinationCode", e.target.value));
          }}
        >
          <option value="All">All</option>
          {locationQuery.data?.map((loc) => (
            <option key={loc.locId} value={loc.locId}>
              {loc.locId}
            </option>
          ))}
        </select>
      </div>
      <div>
        <label htmlFor="itemId" className={labelClasses}>
          Commodity Code
        </label>
        <select
          id="itemId"
          name="itemId"
          className="select"
          value={filters.itemId ?? ""}
          onChange={(e) => {
            dispatch(setFilter("itemId", e.target.value));
          }}
        >
          <option value="All">All</option>
          {itemsQuery.data?.map((item) => (
            <option key={item.id} value={item.id}>
              {item.commodityCode}
            </option>
          ))}
        </select>
      </div>
      <div>issuerId</div>
    </div>
  );
}
