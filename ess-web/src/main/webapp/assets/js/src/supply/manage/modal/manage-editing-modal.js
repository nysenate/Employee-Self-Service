var essSupply = angular.module('essSupply');

essSupply.directive('manageEditingModal', ['appProps', 'modals', 'SupplyProcessShipmentApi', 'SupplyCompleteShipmentApi',
    'SupplyRejectOrderApi', 'SupplyCancelShipmentApi', 'SupplyUpdateLineItemsApi', 'SupplyApproveShipmentApi',
    'SupplyAddNoteApi', 'SupplyIssuerApi', 'SupplyUpdateShipmentsApi', 'SupplyUpdateOrderApi', 'LocationApi', 'LocationService',
    function (appProps, modals, processShipmentApi, completeShipmentApi,
              rejectOrderApi, cancelShipmentApi, updateLineItemsApi, approveShipmentApi,
              addNoteApi, issuerApi, updateShipmentsApi, updateOrderApi, locationApi, locationService) {
        return {
            templateUrl: appProps.ctxPath + '/template/supply/manage/modal/editing-modal',
            controller: ['$scope', function($scope) {

                /** Original shipment */
                $scope.shipment = null;
                /** A copy of the shipment to be displayed and edited as the user wishes. */
                $scope.dirtyShipment = null;
                $scope.note = "";
                $scope.dirty = false;
                
                $scope.dirtyLocationCode = null;
                $scope.locationSearch = {
                    matches: [],
                    response: {},
                    codes: [],
                    map: new Map() // Map of location code to location object. Used to link autocomplete values to full objects.
                };

                $scope.init = function () {
                    $scope.shipment = modals.params();
                    $scope.dirtyShipment = angular.copy($scope.shipment);
                    $scope.dirtyLocationCode = $scope.dirtyShipment.order.activeVersion.destination.code;
                    
                    $scope.locationSearch.response = locationApi.get(function(response) {
                        $scope.locationSearch.matches = response.result;
                        angular.forEach($scope.locationSearch.matches, function(loc) {
                            if (loc.locationTypeCode === 'W') {
                                $scope.locationSearch.codes.push(loc.code);
                                $scope.locationSearch.map.set(loc.code, loc);
                            }
                        });
                        $scope.locationSearch.codes.sort(function(a, b){
                            if(a < b) {return -1;}
                            if(a > b) {return 1;}
                            return 0;
                        })
                    }, function(errorResponse) {
                        $scope.locationSearch.matches = [];
                    });
                    
                };

                $scope.init();
                
                function saveShipmentVersion(version) {
                    updateShipmentsApi.save(
                        {id: $scope.shipment.id},
                        version, success, error);
                }
                
                function saveOrderVersion(version) {
                    updateOrderApi.save(
                        {id: $scope.shipment.order.id},
                        version, success, error);
                }
                
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
                    console.log(JSON.stringify($scope.dirtyShipment.order.activeVersion));
                    saveOrderVersion($scope.dirtyShipment.order.activeVersion);
                    saveShipmentVersion($scope.dirtyShipment.activeVersion);
                    // saveOrderUpdates();
                    // saveShipmentUpdates();
                };

                // TODO: clean this up into single api
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
                    var locUpdated = angular.toJson($scope.shipment.order.activeVersion.destination) !== angular.toJson($scope.dirtyShipment.order.activeVersion.destination);
                    if (locUpdated) {
                        // TODO update location via api.
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
                };

                $scope.onNoteUpdated = function() {
                    // $scope.shipment.order.activeVersion.note
                    // $scope.onUPdate();
                };

                /** --- Location Autocomplete --- */

                /**
                 * If a valid location is selected in autocomplete, set that location in dirty shipment.
                 * Otherwise reset the dirty shipment location to the original.
                 */
                $scope.onAutocompleteUpdated = function() {
                    var loc = $scope.locationSearch.map.get($scope.dirtyLocationCode);
                    if(loc) {
                        $scope.dirtyShipment.order.activeVersion.destination = loc;
                    }
                    else {
                        $scope.dirtyShipment.order.activeVersion.destination = $scope.shipment.order.activeVersion.destination;
                    }
                    $scope.onUpdate();
                };

                $scope.locationOption = {
                    options: {
                        html: true,
                        focusOpen: false,
                        onlySelectValid: true,
                        source: function (request, response) {
                            var data = $scope.locationSearch.codes;
                            data = $scope.locationOption.methods.filter(data, request.term);
                            if (!data.length) {
                                data.push({
                                    label: 'Not Found',
                                    value: ''
                                })
                            }
                            response(data);
                        }
                    },
                    methods: {}
                };

            }]
        };
    }]);
