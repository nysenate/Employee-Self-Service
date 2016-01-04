var essSupply = angular.module('essSupply');

essSupply.directive('cartSummary', ['appProps', function(appProps) {
    return {
        restrict: 'E',
        controller: function($scope, SupplyCart) {
            $scope.getTotalItems = function() {
                return SupplyCart.getTotalItems();
            }
        },
        templateUrl: appProps.ctxPath + '/template/supply/order/cart/cart-summary',
        transclude: true
    }
}]);
