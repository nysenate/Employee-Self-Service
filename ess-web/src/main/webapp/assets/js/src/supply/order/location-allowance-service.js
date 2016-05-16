angular.module('essSupply').service('SupplyLocationAllowanceService',
    ['SupplyLocationAllowanceApi', 'SupplyUtils', locationAllowanceService]);

function locationAllowanceService(allowanceApi, supplyUtils) {

    var allowances = undefined;

    return {
        queryLocationAllowance: function (location) {
            return allowanceApi.get({id: location.locId}, function (response) {
                allowances = supplyUtils.alphabetizeAllowances(response.result.itemAllowances);
            }).$promise;
        },

        getFilteredAllowances: function (categories) {
            // If no categories given, return all allowances.
            if (categories.length === 0) {
                return allowances;
            }
            var filteredAllowances = [];
            angular.forEach(allowances, function (allowance) {
                if (categories.indexOf(allowance.item.category.name) !== -1) {
                    filteredAllowances.push(allowance);
                }
            });
            return filteredAllowances;
        },

        getAllowances: function () {
            return angular.copy(allowances);
        },

        getAllowanceByItemId: function (itemId) {
            for (var i = 0; i < allowances.length; i++) {
                if (allowances[i].item.id === itemId) {
                    return allowances[i];
                }
            }
        },

        /**
         * Returns an array with integers from 1 to the per order allowance for an allowance.
         */
        getAllowedQuantities: function (allowance) {
            // TODO: tempoary adjustment of per order allowances since database is inaccurate.
            if (allowance.perOrderAllowance === 0) {
                allowance.perOrderAllowance = 2;
            }
            var range = [];
            for (var i = 1; i <= allowance.perOrderAllowance; i++) {
                range.push(i);
            }
            return range;
        }
    }
}