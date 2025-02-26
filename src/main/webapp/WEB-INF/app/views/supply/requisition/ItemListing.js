import React, { useEffect, useRef, useState } from "react";
import ItemsGrid from "app/views/supply/requisition/ItemsGrid";
import styles from "app/views/supply/universalStyles.module.css";
import Pagination from "app/components/Pagination";
import { useSearchParams } from "react-router-dom";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useItems } from "app/views/supply/requisition/useItems";
import Card from "app/components/Card";
import { Button } from "app/components/Button";

const ITEMS_PER_PAGE = 16;

export default function ItemListing({
  cart,
  handleQuantityChange,
  handleOverOrderAttempt,
  setCategories,
}) {
  const { destination } = useSupplyContext();
  const [term, setTerm] = useState({ dirty: "", saved: "" });
  const [sort, setSort] = useState("Name");
  const [limit, setLimit] = useState(ITEMS_PER_PAGE);
  const [offset, setOffset] = useState(1);
  const itemsQuery = useItems({
    locId: destination.locId,
    categories: [],
    term: term.saved,
    sort: sort,
    limit: limit,
    offset: offset,
  });
  const [searchParams, setSearchParams] = useSearchParams();
  const selectedCategories = searchParams.getAll("category");

  return (
    <Card className="my-3">
      <ItemFilters
        term={term}
        setTerm={setTerm}
        sort={sort}
        setSort={setSort}
      />
      {itemsQuery.isPending ? (
        <LoadingIndicator />
      ) : (
        <ItemResults
          itemsQuery={itemsQuery}
          setOffset={setOffset}
          limit={limit}
          cart={cart}
          handleQuantityChange={handleQuantityChange}
          handleOverOrderAttempt={handleOverOrderAttempt}
        />
      )}
    </Card>
  );
}

// function extractCategoriesFromItems(items) {
//   const uniqueCategories = new Set();
//   items.forEach((item) => {
//     uniqueCategories.add(item.category);
//   });
//   const categoriesArray = Array.from(uniqueCategories);
//   return categoriesArray;
// }
//
// const arraysAreEqual = (arr1, arr2) => {
//   if (arr1.length !== arr2.length) return false;
//   for (let i = 0; i < arr1.length; i++) {
//     if (arr1[i] !== arr2[i]) return false;
//   }
//   return true;
// };

function ItemResults({
  itemsQuery,
  setOffset,
  limit,
  cart,
  handleQuantityChange,
  handleOverOrderAttempt,
}) {
  const getCurrentPage = () => {
    return itemsQuery.data.offsetEnd / itemsQuery.data.limit;
  };

  const handlePageChange = (page) => {
    setOffset(page * limit - limit + 1);
  };

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

function ItemFilters({ term, setTerm, sort, setSort }) {
  const handleInputChange = (e) => {
    setTerm({ ...term, dirty: e.target.value });
  };

  const handleSave = () => {
    setTerm({ ...term, saved: term.dirty });
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      handleSave();
    }
  };

  return (
    <div className="flex justify-between gap-3 p-3">
      <div></div>
      <div>
        <label>
          Item name:
          <input
            id="term"
            name="term"
            type="text"
            className="input mx-2"
            value={term.dirty}
            onChange={handleInputChange}
            onKeyDown={handleKeyDown}
          />
        </label>
        <Button color="success" onClick={handleSave}>
          Search
        </Button>
        <Button color="secondary" className="mx-2">
          Reset
        </Button>
      </div>
      <div>
        <label>
          Sort By:
          <select
            id="sort"
            name="sort"
            value={sort}
            className="select mx-2"
            onChange={(e) => setSort(e.target.value)}
          >
            <option value="Name">Name</option>
            <option value="Category">Category</option>
          </select>
        </label>
      </div>
    </div>
  );
}
