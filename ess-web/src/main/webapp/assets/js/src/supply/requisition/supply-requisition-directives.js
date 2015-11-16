var essSupply = angular.module('essSupply');

essSupply.directive('requisitionQuantitySelector', ['SupplyInventoryService', function(SupplyInventoryService) {
    return {
        restrict: 'A',
        scope: {
            product: '=product'
        },
        // TODO: refactor alll of this mess.
        link: function(scope, element, attributes) {
            element.on('mouseover', function(event) { // TODO: better event than mouseover?
                $(this).children().each(function() {
                    var qty = $(this).val().split(':')[1]; // TODO: hack
                    if (SupplyInventoryService.isWarningQuantity(scope.product, qty)) {
                        $(this).addClass("warn-option");
                    }
                });
            });
            element.on('change', function(event) {
                var qty = $(this).val().split(':')[1]; // TODO: hack
                if (SupplyInventoryService.isWarningQuantity(scope.product, qty)) {
                    $(this).addClass("warn-select");
                }
                else {
                    $(this).removeClass("warn-select");
                }
            });
        }
    }
}]);
