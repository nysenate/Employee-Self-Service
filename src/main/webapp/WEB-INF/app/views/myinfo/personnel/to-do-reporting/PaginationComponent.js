import React from "react";
import styles from './PaginationComponent.module.css';

/**
 * Pagination component to navigate through pages.
 *
 * @param {number} currentPage - The current active page.
 * @param {number} totalPages - The total number of pages available.
 * @param {function(number)} onPageChange - Callback function to handle page change.
 * @returns {JSX.Element}
 */
const PaginationComponent = ({ currentPage, totalPages, onPageChange }) => {

  /**
   * Calculate the page numbers to be displayed in the pagination.
   *
   * @param {number} currentPage - The current active page.
   * @param {number} totalPages - The total number of pages available.
   * @returns {Array<number|string>} - Array of page numbers and ellipsis to be displayed.
   */
  const calculatePageNumbers = (currentPage, totalPages) => {
    let pageNumbers = [ 1 ];

    // Add ellipsis if current page is greater than 5 and total pages are greater than 9
    if (currentPage > 5 && totalPages > 9) {
      pageNumbers.push('...');
    }

    // Calculate range start based on current page and total pages
    let dl = Math.abs(currentPage - 1) - 4;
    let dr = Math.abs(currentPage - totalPages) - 4;
    let rangeStart = currentPage - 3;

    if (dl < 0) {
      rangeStart -= dl;
    }
    if (dr < 0) {
      rangeStart += dr;
    }

    // Add page numbers in the range to the array
    for (let i = rangeStart; i <= rangeStart + 6 && i < totalPages; i++) {
      if (i > 1 && i < totalPages) {
        pageNumbers.push(i);
      }
    }
    // Add ellipsis if current page is less than total pages minus 4 and total pages are greater than 9
    if (currentPage < totalPages - 4 && totalPages > 9) {
      pageNumbers.push('...');
    }
    if (totalPages > 1) {
      pageNumbers.push(totalPages);
    }
    return pageNumbers;
  };

  const pageNumbers = calculatePageNumbers(currentPage, totalPages);


  return (
    <div className={styles.pagination}>
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

export default PaginationComponent;