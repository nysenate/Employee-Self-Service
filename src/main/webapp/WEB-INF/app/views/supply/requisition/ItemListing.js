import React, { useEffect, useState } from "react";
import ItemsGrid from "app/views/supply/requisition/ItemsGrid";
import styles from "app/views/supply/universalStyles.module.css";
import Pagination from "app/components/Pagination";
import { useSearchParams } from "react-router-dom";
import { getItems } from "app/views/supply/helpers";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useItems } from "app/views/supply/requisition/useItems";

const ITEMS_PER_PAGE = 16;

export default function ItemListing({
  cart,
  handleQuantityChange,
  handleOverOrderAttempt,
  setCategories,
}) {
  const { destination } = useSupplyContext();
  const [term, setTerm] = useState("");
  const [sort, setSort] = useState("Name"); // TODO integrate with select element.
  const [limit, setLimit] = useState(ITEMS_PER_PAGE);
  const [offset, setOffset] = useState(1);
  const itemsQuery = useItems({
    locId: destination.locId,
    categories: [],
    term: term,
    sort: sort,
    limit: limit,
    offset: offset,
  });
  const [searchParams, setSearchParams] = useSearchParams();
  const selectedCategories = searchParams.getAll("category");

  const getCurrentPage = () => {
    return itemsQuery.data.offsetEnd / itemsQuery.data.limit;
  };

  const handlePageChange = (page) => {
    setOffset(page * limit - limit + 1);
  };

  if (itemsQuery.isPending) {
    return <LoadingIndicator />;
  }

  return (
    <div>
      {itemsQuery.data.total > itemsQuery.data.result.length && (
        <Pagination
          currentPage={getCurrentPage()}
          totalPages={Math.ceil(itemsQuery.data.total / itemsQuery.data.limit)}
          onPageChange={handlePageChange}
        />
      )}
      <ItemsGrid
        items={itemsQuery.data.result}
        cart={cart}
        handleQuantityChange={handleQuantityChange}
        handleOverOrderAttempt={handleOverOrderAttempt}
      />
      {itemsQuery.data.total > itemsQuery.data.result.length && (
        <div className={styles.contentContainer}>
          <Pagination
            currentPage={getCurrentPage()}
            totalPages={Math.ceil(
              itemsQuery.data.total / itemsQuery.data.limit,
            )}
            onPageChange={handlePageChange}
          />
        </div>
      )}
    </div>
  );
}

function extractCategoriesFromItems(items) {
  const uniqueCategories = new Set();
  items.forEach((item) => {
    uniqueCategories.add(item.category);
  });
  const categoriesArray = Array.from(uniqueCategories);
  return categoriesArray;
}

const arraysAreEqual = (arr1, arr2) => {
  if (arr1.length !== arr2.length) return false;
  for (let i = 0; i < arr1.length; i++) {
    if (arr1[i] !== arr2[i]) return false;
  }
  return true;
};
