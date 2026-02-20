import React, { useMemo } from "react";
import InputDebounced from "app/components/InputDebounced";
import { useItemsMap } from "app/views/supply/shared/hooks/useItems";
import {
  setDateRange,
  setFilter,
} from "app/views/supply/shared/lib/supplyFilterActions";
import { useLocations } from "app/views/supply/shared/hooks/useLocations";
import { useIssuers } from "app/views/supply/requisition-history/useIssuers";
import ComboBox, { createComboBoxOption } from "app/components/ComboBox";

export default function RequisitionHistoryFilters({ filters, dispatch }) {
  const itemsQuery = useItemsMap();
  const locationQuery = useLocations();
  const issuersQuery = useIssuers();

  const labelClasses = "block font-semibold";

  const destinationOptions = useMemo(() => {
    return (locationQuery.data ?? []).map((loc) => {
      const searchText = [loc.locId, loc.locationDescription]
        .filter(Boolean)
        .join(" ");

      return createComboBoxOption({
        key: loc.locId,
        textValue: loc.locId,
        optionLabel: loc.locId,
        optionDescription: loc.locationDescription || null,
        data: loc,
        searchText,
      });
    });
  }, [locationQuery.data]);

  const selectedCommodityKey =
    filters.itemId == null || filters.itemId === ""
      ? null
      : Number(filters.itemId);

  const commodityOptions = useMemo(() => {
    const itemsMap = itemsQuery.data;
    if (!itemsMap) return [];

    return Array.from(itemsMap.values(), (commodity) =>
      createComboBoxOption({
        key: commodity.id,
        textValue: commodity.commodityCode,
        data: commodity,
        searchText: commodity.commodityCode,
      }),
    );
  }, [itemsQuery.data]);

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
        <ComboBox
          label="Destination Code"
          selectedKey={filters.destinationId}
          onSelectionChange={({ key }) =>
            dispatch(setFilter("destinationId", key))
          }
          options={destinationOptions}
        />
      </div>
      <div>
        <ComboBox
          label="Commodity Code"
          selectedKey={selectedCommodityKey}
          onSelectionChange={({ key }) => dispatch(setFilter("itemId", key))}
          options={commodityOptions}
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
