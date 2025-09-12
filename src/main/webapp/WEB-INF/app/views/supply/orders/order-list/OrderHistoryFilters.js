import React from "react";
import {
  setDateRange,
  setFilter,
} from "app/views/supply/shared/helpers/supplyFilterActions";
import InputDebounced from "app/components/InputDebounced";

export default function OrderHistoryFilters({ filters, dispatch }) {
  const onStatusClick = (e) => {
    let dirtyStatuses;
    const toggledStatus = e.target.value;
    if (filters.status.includes(toggledStatus)) {
      dirtyStatuses = filters.status.filter((s) => s !== toggledStatus);
    } else {
      dirtyStatuses = [...filters.status, toggledStatus];
    }
    dispatch(setFilter("status", dirtyStatuses));
  };

  const labelClasses = "block font-semibold";
  return (
    <div className="grid grid-cols-3 gap-4">
      <div>
        <label htmlFor="fromDate" className={labelClasses}>
          From Date
        </label>
        <InputDebounced
          id="fromDate"
          value={filters.from}
          type="date"
          onChange={(value) => dispatch(setDateRange(value, filters.to))}
          max={filters.toDate}
        />
      </div>
      <div>
        <label htmlFor="toDate" className={labelClasses}>
          To Date
        </label>
        <InputDebounced
          id="toDate"
          value={filters.to}
          type="date"
          onChange={(value) => dispatch(setDateRange(filters.from, value))}
          min={filters.from}
        />
      </div>
      <div>
        <label htmlFor="statuses" className={labelClasses}>
          Statuses
        </label>
        <div id="statuses" className="grid grid-cols-2">
          <div>
            <Checkbox
              id="pendingStatus"
              value="PENDING"
              label="Pending"
              selectedStatuses={filters.status}
              onChange={onStatusClick}
            />
            <Checkbox
              id="processingStatus"
              value="PROCESSING"
              label="Processing"
              selectedStatuses={filters.status}
              onChange={onStatusClick}
            />
            <Checkbox
              id="completedStatus"
              value="COMPLETED"
              label="Completed"
              selectedStatuses={filters.status}
              onChange={onStatusClick}
            />
          </div>
          <div>
            <Checkbox
              id="approvedStatus"
              value="APPROVED"
              label="Approved"
              selectedStatuses={filters.status}
              onChange={onStatusClick}
            />
            <Checkbox
              id="rejectedStatus"
              value="REJECTED"
              label="Rejected"
              selectedStatuses={filters.status}
              onChange={onStatusClick}
            />
          </div>
        </div>
      </div>
    </div>
  );
}

function Checkbox({ id, value, label, selectedStatuses, onChange }) {
  return (
    <div className="flex items-center gap-1">
      <input
        id={id}
        type="checkbox"
        name="status"
        value={value}
        checked={selectedStatuses.includes(value)}
        onChange={onChange}
      />
      <label htmlFor={id}>{label}</label>
    </div>
  );
}
