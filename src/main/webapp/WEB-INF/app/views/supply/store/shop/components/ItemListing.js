import React from "react";
import Pagination from "app/components/Pagination";
import { useSupplyContext } from "app/views/supply/store/useSupplyContext";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useItemSearch } from "app/views/supply/store/shop/hooks/useItemSearch";
import Card from "app/components/Card";
import { setOffset } from "app/views/supply/store/shop/utils/itemFilterActions";
import ItemCell from "app/views/supply/store/shop/components/ItemCell";

export default function ItemListing({ filterState, dispatch }) {
  const { destination } = useSupplyContext();
  const itemsQuery = useItemSearch(destination.locId, filterState);

  const handlePageChange = (offset) => {
    dispatch(setOffset(offset));
  };

  if (itemsQuery.isPending) {
    return <LoadingIndicator />;
  }

  return (
    <Card className="my-3">
      <div className="py-2">
        <Pagination
          limit={filterState.limit}
          offset={filterState.offset}
          total={itemsQuery.data.total}
          onPageChange={handlePageChange}
        />
        <div className="grid grid-cols-4 border-t border-l border-gray-300">
          {itemsQuery.data.result.map((item) => (
            <div key={item.id} className="">
              <ItemCell item={item} />
            </div>
          ))}
        </div>
        <Pagination
          limit={filterState.limit}
          offset={filterState.offset}
          total={itemsQuery.data.total}
          onPageChange={handlePageChange}
        />
      </div>
    </Card>
  );
}
