var essSupply = angular.module('essSupply');

essSupply.directive('editableOrderListing', ['appProps', function (appProps) {
    return {
        restrict: 'A',
        scope: false,
        templateUrl: appProps.ctxPath + '/template/supply/manage/modal/editable-order-listing'
    }
}]);