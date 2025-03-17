import React, { useEffect, useRef, useState } from "react";
import Pagination from "app/components/Pagination";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useItemSearch } from "app/views/supply/requisition/useItemSearch";
import Card from "app/components/Card";
import { setPage } from "app/views/supply/requisition/itemFilterActions";
import ItemCell from "app/views/supply/requisition/ItemCell";

export default function ItemListing({ filterState, dispatch }) {
  const { cart, destination } = useSupplyContext();
  const itemsQuery = useItemSearch(destination.locId, filterState);
  const handlePageChange = (page) => {
    dispatch(setPage(page));
  };

  if (itemsQuery.isPending) {
    return <LoadingIndicator />;
  }

  return (
    <Card className="my-3">
      <div>
        {itemsQuery.data.total > itemsQuery.data.result.length && (
          <Pagination
            currentPage={filterState.page}
            totalPages={Math.ceil(
              itemsQuery.data.total / itemsQuery.data.limit,
            )}
            onPageChange={handlePageChange}
          />
        )}
        <div className="grid grid-cols-4">
          {itemsQuery.data.result.map((item) => (
            <ItemCell item={item} key={item.id} />
          ))}
        </div>
        {itemsQuery.data.total > itemsQuery.data.result.length && (
          <Pagination
            currentPage={filterState.page}
            totalPages={Math.ceil(
              itemsQuery.data.total / itemsQuery.data.limit,
            )}
            onPageChange={handlePageChange}
          />
        )}
      </div>
    </Card>
  );
}
