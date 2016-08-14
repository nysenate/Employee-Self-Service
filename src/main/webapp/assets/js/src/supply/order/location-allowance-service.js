angular.module('essSupply').service('SupplyLocationAllowanceService',
    ['SupplyLocationAllowanceApi', locationAllowanceService]);

/**
 * Stores the item allowances while a user is making an order.
 * The allowances are set when an employee selects a destination for their order.
 * @param allowanceApi
 * @returns {{queryLocationAllowance: queryLocationAllowance, filterAllowances: filterAllowances, getAllowances: getAllowances, getAllowedQuantities: getAllowedQuantities}}
 */
function locationAllowanceService(allowanceApi) {

    var allowances = null;

    function filterAllowancesByCategories(allowances, categories) {
        if (categories.length === 0) {
            return allowances;
        }
        var filtered = [];
        angular.forEach(allowances, function (allowance) {
            if (categories.indexOf(allowance.item.category.name) !== -1) {
                filtered.push(allowance);
            }
        });
        return filtered;
    }

    function filterAllowancesBySearch(allowances, searchTerm) {
        var filtered = [];
        angular.forEach(allowances, function (allowance) {
            if (allowance.item.description.indexOf(searchTerm.toUpperCase()) !== -1) {
                filtered.push(allowance);
            }
        });
        return filtered
    }

    return {
        queryLocationAllowance: function (location) {
            return allowanceApi.get({id: location.locId}, function (response) {
                allowances = response.result.itemAllowances;
            }).$promise;
        },

        /**
         * Returns a new array of allowances with each element belonging to one of the
         * supplied categories and with a description matching the search term.
         *
         * Does not modify the supplied allowances array.
         *
         * @param allowances An array of allowances to filter.
         * @param categories An array of categories. The returned allowances must belong
         * to at least one of these categories.
         * @param searchTerm A search term that the returned allowance's description must contain.
         */
        filterAllowances: function (allowances, categories, searchTerm) {
            var filteredAllowances = angular.copy(allowances);
            if (categories !== undefined && categories !== null && categories.length > 0) {
                filteredAllowances = filterAllowancesByCategories(filteredAllowances, categories);
            }
            if (searchTerm !== undefined && searchTerm !== null && searchTerm.length > 0) {
                filteredAllowances = filterAllowancesBySearch(filteredAllowances, searchTerm);
            }
            return filteredAllowances;
        },

        getAllowances: function () {
            return allowances;
        },

        /**
         * Returns an array with integers from 1 to the per order allowance for an allowance.
         */
        getAllowedQuantities: function (item) {
            // Some items have an per order allowance of 99999, this is too much, cap it at 50.
            if (item.maxQtyPerOrder > 50) {
                item.maxQtyPerOrder = 50;
            }
            var range = [];
            for (var i = 1; i <= item.maxQtyPerOrder; i++) {
                range.push(i);
            }
            return range;
        }
    }
}