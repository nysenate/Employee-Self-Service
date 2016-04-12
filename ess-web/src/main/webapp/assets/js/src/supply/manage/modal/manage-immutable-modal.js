var essSupply = angular.module('essSupply');

essSupply.directive('manageImmutableModal', ['appProps', 'modals', 'LocationService', 
    function (appProps, modals, locationService) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/manage/modal/immutable-modal',
        link: link
    };

    function link($scope, $elem, $attrs) {
        $scope.shipment = modals.params();

        $scope.close = function() {
            modals.resolve();
        };

        function reload() {
            locationService.go("/supply/manage/manage", true);
        }
    }
}]);