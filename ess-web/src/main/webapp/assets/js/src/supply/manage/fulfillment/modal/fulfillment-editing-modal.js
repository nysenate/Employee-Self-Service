var essSupply = angular.module('essSupply')
    .directive('fulfillmentEditingModal', ['appProps', function (appProps) {
        return {
            templateUrl: appProps.ctxPath + '/template/supply/manage/fulfillment/modal/fulfillment-editing-modal',
            scope: {
                'supplyEmployees': '=',
                'supplyItems': '='
            },
            controller: 'FulfillmentEditingModal',
            controllerAs: 'ctrl'
        };
    }])

    .controller('FulfillmentEditingModal', ['$scope', 'appProps', 'modals', 'SupplyRequisitionByIdApi', 'SupplyLocationAutocompleteService', 'LocationService',
        function ($scope, appProps, modals, requisitionApi, locationAutocompleteService, locationService) {
            /** Original shipment */
            $scope.shipment = {};
            $scope.displayedVersion = {};
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
                $scope.shipment.activeVersion.note = ""; // Reset note so new note can be added.
                $scope.displayedVersion = angular.copy($scope.shipment.activeVersion);
                $scope.dirtyLocationCode = $scope.displayedVersion.destination.code;
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
                saveRequisition($scope.displayedVersion);
            };

            function saveRequisition(version) {
                requisitionApi.save(
                    {id: $scope.shipment.id},
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
                $scope.displayedVersion.status = 'PROCESSING';
                if ($scope.displayedVersion.issuer === null) {
                    setIssuerToLoggedInUser();
                }
                $scope.saveChanges();
            };

            function setIssuerToLoggedInUser() {
                angular.forEach($scope.supplyEmployees, function (emp) {
                    if (emp.employeeId === appProps.user.employeeId) {
                        $scope.displayedVersion.issuer = emp
                    }
                })
            }

            $scope.completeOrder = function () {
                $scope.displayedVersion.status = 'COMPLETED';
                $scope.saveChanges();
            };

            $scope.approveShipment = function () {
                $scope.displayedVersion.status = 'APPROVED';
                $scope.saveChanges();
            };

            $scope.rejectOrder = function () {
                $scope.displayedVersion.status = 'REJECTED';
                $scope.saveChanges();
            };

            $scope.onUpdate = function () {
                $scope.dirty = angular.toJson($scope.shipment.activeVersion) !== angular.toJson($scope.displayedVersion);
            };

            $scope.closeModal = function () {
                modals.resolve();
            };

            function reload() {
                locationService.go("/supply/manage/fulfillment", true);
            }

            /** --- Location Autocomplete --- */

            /**
             * If a valid location is selected in autocomplete, update location. If selection is invalid,
             * use original location.
             */
            $scope.onLocationUpdated = function () {
                var loc = $scope.locationSearch.map.get($scope.dirtyLocationCode);
                if (loc) {
                    $scope.displayedVersion.destination = loc;
                }
                else {
                    $scope.displayedVersion.destination = $scope.shipment.activeVersion.destination;
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
                $scope.displayedVersion.lineItems.push({item: newItem, quantity: 1});
                $scope.onUpdate();
            };

            function newItemIsDuplicate(newItem) {
                var duplicateItem = false;
                angular.forEach($scope.shipment.activeVersion.lineItems, function (lineItem) {
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

        }])

    .directive('editableOrderListing', ['appProps', function (appProps) {
        return {
            restrict: 'A',
            scope: false,
            templateUrl: appProps.ctxPath + '/template/supply/manage/fulfillment/modal/editable-order-listing'
        }
    }]);
