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

    return {
        queryLocationAllowance: function (location) {
            return allowanceApi.get({id: location.locId}, function (response) {
                allowances = response.result.itemAllowances;
            }).$promise;
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