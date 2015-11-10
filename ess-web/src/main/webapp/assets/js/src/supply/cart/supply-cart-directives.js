var essSupply = angular.module('essSupply');

essSupply.directive('cartSummary', ['appProps', function(appProps) {
    return {
        restrict: 'E',
        controller: function($scope, supplyCart) {
            $scope.getTotalItems = function() {
                return supplyCart.getTotalItems();
            }
        },
        templateUrl: appProps.ctxPath + '/template/supply/cart/cart-summary',
        transclude: true
    }
}]);
