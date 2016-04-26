essSupply = angular.module('essSupply').controller('SupplyViewController', ['$scope', 'SupplyShipmentByIdApi',
    'SupplyInventoryService', 'LocationService', '$window', '$timeout', supplyViewController]);

function supplyViewController($scope, shipmentApi, inventoryService, locationService, $window, $timeout) {

    $scope.order = {};
    
    $scope.init = function() {
        var id = locationService.getSearchParam('shipment');

        shipmentApi.get({id: id}, function(response) {
            $scope.shipment = response.result;
            $scope.order = $scope.shipment.order;
            console.log($scope.order);
            var print = locationService.getSearchParam('print');
            if (print === 'true') {
                $timeout(function() {
                    $window.print();
                })
            }
        }, function(errorResponse) {
            // TODO: handle api error
        });
    };

    $scope.init();
}
