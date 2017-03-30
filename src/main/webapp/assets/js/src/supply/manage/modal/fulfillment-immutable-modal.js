var essSupply = angular.module('essSupply');

essSupply.directive('fulfillmentImmutableModal', ['appProps', 'modals', 'LocationService',
    function (appProps, modals, locationService) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/manage/modal/fulfillment-immutable-modal',
        link: link
    };

    function link($scope, $elem, $attrs) {
        $scope.requisition = modals.params();
        
        $scope.close = function() {
            modals.resolve();
        };

        function reload() {
            locationService.go("/supply/manage/fulfillment", true);
        }
    }
}]);