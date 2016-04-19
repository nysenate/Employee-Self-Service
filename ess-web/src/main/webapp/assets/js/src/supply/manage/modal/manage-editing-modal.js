var essSupply = angular.module('essSupply');

essSupply.directive('manageEditingModal', ['appProps', 'modals', 'SupplyProcessShipmentApi', 'SupplyCompleteShipmentApi',
    'SupplyRejectOrderApi', 'SupplyCancelShipmentApi', 'SupplyUpdateLineItemsApi', 'SupplyApproveShipmentApi',
    'SupplyAddNoteApi', 'SupplyIssuerApi', 'LocationService',
    function (appProps, modals, processShipmentApi, completeShipmentApi,
              rejectOrderApi, cancelShipmentApi, updateLineItemsApi, approveShipmentApi,
              addNoteApi, issuerApi, locationService) {
        return {
            templateUrl: appProps.ctxPath + '/template/supply/manage/modal/editing-modal',
            link: link
        };

        function link($scope, $elem, $attrs) {
            /** Original shipment */
            $scope.shipment = null;
            /** A copy of the shipment to be displayed and edited as the user wishes. */
            $scope.dirtyShipment = null;
            $scope.note = "";
            $scope.dirty = false;

            $scope.init = function () {
                $scope.shipment = modals.params();
                $scope.dirtyShipment = angular.copy($scope.shipment);
            };

            $scope.init();

            /** Save any changes, then process shipment */
            $scope.processOrder = function () {
                var issuerId = $scope.dirtyShipment.activeVersion.issuer === null
                    ? appProps.user.employeeId : $scope.dirtyShipment.activeVersion.issuer.employeeId;

                processShipmentApi.save(
                    {id: $scope.shipment.id},
                    issuerId,
                    success,
                    error
                );
            };

            /** Save any changes, then complete shipment */
            $scope.completeOrder = function () {
                completeShipmentApi.save(
                    {id: $scope.shipment.id},
                    null,
                    success,
                    error);
            };

            $scope.saveOrder = function () {
                saveOrderUpdates();
                saveShipmentUpdates();
            };
            
            function saveOrderUpdates() {
                var areItemsUpdated = angular.toJson($scope.shipment.order.activeVersion.lineItems) !== angular.toJson($scope.dirtyShipment.order.activeVersion.lineItems);
                if (areItemsUpdated) {
                    updateLineItemsApi.save(
                        {id: $scope.shipment.order.id},
                        {
                            lineItems: $scope.dirtyShipment.order.activeVersion.lineItems,
                            note: $scope.note === "" ? null : $scope.note
                        },
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
            }
            
            function saveShipmentUpdates() {
                var isIssuerUpdated = angular.toJson($scope.shipment.activeVersion.issuer) !== angular.toJson($scope.dirtyShipment.activeVersion.issuer);
                if (isIssuerUpdated) {
                    issuerApi.save(
                        {id: $scope.shipment.id},
                        $scope.dirtyShipment.activeVersion.issuer.employeeId,
                        success,
                        error)
                }
            }

            $scope.rejectOrder = function () {
                rejectOrderApi.save({id: $scope.shipment.order.id});
                // Also cancel the shipment since the order has been rejected.
                cancelShipmentApi.save(
                    {id: $scope.shipment.id},
                    null,
                    success,
                    error);
            };

            $scope.approveShipment = function () {
                approveShipmentApi.save(
                    {id: $scope.shipment.id},
                    null,
                    success,
                    error);
            };

            /**
             * When the user updates data, check the original and dirty shipment to see if there are changes.
             * Set $scope.dirty to true if there are changes, otherwise false.
             *
             * Angular adds a hashkey key to objects for tracking changes.
             * So most traditional ways of doing deep equality checks will not work.
             * We use angular.toJson here because it automatically strips out the hashkey value.
             */
            $scope.onUpdate = function () {
                $scope.dirty = angular.toJson($scope.shipment) !== angular.toJson($scope.dirtyShipment) || $scope.note !== "";
            };

            $scope.close = function () {
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
                console.log("An error occurred: " + JSON.stringify(httpResponse));
                // TODO
            }
        }
    }]);
