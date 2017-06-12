var essSupply = angular.module('essSupply');

/**
 *  --- Cart Checkout Modal ---
 */
essSupply.directive('cartCheckoutModal', ['appProps', function (appProps) {
    return {
        restrict: 'E',
        templateUrl: appProps.ctxPath + '/template/supply/shopping/cart/cart-checkout-modal',
        controller: 'CartCheckoutModalCtrl',
        controllerAs: 'ctrl'
    }
}]).controller('CartCheckoutModalCtrl', ['$scope', 'modals', function ($scope, modals) {

    $scope.requisitionId = modals.params().result.requisitionId;

    $scope.returnToSupply = function () {
        modals.resolve();
        locationService.go("/supply/shopping/order", false);
    };

    $scope.logout = function () {
        locationService.go('/logout', true);
    };
}]);


/**
 *  --- Empty cart modal ---
 */
essSupply.directive('supplyCartEmptyModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/shopping/cart/cart-empty-modal',
        controller: 'SupplyCartEmptyModal',
        controllerAs: 'ctrl'
    }
}]).controller('SupplyCartEmptyModal', ['$scope', 'modals', 'SupplyOrderDestinationService', 'SupplyCartService', 'LocationService', function ($scope, modals, destinationService, supplyCart, locationService) {

    $scope.confirm = function () {
        modals.resolve();
    };

    $scope.nevermind = function () {
        modals.reject();
    }
}]);
