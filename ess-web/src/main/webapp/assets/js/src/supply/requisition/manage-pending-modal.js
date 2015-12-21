var essApp = angular.module('ess');

essApp.directive('managePendingModal', ['appProps', 'modals', function (appProps, modals) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/requisition/manage/pending/modal',
        link: link
    };

    function link($scope, $elem, $attrs) {
        $scope.order = modals.params();
    }
}]);