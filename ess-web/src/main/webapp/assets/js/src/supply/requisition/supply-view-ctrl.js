essSupply = angular.module('essSupply').controller('SupplyViewController', ['$scope', 'SupplyOrderService',
    'LocationService', supplyViewController]);

function supplyViewController($scope, supplyOrderService, locationService) {

    $scope.init = function() {
        var id = locationService.getSearchParam('order');
        $scope.order = supplyOrderService.getOrderById(id);
    };

    $scope.init();
}