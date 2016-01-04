var essSupply = angular.module('essSupply');

essSupply.directive('manageEditingModal', ['appProps', 'modals', 'SupplyProcessOrderApi', 'SupplyCompleteOrderApi',
    'SupplySaveOrderApi', 'SupplyRejectOrderApi', 'LocationService',
    function (appProps, modals, processOrderApi, completeOrderApi, saveOrderApi, rejectOrderApi, locationService) {
        return {
            templateUrl: appProps.ctxPath + '/template/supply/manage/modal/editing-modal',
            link: link
        };

        function link($scope, $elem, $attrs) {

            // TODO: temporary until implemented in server. supplyEmployees should be array of EmployeeView objects.
            $scope.assignedTo = "Caseiras";
            $scope.supplyEmployees = ["Caseiras", "Smith", "Johnson", "Maloy", "Richard"];

            /** Original order */
            $scope.order = modals.params();
            /** Order containing any user edits */
            $scope.dirtyOrder = angular.copy($scope.order);
            /** Status of order, either 'PENDING' or 'PROCESSING'*/
            $scope.status = null;
            $scope.dirty = false;

            $scope.init = function() {
                $scope.status = modals.params().status;
                // Consistently sort items.
                $scope.dirtyOrder.items.sort(function(a, b) {return a.itemId - b.itemId});
            };

            $scope.init();

            /** Save any changes, then process order */
            $scope.processOrder = function() {
                var process = function() {
                    $scope.dirtyOrder.issuingEmployee.employeeId = appProps.user.employeeId;
                    processOrderApi.save($scope.dirtyOrder);
                    $scope.close();
                    reload();
                };

                if($scope.dirty) {
                    saveOrderApi.save($scope.dirtyOrder, function(successRes) {
                        process();
                    })
                }
                else {
                    process();
                }
            };

            /** Save any changes, then complete order */
            $scope.completeOrder = function() {
                var complete = function() {
                    completeOrderApi.save($scope.dirtyOrder);
                    $scope.close();
                    reload();
                };

                if($scope.dirty) {
                    saveOrderApi.save($scope.dirtyOrder, function(successRes) {
                        complete();
                    })
                }
                else {
                    complete();
                }
            };

            /** Save the changes made to dirtyOrder */
            $scope.saveOrder = function() {
                saveOrderApi.save($scope.dirtyOrder);
                $scope.close();
                reload();
            };

            $scope.rejectOrder = function(order) {
                rejectOrderApi.save(order);
                $scope.close();
                reload();
            };

            $scope.removeLineItem = function(lineItem) {
                angular.forEach($scope.dirtyOrder.items, function (dirtyItem) {
                    if (lineItem.itemId === dirtyItem.itemId) {
                        $scope.dirtyOrder.items.splice($scope.dirtyOrder.items.indexOf(lineItem), 1);
                        $scope.setDirty();
                    }
                });
            };

            // TODO: cant save this until we get full EmployeeView objects from server.
            $scope.setIssuedBy = function() {
                // set $scope.dirtyOrder.issuingEmployee = $scope.assignedTo
                $scope.setDirty();
            };

            $scope.setDirty = function() {
                $scope.dirty = true;
            };

            $scope.close = function() {
                modals.resolve();
            };

            function reload() {
                locationService.go("/supply/manage/manage", true);
            }
        }
    }]);