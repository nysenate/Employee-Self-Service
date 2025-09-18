import {
  resetFilters,
  setDateRange,
  setFilter,
} from "app/views/supply/shared/helpers/supplyFilterActions";
import React from "react";
import InputDebounced from "app/components/InputDebounced";
import Button from "app/components/Button";

export default function ItemSummaryFilters({ filters, dispatch }) {
  const labelClasses = "block font-semibold";
  return (
    <div className="grid grid-cols-[1fr_1fr_auto] items-end gap-4">
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

      <div className="mb-px pb-1.5">
        <Button
          className="print:hidden"
          variant="text"
          onClick={() => dispatch(resetFilters())}
        >
          Reset
        </Button>
      </div>

      <div>
        <label htmlFor="commodityCode" className={labelClasses}>
          Commodity Code
        </label>
        <InputDebounced
          id="Commodity Code"
          value={filters.commodityCode || ""}
          placeholder="Search commodity..."
          onChange={(value) =>
            dispatch(setFilter("commodityCode", value || null))
          }
          className="w-1/2"
        />
      </div>

      <div>
        <label htmlFor="locationCode" className={labelClasses}>
          Location Code
        </label>
        <InputDebounced
          id="locationCode"
          value={filters.locationCode || ""}
          placeholder="Search location..."
          onChange={(value) =>
            dispatch(setFilter("locationCode", value || null))
          }
          className="w-1/2"
        />
      </div>

      <div className="mb-px pb-1.5">
        <Button
          className="print:hidden"
          variant="text"
          onClick={() => window.print()}
        >
          Print Report
        </Button>
      </div>
    </div>
  );
}
