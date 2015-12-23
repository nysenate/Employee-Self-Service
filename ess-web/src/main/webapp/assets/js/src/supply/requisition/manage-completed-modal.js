var essSupply = angular.module('essSupply');

essSupply.directive('manageCompletedModal', ['appProps', 'modals', 'LocationService',
    function (appProps, modals, locationService) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/requisition/manage/completed/modal',
        link: link
    };

    function link($scope, $elem, $attrs) {
        $scope.order = modals.params();

        $scope.undo = function(order) {
            // TODO: needs to be implemented.
            console.log("undo");
            $scope.close();
            reload();
        };

        $scope.close = function() {
            modals.resolve();
        };

        function reload() {
            locationService.go("/supply/requisition/manage", true);
        }
    }
}]);