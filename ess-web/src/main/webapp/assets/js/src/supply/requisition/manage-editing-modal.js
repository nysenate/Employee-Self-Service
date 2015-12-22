var essApp = angular.module('ess');

essApp.directive('manageEditingModal', ['appProps', 'modals', 'SupplyProcessOrderApi', 'SupplyUpdateOrderItemsApi', 'LocationService',
    function (appProps, modals, processOrderApi, updateOrderItemsApi, locationService) {
    return {
        templateUrl: appProps.ctxPath + '/template/supply/requisition/manage/editing/modal',
        link: link
    };

    function link($scope, $elem, $attrs) {

        // TODO: temporary until implemented in server.
        $scope.assignedTo = "Caseiras";
        $scope.supplyEmployees = ["Caseiras", "Smith", "Johnson", "Maloy", "Richard"];

        /** Status of order, either 'PENDING' or 'PROCESSING'*/
        $scope.status = null;
        $scope.dirty = false;

        $scope.init = function() {
            $scope.status = modals.params().status;
        };

        $scope.init();

        $scope.processOrder = function(order) {
            order.issuingEmployee.employeeId = appProps.user.employeeId;
            processOrderApi.save(order);
            close();
            reload();
        };

        /** Save the changes made to dirtyOrder */
        $scope.saveOrder = function() {
            updateOrderItemsApi.save($scope.dirtyOrder);
            close();
            reload();
        };

       $scope.setDirty = function() {
            $scope.dirty = true;
        };

        function close() {
            modals.resolve();
        }

        function reload() {
            locationService.go("/supply/requisition/manage", true);
        }
    }
}]);