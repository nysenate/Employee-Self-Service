essSupply = angular.module('essSupply').controller('SupplyViewController', ['$scope', 'SupplyOrderByIdApi',
    'SupplyInventoryService', 'LocationService', '$window', '$timeout', supplyViewController]);

function supplyViewController($scope, orderByIdApi, inventoryService, locationService, $window, $timeout) {

    $scope.getItemCommodityCode = function(itemId) {
        var item = inventoryService.getItemById(itemId);
        return item.commodityCode;
    };

    $scope.getItemName = function(itemId) {
        var item = inventoryService.getItemById(itemId);
        return item.name;
    };

    $scope.init = function() {
        var id = locationService.getSearchParam('order');

        orderByIdApi.get({id: id}, function(response) {
            $scope.order = response.result;
            var print = locationService.getSearchParam('print');
            if (print === 'true') {
                $timeout(function() {
                    $window.print();
                })
            }
        }, function(response) {

        });
    };

    $scope.init();
}