export function getPageNumber(limit, offset) {
  return Math.floor((offset - 1) / limit) + 1;
}

export function getPageCount(limit, total) {
  return Math.ceil(total / limit);
}

/**
 * @param limit - The number of results per page.
 * @param page - The one indexed page number.
 * @returns {number} The offset for the given limit and page.
 */
export function getOffset(limit, page) {
  return (page - 1) * limit + 1;
}
