essSupply = angular.module('essSupply').controller('SupplyViewController', ['$scope', 'SupplyOrderByIdApi',
    'SupplyInventoryService', 'LocationService', '$window', '$timeout', supplyViewController]);

function supplyViewController($scope, orderByIdApi, inventoryService, locationService, $window, $timeout) {

    $scope.init = function() {
        var id = locationService.getSearchParam('order');

        orderByIdApi.get({id: id}, function(response) {
            $scope.order = response.result;
            console.log($scope.order);
            var print = locationService.getSearchParam('print');
            if (print === 'true') {
                $timeout(function() {
                    $window.print();
                })
            }
        }, function(response) {
            // TODO: handle api error
        });
    };

    $scope.init();
}
