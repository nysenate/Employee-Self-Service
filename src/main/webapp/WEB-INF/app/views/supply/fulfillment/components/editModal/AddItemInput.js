import { useItemsMap } from "app/views/supply/shared/hooks/useItems";
import React, { useMemo, useState } from "react";
import Button from "app/components/Button";
import ComboBox, { createComboBoxOption } from "app/components/ComboBox";

export default function AddItemInput({ append }) {
  const itemsQuery = useItemsMap();
  const [selectedItemId, setSelectedItemId] = useState(null);
  const [selectedItem, setSelectedItem] = useState(null);

  const options = useMemo(() => {
    return Array.from(itemsQuery.data?.values() || []).map((item) => {
      const description = item.description ?? item.name ?? item.itemName ?? "";
      const searchText = [item.commodityCode, item.description, item.category]
        .filter(Boolean)
        .join(" ");

      return createComboBoxOption({
        key: item.id,
        textValue: item.commodityCode,
        optionLabel: item.commodityCode,
        optionDescription: description || null,
        data: item,
        searchText,
      });
    });
  }, [itemsQuery.data]);

  const appendItem = () => {
    if (selectedItem) {
      append({ item: selectedItem, quantity: 1 });
      setSelectedItemId(null);
      setSelectedItem(null);
    }
  };

  return (
    <div className="mt-4 flex items-end justify-center gap-2">
      <ComboBox
        label="Add Commodity Code"
        className="inline-block w-56"
        placeholder="Search by commodity code"
        selectedKey={selectedItemId}
        onSelectionChange={({ key, option }) => {
          setSelectedItemId(key);
          setSelectedItem(option?.data ?? null);
        }}
        options={options}
      />
      <Button
        variant="secondary"
        onPress={appendItem}
        isDisabled={!selectedItem}
      >
        Add Item
      </Button>
    </div>
  );
}
