var essSupply = angular.module('essSupply');

essSupply.directive('cartSummary', ['appProps', function(appProps) {
    return {
        restrict: 'E',
        controller: function($scope, SupplyCartService) {
            $scope.getTotalItems = function() {
                return SupplyCartService.getTotalItems();
            }
        },
        templateUrl: appProps.ctxPath + '/template/supply/order/cart/cart-summary',
        transclude: true
    }
}]);
