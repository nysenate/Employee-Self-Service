import React from "react";
import ReactPaginate from "react-paginate";
import {
  getOffset,
  getPageCount,
  getPageNumber,
} from "app/utils/paginationUtils";

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

  const page = getPageNumber(limit, offset);
  const pageCount = getPageCount(limit, total);

  const onPageChangeWrapper = ({ selected: selectedPage }) => {
    return onPageChange(getOffset(limit, selectedPage + 1));
  };

  const itemClassName = "hover:bg-gray-100 cursor-pointer";
  const linkClassName = "px-2 py-1 text-gray-500 border-none";

  return (
    <ReactPaginate
      pageCount={pageCount}
      pageRangeDisplayed={5}
      onPageChange={onPageChangeWrapper}
      marginPagesDisplayed={1}
      forcePage={page - 1} // react-paginate pages are 0 indexed.
      disableInitialCallback={true}
      nextLabel=">"
      previousLabel="<"
      containerClassName="flex space-x-0 md:space-x-2 justify-center m-3"
      pageClassName={itemClassName}
      pageLinkClassName={linkClassName}
      activeClassName="border-solid border border-teal-600"
      activeLinkClassName="text-teal-600"
      previousClassName={itemClassName}
      previousLinkClassName={linkClassName}
      nextClassName={itemClassName}
      nextLinkClassName={linkClassName}
      breakClassName={itemClassName}
      breakLinkClassName={linkClassName}
      disabledClassName=""
    />
  );
}
