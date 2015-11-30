essSupply = angular.module('essSupply').controller('SupplyViewController', ['$scope', 'SupplyOrderService',
    'LocationService', '$window', '$timeout', supplyViewController]);

function supplyViewController($scope, supplyOrderService, locationService, $window, $timeout) {

    $scope.init = function() {
        var id = locationService.getSearchParam('order');
        $scope.order = supplyOrderService.getOrderById(id);

        var print = locationService.getSearchParam('print');
        if (print === 'true') {
            $timeout(function() {
                $window.print();
            })
        }
    };

    $scope.init();
}