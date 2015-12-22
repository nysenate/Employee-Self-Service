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

essSupply.directive('editableOrderListing', ['appProps', 'modals', function(appProps, modals) {
    return {
        restrict: 'A',
        templateUrl: appProps.ctxPath + '/template/supply/requisition/editable/order/listing',
        link: function($scope, $element, $attrs) {

            /** Original order */
            $scope.order = modals.params();

            /** Order containing any user edits */
            $scope.dirtyOrder = angular.copy($scope.order);

            $scope.init = function() {
                // sort items by their itemId for consistency.
                $scope.dirtyOrder.items.sort(function(a, b) {return a.itemId - b.itemId});
            };

            $scope.init();

            $scope.removeLineItem = function(lineItem) {
                angular.forEach($scope.dirtyOrder.items, function (dirtyItem) {
                    if (lineItem.itemId === dirtyItem.itemId) {
                        $scope.dirtyOrder.items.splice($scope.dirtyOrder.items.indexOf(lineItem), 1);
                        $scope.setDirty();
                    }
                });
            };
        }
    }
}]);