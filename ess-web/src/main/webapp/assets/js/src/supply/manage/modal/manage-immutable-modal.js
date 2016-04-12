var essSupply = angular.module('essSupply');

essSupply.directive('manageImmutableModal', ['appProps', 'modals', 'LocationService', 'SupplyAcceptShipmentApi',
    function (appProps, modals, locationService, acceptShipmentApi) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/manage/modal/immutable-modal',
        link: link
    };

    function link($scope, $elem, $attrs) {
        $scope.shipment = modals.params();
        
        $scope.acceptShipment = function() {
            acceptShipmentApi.save({id: $scope.shipment.id}, null,
            function(value, responseHeaders) {
                $scope.close();
                reload();
            },
            function(httpResponse) { // error handler
                console.log("An error occurred: " + httpResponse);
            })
        };

        $scope.close = function() {
            modals.resolve();
        };

        function reload() {
            locationService.go("/supply/manage/manage", true);
        }
    }
}]);