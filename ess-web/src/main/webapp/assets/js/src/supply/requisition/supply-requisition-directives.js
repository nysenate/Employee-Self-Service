var essSupply = angular.module('essSupply');

essSupply.directive('requisitionQuantitySelector', [function() {
    return {
        restrict: 'A',
        link: function(scope, element, attributes) {
            function aboveWarnQuantity(quantity, product) {
                return typeof quantity !== 'undefined' && quantity >= product.warnQuantity;
            }
            element.on('mouseover', function(event) { // TODO: better event than mouseover?
                $(this).children().each(function() {
                    var qty = $(this).val().split(':')[1]; // TODO: hack
                    if (aboveWarnQuantity(qty, scope.product)) {
                        $(this).addClass("warn-option");
                    }
                });
            });
            element.on('change', function(event) {
                var qty = $(this).val().split(':')[1]; // TODO: hack
                if (aboveWarnQuantity(qty, scope.product)) {
                    $(this).addClass("warn-select");
                }
                else {
                    $(this).removeClass("warn-select");
                }
            });
        }
    }
}]);