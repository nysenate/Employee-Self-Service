var essApp = angular.module('ess');

essApp.directive('manageProcessingModal', ['appProps', 'modals', function (appProps, modals) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/requisition/manage/processing/modal',
        link: link
    };

    function link($scope, $elem, $attrs) {
        $scope.order = modals.params();

        // TODO: this is temp until server has functionality to get supply employees.
        $scope.assignedTo = $scope.order.issuingEmployee.lastName;
        $scope.supplyEmployees = ["Caseiras", "Maloy", "Heitner", "Johnson", "Smith"];
    }
}]);