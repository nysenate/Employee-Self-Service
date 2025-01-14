// PaginationModel.js
class PaginationModel {
  constructor() {
    this.firstPage = 1;
    this.currPage = 1;
    this.lastPage = 1;
    this.itemsPerPage = 6;
    this.totalItems = 0;
  }

  setTotalItems(totalResults) {
    this.totalItems = totalResults;
    this.lastPage = Math.ceil(this.totalItems / this.itemsPerPage);
    if (this.currPage > this.lastPage) {
      this.currPage = 1;
    }
  }

  reset() {
    this.currPage = 1;
  }

  needsPagination() {
    return this.totalItems > this.itemsPerPage;
  }

  onLastPage() {
    return this.currPage >= this.lastPage;
  }

  getOffset() {
    return (this.itemsPerPage * (this.currPage - 1)) + 1;
  }

  getLimit() {
    return this.itemsPerPage;
  }

  nextPage() {
    this.currPage += 1;
  }

  hasNextPage() {
    return this.currPage < this.lastPage;
  }

  prevPage() {
    this.currPage = Math.max(this.currPage - 1, 0);
  }

  hasPrevPage() {
    return this.currPage > this.firstPage;
  }

  toLastPage() {
    this.currPage = this.lastPage;
  }

  toFirstPage() {
    this.currPage = this.firstPage;
  }
}

export default PaginationModel;
