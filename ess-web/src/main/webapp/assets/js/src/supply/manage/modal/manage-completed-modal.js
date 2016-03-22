var essSupply = angular.module('essSupply');

essSupply.directive('manageCompletedModal', ['appProps', 'modals', 'LocationService', 'SupplyUndoCompletionApi',
    function (appProps, modals, locationService, undoCompletionApi) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/manage/modal/completed-modal',
        link: link
    };

    function link($scope, $elem, $attrs) {
        $scope.shipment = modals.params();

        $scope.undo = function(shipment) {
            undoCompletionApi.save({id: shipment.id});
            $scope.close();
            reload();
        };

        $scope.close = function() {
            modals.resolve();
        };

        function reload() {
            locationService.go("/supply/manage/manage", true);
        }
    }
}]);