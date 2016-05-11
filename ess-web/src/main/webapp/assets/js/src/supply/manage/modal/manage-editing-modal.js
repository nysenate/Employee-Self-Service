var essSupply = angular.module('essSupply')
    .directive('manageEditingModal', ['appProps', function (appProps) {
        return {
            templateUrl: appProps.ctxPath + '/template/supply/manage/modal/editing-modal',
            scope: {
                'supplyEmployees': '=',
                'supplyItems': '='
            },
            controller: 'ManageEditingModal',
            controllerAs: 'ctrl'
        };
    }])

    .controller('ManageEditingModal', ['$scope', 'appProps', 'modals', 'SupplyUpdateShipmentsApi',
        'SupplyUpdateOrderApi', 'SupplyLocationAutocompleteService', 'LocationService',
        function ($scope, appProps, modals, updateShipmentsApi, updateOrderApi, locationAutocompleteService, locationService) {
            /** Original shipment */
            $scope.shipment = {};
            $scope.displayOrderVersion = {};
            $scope.displayShipmentVersion = {};
            $scope.dirty = false;
            /** Initializes the location autocomplete field. */
            $scope.dirtyLocationCode = "";
            $scope.locationSearch = {
                map: new Map() // Map of location code to location object. Used to link autocomplete values to full objects.
            };
            $scope.addItemFeature = {
                newItemCommodityCode: "",
                items: [],
                commodityCodes: [],
                commodityCodesToItem: new Map()
            };


            $scope.init = function () {
                $scope.shipment = modals.params();
                $scope.shipment.order.activeVersion.note = ""; // Reset note so new note can be added.
                $scope.displayOrderVersion = angular.copy($scope.shipment.order.activeVersion);
                $scope.displayShipmentVersion = angular.copy($scope.shipment.activeVersion);
                $scope.dirtyLocationCode = $scope.displayOrderVersion.destination.code;

                $scope.locationSearch.map = locationAutocompleteService.getCodeToLocationMap();
                $scope.locationAutocompleteOptions = locationAutocompleteService.getLocationAutocompleteOptions();

                initializeAddItemFeature();
            };

            function initializeAddItemFeature() {
                $scope.addItemFeature.items = $scope.supplyItems;
                angular.forEach($scope.addItemFeature.items, function (item) {
                    $scope.addItemFeature.commodityCodes.push(item.commodityCode);
                    $scope.addItemFeature.commodityCodesToItem.set(item.commodityCode, item);
                })
            }

            $scope.init();

            $scope.saveChanges = function () {
                saveShipmentVersion($scope.displayShipmentVersion);
                saveOrderVersion($scope.displayOrderVersion);
            };

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

            var success = function success(value, responseHeaders) {
                $scope.closeModal();
                reload();
            };

            var error = function error(httpResponse) {
                $scope.closeModal();
                console.log("An error occurred: " + JSON.stringify(httpResponse));
                // TODO
            };

            $scope.processOrder = function () {
                $scope.displayShipmentVersion.status = 'PROCESSING';
                if ($scope.displayShipmentVersion.issuer === null) {
                    setIssuerToLoggedInUser();
                }
                $scope.saveChanges();
            };

            function setIssuerToLoggedInUser() {
                angular.forEach($scope.supplyEmployees, function (emp) {
                    if (emp.employeeId === appProps.user.employeeId) {
                        $scope.displayShipmentVersion.issuer = emp
                    }
                })
            }

            $scope.completeOrder = function () {
                $scope.displayShipmentVersion.status = 'COMPLETED';
                $scope.saveChanges();
            };

            $scope.approveShipment = function () {
                $scope.displayShipmentVersion.status = 'APPROVED';
                $scope.saveChanges();
            };

            $scope.rejectOrder = function () {
                $scope.displayOrderVersion.status = 'REJECTED';
                $scope.displayShipmentVersion.status = 'CANCELED';
                $scope.saveChanges();
            };

            $scope.onUpdate = function () {
                $scope.dirty = changesMadeToShipment() || changesMadeToOrder()
            };

            function changesMadeToShipment() {
                return angular.toJson($scope.shipment.activeVersion) !== angular.toJson($scope.displayShipmentVersion);
            }

            function changesMadeToOrder() {
                return angular.toJson($scope.shipment.order.activeVersion) !== angular.toJson($scope.displayOrderVersion);
            }

            $scope.closeModal = function () {
                modals.resolve();
            };

            function reload() {
                locationService.go("/supply/manage/manage", true);
            }

            /** --- Location Autocomplete --- */

            /**
             * If a valid location is selected in autocomplete, set display shipment
             * version to equal that, otherwise reset to the original location.
             */
            $scope.onLocationUpdated = function () {
                var loc = $scope.locationSearch.map.get($scope.dirtyLocationCode);
                if (loc) {
                    $scope.displayOrderVersion.destination = loc;
                }
                else {
                    $scope.displayOrderVersion.destination = $scope.shipment.order.activeVersion.destination;
                }
                $scope.onUpdate();
            };

            /** --- Add Item --- **/

            $scope.addItem = function () {
                var newItem = $scope.addItemFeature.commodityCodesToItem.get($scope.addItemFeature.newItemCommodityCode);
                if (!newItem || newItemIsDuplicate(newItem)) {
                    // Trying to add invalid item, don't do anything.
                    return;
                }
                $scope.displayOrderVersion.lineItems.push({item: newItem, quantity: 1});
                $scope.onUpdate();
            };

            function newItemIsDuplicate(newItem) {
                var duplicateItem = false;
                angular.forEach($scope.shipment.order.activeVersion.lineItems, function (lineItem) {
                    if (newItem.id === lineItem.item.id) {
                        duplicateItem = true;
                    }
                });
                return duplicateItem;
            }

            $scope.addItemAutocompleteOptions = {
                options: {
                    html: true,
                    focusOpen: false,
                    onlySelectValid: true,
                    outHeight: 50,
                    source: function (request, response) {
                        var data = $scope.addItemFeature.commodityCodes;
                        data = $scope.addItemAutocompleteOptions.methods.filter(data, request.term);
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

        }]);
