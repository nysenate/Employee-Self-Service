var essSupply = angular.module('essSupply');

essSupply.directive('manageEditingModal', ['appProps', 'modals', 'SupplyProcessShipmentApi', 'SupplyCompleteShipmentApi',
    'SupplyRejectOrderApi', 'SupplyCancelShipmentApi', 'SupplyUpdateLineItemsApi', 'SupplyApproveShipmentApi', 
    'SupplyAddNoteApi', 'LocationService',
    function (appProps, modals, processShipmentApi, completeShipmentApi,
              rejectOrderApi, cancelShipmentApi, updateLineItemsApi, approveShipmentApi,
              addNoteApi, locationService) {
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
            /** A copy of the shipment to be displayed and edited as the user wishes. */
            $scope.dirtyShipment = null;
            /** Models the displayed note field which allows comments on the order or any edits supply staff makes.
             * Don't display the shipment.order.activeVersion.note because that note is related to old changes.*/
            $scope.note = "";
            /** dirty == true if any data has been edited. */
            $scope.dirty = false;
            
            $scope.init = function() {
                $scope.shipment = modals.params();
                $scope.dirtyShipment = angular.copy($scope.shipment);
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

            /**
             * Save the changes made to dirtyShipment.
             * Call different API depending on what has been updated.
             */
            $scope.saveOrder = function() {
                var areItemsUpdated = angular.toJson($scope.shipment.order.activeVersion.lineItems) !== angular.toJson($scope.dirtyShipment.order.activeVersion.lineItems);
                if(areItemsUpdated) {
                    updateLineItemsApi.save(
                        {id: $scope.shipment.order.id},
                        {lineItems: $scope.dirtyShipment.order.activeVersion.lineItems,
                            note: $scope.note === "" ? null : $scope.note},
                        success,
                        error)
                }
                else {
                    var isNoteUpdated = $scope.note !== "";
                    if (isNoteUpdated) {
                        addNoteApi.save(
                            {id: $scope.shipment.order.id},
                            $scope.note,
                            success,
                            error)
                    }
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

            // $scope.noteUpdated = function() {
            //     var noteChanged = _.isEqual($scope.shipment.order.activeVersion.note, $scope.dirtyShipment.order.activeVersion.note);
            // };

            /**
             * When the user updates data, check the original and dirty shipment to see if there are changes.
             * Set $scope.dirty to true if there are changes, otherwise false.
             *
             * Angular adds a hashkey key to objects for tracking changes.
             * So most traditional ways of doing deep euality checks will not work.
             * We use angular.toJson here because it automatically strips out the hashkey value.
             */
            $scope.onUpdate = function() {
                $scope.dirty = angular.toJson($scope.shipment) !== angular.toJson($scope.dirtyShipment) || $scope.note !== "";
                console.log($scope.dirty);
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