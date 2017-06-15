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

        alphabetizeItemsByCommodityCode: function (items) {
            items.sort(function (a, b) {
                if (a.commodityCode < b.commodityCode) return -1;
                if (a.commodityCode > b.commodityCode) return 1;
                return 0;
            });
            return items;
        },

        /**
         * Put an array of strings in alphabetical order.
         * @param strings
         */
        alphabetizeByName: function (names) {
            names.sort(function (a, b) {
                if (a < b) return -1;
                if (a > b) return 1;
                return 0;
            });
            return names;
        },

        countDistinctItemsInRequisition: function (requisition) {
            var count = 0;
            if (requisition.lineItems) {
                angular.forEach(requisition.lineItems, function (item) {
                    count++;
                });
            }
            return count;
        },

        /**
         * Returns true if any items in a requisition are over the per order allowance.
         */
        containsItemOverOrderMax: function (requisition) {
            var overOrderMax = false;
            angular.forEach(requisition.lineItems, function (lineItem) {
                if (lineItem.quantity > lineItem.item.perOrderAllowance) {
                    overOrderMax = true;
                }
            });
            return overOrderMax;
        },

        /**
         * Returns true if any item in the requisition is a special item.
         */
        containsSpecialItem: function (requisition) {
            var containsSpecialItems = false;
            angular.forEach(requisition.lineItems, function (lineItem) {
                if (lineItem.item.specialRequest) {
                    containsSpecialItems = true;
                }
            });
            return containsSpecialItems;
        }
    }
}]);

essSupply.directive('capitalize', function ($parse) {
        return {
            require: 'ngModel',
            link: function (scope, element, attrs, modelCtrl) {
                var capitalize = function (inputValue) {
                    if (inputValue === undefined) {
                        inputValue = "";
                    }
                    if(inputValue.indexOf("(") != -1){
                        inputValue = inputValue.substring(0,inputValue.indexOf("(")-1);
                        modelCtrl.$setViewValue(inputValue);
                    }
                    var capitalized = inputValue.toUpperCase();
                    if (capitalized !== inputValue) {
                        modelCtrl.$setViewValue(capitalized);
                        modelCtrl.$render();
                    }
                    return capitalized;
                };
                modelCtrl.$parsers.push(capitalize);
                capitalize($parse(attrs.ngModel)(scope)); // capitalize initial value
            }
        }
    }
);