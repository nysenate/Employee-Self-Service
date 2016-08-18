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
essSupply.directive('capitalize', function ($parse) {
        return {
            require: 'ngModel',
            link: function (scope, element, attrs, modelCtrl) {
                var capitalize = function (inputValue) {
                    if (inputValue === undefined) {
                        inputValue = "";
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