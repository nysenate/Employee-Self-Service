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

/**
 *  --- Cart Checkout Modal ---
 */
essSupply.directive('cartCheckoutModal', ['appProps', function (appProps) {
    return {
        restrict: 'E',
        templateUrl: appProps.ctxPath + '/template/supply/order/cart/cart-checkout-modal',
        controller: 'CartCheckoutModalCtrl',
        controllerAs: 'ctrl'
    }
}]).controller('CartCheckoutModalCtrl', ['$scope', 'modals', function ($scope, modals) {

    $scope.requisitionId = modals.params().result.requisitionId;
    console.log(modals.params());
}]);
