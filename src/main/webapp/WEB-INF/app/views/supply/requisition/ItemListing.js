import React, { useEffect, useState } from "react";
import ItemsGrid from "app/views/supply/requisition/ItemsGrid";
import styles from "app/views/supply/universalStyles.module.css";
import Pagination from "app/components/Pagination";

export default function ItemListing({
  items,
  cart,
  handleQuantityChange,
  handleOverOrderAttempt,
}) {
  const [pageItems, setPageItems] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 16;

  const handlePageChange = (page) => {
    if (page >= 1 && page <= Math.ceil(items.length / itemsPerPage)) {
      setCurrentPage(page);
    }
  };

  useEffect(() => {
    const startIndex = (currentPage - 1) * itemsPerPage;
    setPageItems(items.slice(startIndex, startIndex + itemsPerPage));
  }, [items, currentPage]);

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
