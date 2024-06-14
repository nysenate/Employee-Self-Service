import React from "react";
import styles from './Pagination.module.css';
const Pagination = ({ currentPage, totalPages, onPageChange, top }) => {
  const calculatePageNumbers = (currentPage, totalPages) => {
    let pageNumbers = [1];

    if (currentPage > 5 && totalPages > 9) {
      pageNumbers.push('...');
    }

    let dl = Math.abs(currentPage - 1) - 4;
    let dr = Math.abs(currentPage - totalPages) - 4;
    let rangeStart = currentPage - 3;

    if (dl < 0) {
      rangeStart -= dl;
    }
    if (dr < 0) {
      rangeStart += dr;
    }

    for (let i = rangeStart; i <= rangeStart + 6 && i < totalPages; i++) {
      if (i > 1 && i < totalPages) {
        pageNumbers.push(i);
      }
    }

    if (currentPage < totalPages - 4 && totalPages > 9) {
      pageNumbers.push('...');
    }
    pageNumbers.push(totalPages);

    return pageNumbers;
  };

  const pageNumbers = calculatePageNumbers(currentPage, totalPages);

  return (
    <div className={`${styles.pagination} ${top ? styles.paginationTop : styles.paginationBottom}`}>
      <button
        className={styles.pageButton}
        onClick={() => onPageChange(1)}
        disabled={currentPage === 1}
      >
        &laquo;
      </button>
      <button
        className={styles.pageButton}
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 1}
      >
        &lsaquo;
      </button>
      {pageNumbers.map((number, index) => (
        <button
          key={index}
          className={`${styles.pageButton} ${number === currentPage ? styles.activePage : ''}`}
          onClick={() => (typeof number === 'number') && onPageChange(number)}
        >
          {number}
        </button>
      ))}
      <button
        className={styles.pageButton}
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages}
      >
        &rsaquo;
      </button>
      <button
        className={styles.pageButton}
        onClick={() => onPageChange(totalPages)}
        disabled={currentPage === totalPages}
      >
        &raquo;
      </button>
    </div>
  );
};

export default Pagination;