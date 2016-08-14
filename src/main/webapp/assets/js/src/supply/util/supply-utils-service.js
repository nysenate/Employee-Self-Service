essSupply = angular.module('essSupply');
essSupply.service('SupplyUtils', [function () {
    return {
        alphabetizeLineItems: function (lineItems) {
            lineItems.sort(function (a, b) {
                if (a.item.description < b.item.description) return -1;
                if (a.item.description > b.item.description) return 1;
                return 0;
            });
            return lineItems;
        },
        
        alphabetizeAllowances: function (allowances) {
            var allowancesCopy = angular.copy(allowances);
            allowancesCopy.sort(function (a, b) {
                if (a.item.description < b.item.description) return -1;
                if (a.item.description > b.item.description) return 1;
                return 0;
            });
            return allowancesCopy;
        }
    }
}]);