var essSupply = angular.module('essSupply');

essSupply.directive('manageEditingModal', ['appProps', 'modals', 'SupplyProcessShipmentApi', 'SupplyCompleteShipmentApi',
    'SupplyRejectOrderApi', 'SupplyCancelShipmentApi', 'SupplyUpdateLineItemsApi', 'SupplyApproveShipmentApi', 'LocationService',
    function (appProps, modals, processShipmentApi, completeShipmentApi,
              rejectOrderApi, cancelShipmentApi, updateLineItemsApi, approveShipmentApi, locationService) {
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
            /** Status of shipment, either 'PENDING', 'PROCESSING' or 'COMPLETED'*/
            $scope.status = null;
            $scope.dirty = false;

            $scope.init = function() {
                $scope.shipment = modals.params();
                $scope.dirtyShipment = angular.copy($scope.shipment);
                $scope.status = $scope.shipment.activeVersion.status;
                // Consistently sort items.
                // $scope.dirtyShipment.items.sort(function(a, b) {return a.itemId - b.itemId});
            };

            $scope.init();

            /** Save any changes, then process shipment */
            $scope.processOrder = function() {
                processShipmentApi.save(
                    {id: $scope.shipment.id},
                    appProps.user.employeeId, // TODO set this to selected issuer
                    success,
                    error
                );
            };

            /** Save any changes, then complete shipment */
            $scope.completeOrder = function() {
                completeShipmentApi.save(
                    {id: $scope.shipment.id},
                    null,
                    success,
                    error);
            };

            /** Save the changes made to dirtyShipment */
            $scope.saveOrder = function() {
                var itemsUpdated = _.isEqual($scope.shipment.order.activeVersion.lineItems, $scope.dirtyShipment.order.activeVersion.lineItems);
                if(!itemsUpdated) {
                    updateLineItemsApi.save(
                        {id: $scope.shipment.order.id},
                        {lineItems: $scope.dirtyShipment.order.activeVersion.lineItems},
                        success,
                        error)
                }
            };

            $scope.rejectOrder = function() {
                rejectOrderApi.save({id: $scope.shipment.order.id});
                // Also cancel the shipment since the order has been rejected.
                cancelShipmentApi.save(
                    {id: $scope.shipment.id},
                    null,
                    success,
                    error);
            };
            
            $scope.approveShipment = function() {
                approveShipmentApi.save(
                    {id: $scope.shipment.id},
                    null,
                    success,
                    error);
            };


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
            
            var success = function success(value, responseHeaders) {
                $scope.close();
                reload();
            };
            
            var error = function error(httpResponse) {
                $scope.close();
                console.log("An error occurred: " + httpResponse);
                // TODO
            }
        }
    }]);