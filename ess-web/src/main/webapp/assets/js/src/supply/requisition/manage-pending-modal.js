var essApp = angular.module('ess');

essApp.directive('managePendingModal', ['appProps', 'modals', 'SupplyProcessOrderApi', 'LocationService',
    function (appProps, modals, processOrderApi, locationService) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/requisition/manage/pending/modal',
        link: link
    };

    function link($scope, $elem, $attrs) {

        /** Original order */
        $scope.order = modals.params();

        /** Order containing any user edits */
        $scope.dirtyOrder = angular.copy($scope.order);

        $scope.dirty = false;

        $scope.processOrder = function(order) {
            order.issuingEmployee.employeeId = appProps.user.employeeId;
            processOrderApi.save(order);
            modals.resolve();
            reload();
        };

        /** Save the changes made to dirtyOrder */
        $scope.saveOrder = function() {
            console.log("save");
            modals.resolve();
            reload();
        };

        $scope.setDirty = function() {
            $scope.dirty = true;
        };

        function reload() {
            locationService.go("/supply/requisition/manage", true);
        }
    }
}]);