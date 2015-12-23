var essSupply = angular.module('essSupply');

essSupply.directive('requisitionQuantitySelector', [function() {
    var getValue = function(el) {
        if (typeof el !== 'undefined') {
            var parts = el.val().split(':');
            if (parts.length > 1) {
                return parts[1];
            }
        }
    };

    return {
        restrict: 'A',
        scope: {
            item: '=item',
            warnQty: '=warnQty'
        },
        link: function(scope, element, attributes) {
            element.on('mouseover', function(event) { // TODO: better event than mouseover?
                $(this).children().each(function() {
                    if (getValue($(this)) >= scope.warnQty) {
                        $(this).addClass("warn-option");
                    }
                });
            });
            element.on('change', function(event) {
                if (getValue($(this)) >= scope.warnQty) {
                    $(this).addClass("warn-select");
                }
                else {
                    $(this).removeClass("warn-select");
                }
            });
        }
    }
}]);

essSupply.directive('editableOrderListing', ['appProps', function(appProps) {
    return {
        restrict: 'A',
        scope: false,
        templateUrl: appProps.ctxPath + '/template/supply/requisition/editable/order/listing',
    }
}]);