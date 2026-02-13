import { useItemsMap } from "app/views/supply/shared/hooks/useItems";
import React, { useState } from "react";
import InputAutocomplete from "app/components/InputAutocomplete";
import Button from "app/components/Button";

export default function AddItemInput({ append }) {
  const itemsQuery = useItemsMap();
  const [item, setItem] = useState(null);

  const appendItem = () => {
    if (itemsQuery.data.has(item?.id)) {
      append({ item: item, quantity: 0 });
    }
  };

  return (
    <div className="mt-4 flex items-baseline justify-center gap-2">
      <label htmlFor="addItem" className="font-light">
        Add Commodity Code
      </label>
      <InputAutocomplete
        id="addItem"
        name="addItem"
        value={item}
        onChange={(value) => setItem(value)}
        options={Array.from(itemsQuery.data?.values() || [])}
        displayValue={(item) => item?.commodityCode}
        className="inline-block w-44"
      />
      <Button variant="secondary" onPress={appendItem}>
        Add Item
      </Button>
    </div>
  );
}
