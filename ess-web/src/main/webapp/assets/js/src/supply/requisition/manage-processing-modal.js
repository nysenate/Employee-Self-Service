var essApp = angular.module('ess');

essApp.directive('manageProcessingModal', ['appProps', 'modals', function (appProps, modals) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/requisition/manage/processing/modal',
        link: link
    };

    function link($scope, $elem, $attrs) {
        $scope.order = modals.params();
    }
}]);