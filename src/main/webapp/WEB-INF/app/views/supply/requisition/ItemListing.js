import React, { useEffect, useState } from "react";
import ItemsGrid from "app/views/supply/requisition/ItemsGrid";
import styles from "app/views/supply/universalStyles.module.css";
import Pagination from "app/components/Pagination";
import { useSearchParams } from "react-router-dom";
import { getItems } from "app/views/supply/helpers";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useItems } from "app/views/supply/requisition/useItems";

export default function ItemListing({
  cart,
  handleQuantityChange,
  handleOverOrderAttempt,
  setCategories,
}) {
  const { destination } = useSupplyContext();
  const [searchParams, setSearchParams] = useSearchParams();
  const itemsQuery = useItems(destination);
  const selectedCategories = searchParams.getAll("category");
  const [pageItems, setPageItems] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 16;

  const handlePageChange = (page) => {
    if (page >= 1 && page <= Math.ceil(items.length / itemsPerPage)) {
      setCurrentPage(page);
    }
  };

  // useEffect(() => {
  //   const startIndex = (currentPage - 1) * itemsPerPage;
  //   setPageItems(items.slice(startIndex, startIndex + itemsPerPage));
  // }, [pageItems, currentPage]);
  //

  useEffect(() => {
    let filtered = pageItems;
    const updatedFilteredItems = items.filter((item) =>
      selectedCategories.includes(item.category),
    );
    if (!arraysAreEqual(filtered, updatedFilteredItems)) {
      filtered = updatedFilteredItems;
    }
    const startIndex = (currentPage - 1) * itemsPerPage;
    setPageItems(filtered.slice(startIndex, startIndex + itemsPerPage));
  }, [selectedCategories, itemsQuery.data, currentPage]);

  if (itemsQuery.isPending) {
    return <LoadingIndicator />;
  }

  return (
    <div>
      {items.length > itemsPerPage && (
        <Pagination
          currentPage={currentPage}
          totalPages={Math.ceil(items.length / itemsPerPage)}
          onPageChange={handlePageChange}
        />
      )}
      <ItemsGrid
        items={pageItems}
        cart={cart}
        handleQuantityChange={handleQuantityChange}
        handleOverOrderAttempt={handleOverOrderAttempt}
      />
      {items.length > itemsPerPage && (
        <div className={styles.contentContainer}>
          <Pagination
            currentPage={currentPage}
            totalPages={Math.ceil(items.length / itemsPerPage)}
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
