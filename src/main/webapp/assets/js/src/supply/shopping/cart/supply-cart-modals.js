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
}]).controller('CartCheckoutModalCtrl', ['$scope', 'modals', 'LocationService', function ($scope, modals, locationService) {

    $scope.requisitionId = modals.params().result.requisitionId;

    $scope.returnToSupply = function () {
        modals.resolve();
        locationService.go("/supply/shopping/order", false);
    };

    $scope.logout = locationService.logout;
}]);

/**
 *  --- Delivery method modal ---
 *
 *  Returns the DeliveryMethod chosen by the user.
 */
essSupply.directive('deliveryMethodModal', ['appProps', function (appProps) {
    return {
        restrict: 'E',
        templateUrl: appProps.ctxPath + '/template/supply/shopping/cart/delivery-method-modal',
        controller: 'DeliveryMethodModalCtrl',
        controllerAs: 'ctrl'
    }
}]).controller('DeliveryMethodModalCtrl', ['$scope', 'modals', function ($scope, modals) {

    $scope.deliver = function () {
        modals.resolve("DELIVERY");
    };

    $scope.pickup = function () {
        modals.resolve("PICKUP");
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
