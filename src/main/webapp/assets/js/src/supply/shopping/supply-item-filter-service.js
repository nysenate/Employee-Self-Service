angular.module('essSupply').service('SupplyItemFilterService', [itemFilterService]);

function itemFilterService() {

    function filterLineItemsByCategories(lineItems, categories) {
        if (categories.length === 0) {
            return lineItems;
        }
        var filtered = [];
        angular.forEach(lineItems, function (lineItem) {
            if (categories.indexOf(lineItem.item.category) !== -1) {
                filtered.push(lineItem);
            }
        });
        return filtered;
    }

    function filterLineItemsBySearch(lineItems, searchTerm) {
        var filtered = [];
        angular.forEach(lineItems, function (lineItem) {
            if (lineItem.item.description.indexOf(searchTerm.toUpperCase()) !== -1) {
                filtered.push(lineItem);
            }
        });
        return filtered
    }

    return {
        /**
         * Returns a new array of line items where each line item matches the given filters.
         * Filters based on selected categories and search term.
         *
         * Does not modify the supplied allowances array.
         *
         * @param lineItems An array of lineItems to filter.
         * @param categories An array of categories. The returned lineItems must belong
         * to at least one of these categories.
         * @param searchTerm A search term that the returned lineItems description must contain.
         */
        filterLineItems: function (lineItems, categories, searchTerm) {
            var filteredLineItems = angular.copy(lineItems);
            if (categories !== undefined && categories !== null && categories.length > 0) {
                filteredLineItems = filterLineItemsByCategories(filteredLineItems, categories);
            }
            if (searchTerm !== undefined && searchTerm !== null && searchTerm.length > 0) {
                filteredLineItems = filterLineItemsBySearch(filteredLineItems, searchTerm);
            }
            return filteredLineItems;
        }
    }
}
