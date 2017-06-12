var essSupply = angular.module('essSupply');

essSupply.directive('cartSummary', ['appProps', function(appProps) {
    return {
        restrict: 'E',
        controller: function($scope, SupplyCartService) {
            $scope.getSize = function() {
                return SupplyCartService.getSize();
            }
        },
        templateUrl: appProps.ctxPath + '/template/supply/shopping/cart/cart-summary',
        transclude: true
    }
}]);
