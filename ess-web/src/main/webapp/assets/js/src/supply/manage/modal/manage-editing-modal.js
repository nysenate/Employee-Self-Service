var essSupply = angular.module('essSupply');

essSupply.directive('manageEditingModal', ['appProps', 'modals', 'SupplyProcessShipmentApi', 'SupplyCompleteShipmentApi',
    'SupplySaveOrderApi', 'SupplyRejectOrderApi', 'LocationService',
    function (appProps, modals, processShipmentApi, completeShipmentApi, saveOrderApi, rejectOrderApi, locationService) {
        return {
            templateUrl: appProps.ctxPath + '/template/supply/manage/modal/editing-modal',
            link: link
        };

        function link($scope, $elem, $attrs) {

            // TODO: temporary until implemented in server. supplyEmployees should be array of EmployeeView objects.
            $scope.assignedTo = "Caseiras";
            $scope.supplyEmployees = ["Caseiras", "Smith", "Johnson", "Maloy", "Richard"];

            /** Original shipment */
            $scope.shipment = null;
            /** Shipment containing any user edits */
            $scope.dirtyShipment = null;
            /** Status of shipment, either 'PENDING' or 'PROCESSING'*/
            $scope.status = null;
            $scope.dirty = false;

            $scope.init = function() {
                $scope.shipment = modals.params();
                $scope.dirtyShipment = angular.copy($scope.shipment);
                $scope.status = $scope.shipment.activeVersion.status;
                // Consistently sort items. TODO why?
                // $scope.dirtyShipment.items.sort(function(a, b) {return a.itemId - b.itemId});
            };

            $scope.init();

            /** Save any changes, then process shipment */
            $scope.processOrder = function() {
                // $scope.dirtyShipment.activeVersion.issuingEmployee.employeeId = appProps.user.employeeId;
                // TODO why is scope.dirtyShipment needed in api call below!!!!! &&&&&&&&&&&&&&&&&&&&&&&& TOMORROW!!!!
                processShipmentApi.save({id: $scope.shipment.id}, appProps.user.employeeId);
                $scope.close();
                reload();
            };

            /** Save any changes, then complete shipment */
            $scope.completeOrder = function() {
                completeShipmentApi.save({id: $scope.shipment.id});
                $scope.close();
                reload();
            };

            /** Save the changes made to dirtyShipment */
            $scope.saveOrder = function() {
                // TODO: check if issuer and/or line items updated. make appropriate api calls.
                // saveOrderApi.save($scope.dirtyShipment);
                // $scope.close();
                // reload();
            };

            $scope.rejectOrder = function(shipment) {
                rejectOrderApi.save(shipment);
                $scope.close();
                reload();
            };

            //$scope.removeLineItem = function(lineItem) {
            //    angular.forEach($scope.dirtyShipment.items, function (dirtyItem) {
            //        if (lineItem.itemId === dirtyItem.itemId) {
            //            $scope.dirtyShipment.items.splice($scope.dirtyShipment.items.indexOf(lineItem), 1);
            //            $scope.setDirty();
            //        }
            //    });
            //};

            // TODO: cant save this until we get full EmployeeView objects from server.
            $scope.setIssuedBy = function() {
                // set $scope.dirtyShipment.issuingEmployee = $scope.assignedTo
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