var essApp = angular.module('ess');

essApp.directive('managePendingModal', ['appProps', 'modals', 'SupplyProcessOrderApi', 'SupplyUpdateOrderItemsApi', 'LocationService',
    function (appProps, modals, processOrderApi, updateOrderItemsApi, locationService) {
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

        $scope.init = function() {
            // sort items by their itemId for consistency.
            $scope.dirtyOrder.items.sort(function(a, b) {return a.itemId - b.itemId});
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

        $scope.removeLineItem = function(lineItem) {
            angular.forEach($scope.dirtyOrder.items, function(dirtyItem) {
                if (lineItem.itemId === dirtyItem.itemId) {
                    $scope.dirtyOrder.items.splice($scope.dirtyOrder.items.indexOf(lineItem), 1);
                    $scope.setDirty();
                }
            });
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