var essSupply = angular.module('essSupply');

essSupply.directive('manageCompletedModal', ['appProps', 'modals', function (appProps, modals) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/requisition/manage/completed/modal',
        link: link
    };

    function link($scope, $elem, $attrs) {
        $scope.order = modals.params();
    }
}]);