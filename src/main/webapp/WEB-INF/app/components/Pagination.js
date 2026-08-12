import React from "react";
import ReactPaginate from "react-paginate";
import {
  getOffset,
  getPageCount,
  getPageNumber,
} from "app/utils/paginationUtils";
import { ChevronLeft, ChevronRight } from "lucide-react";

/**
 * Pagination component to navigate through pages.
 * @param limit - The number of results to show per page.
 * @param offset - The inclusive one-indexed start point for displaying results.
 * @param total - The total number of results.
 * @param onPageChange - Callback function executed when the user changes pages.
 *                       It is passed the offset for the selected page.
 */
export default function Pagination({
  limit = 12,
  offset,
  total,
  onPageChange,
}) {
  if (!total || !offset) {
    console.error(`Pagination component requires "offset" and "total" values.`);
    return null;
  }

  if (total <= limit) {
    return null;
  }

  const page = getPageNumber(limit, offset);
  const pageCount = getPageCount(limit, total);

  const onPageChangeWrapper = ({ selected: selectedPage }) => {
    return onPageChange(getOffset(limit, selectedPage + 1));
  };

  const linkClassName =
    "inline-flex h-9 min-w-9 items-center justify-center border border-transparent px-2 text-sm font-medium text-gray-600 outline-none transition hover:bg-gray-100 focus-visible:ring-2 focus-visible:ring-teal-600 focus-visible:ring-offset-1 aria-disabled:cursor-not-allowed aria-disabled:text-gray-300 aria-disabled:hover:bg-transparent";

  return (
    <ReactPaginate
      pageCount={pageCount}
      pageRangeDisplayed={3}
      onPageChange={onPageChangeWrapper}
      marginPagesDisplayed={1}
      forcePage={page - 1} // react-paginate pages are 0 indexed.
      disableInitialCallback={true}
      nextLabel={
        <span className="inline-flex items-center gap-1">
          <span className="hidden sm:inline">Next</span>
          <ChevronRight aria-hidden="true" className="h-4 w-4" />
        </span>
      }
      previousLabel={
        <span className="inline-flex items-center gap-1">
          <ChevronLeft aria-hidden="true" className="h-4 w-4" />
          <span className="hidden sm:inline">Previous</span>
        </span>
      }
      previousAriaLabel="Go to previous page"
      nextAriaLabel="Go to next page"
      ariaLabelBuilder={(pageNumber, isSelected) =>
        isSelected
          ? `Page ${pageNumber}, current page`
          : `Go to page ${pageNumber}`
      }
      containerClassName="mt-4 flex flex-wrap items-center justify-center gap-1"
      pageLinkClassName={linkClassName}
      activeLinkClassName="border-teal-600 bg-teal-50 text-teal-700"
      previousLinkClassName={`${linkClassName} sm:px-3`}
      nextLinkClassName={`${linkClassName} sm:px-3`}
      breakLinkClassName={linkClassName}
    />
  );
}
