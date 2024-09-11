var essTravel = angular.module('essTravel');

/**
 * This controller contains function shared by app edit form directives such as purpose-edit-form-directive.js
 */
essTravel.controller('AppEditCtrl', ['$scope', '$timeout', '$q', 'modals', 'AddressCountyService',
                                     'TravelModeOfTransportationApi', 'TravelDraftsApi', appEditCtrl]);

function appEditCtrl($scope, $timeout, $q, modals, countyService, motApi, draftsApi) {

    this.$onInit = function () {
        motApi.get({}, function (response) {
            $scope.methodsOfTravel = [];
            response.result.forEach(function (modeOfTransportation) {
                $scope.methodsOfTravel.push(modeOfTransportation.displayName);
            });
        }, $scope.handleErrorResponse);
    };

    $scope.saveDraft = function (draft) {
        var deferred = $q.defer();
        draftsApi.save({}, draft).$promise
            .then(function (res) {
                modals.open("draft-save-success")
                deferred.resolve(res.result)
            })
            .catch(function (error) {
                modals.open("draft-save-error")
                deferred.reject(error)
            })
        return deferred.promise;
    }

    $scope.openLoadingModal = function () {
        modals.open('loading');
    };

    $scope.closeLoadingModal = function () {
        if (modals.isTop('loading')) {
            modals.resolve();
        }
    };

    // Set all invalid form elements as touched so they can be styled appropriately if they have errors.
    $scope.setInvalidFormElementsTouched = function (form) {
        angular.forEach(form.$error, function (validator) {
            angular.forEach(validator, function(errorField){
                errorField.$setTouched();
            })
        });
    };

    /**
     * Modifies the given legs, normalizing each travel date to MM/DD/YYYY format.
     * @param legs
     */
    $scope.normalizeTravelDates = function (legs) {
        angular.forEach(legs, function (leg) {
            if (leg.travelDate) {
                leg.travelDate = normalizedDateFormat(leg.travelDate);
            }
        })
    };

    /**
     * Returns a date representing the given date in MM/DD/YYYY format.
     * Returns undefined if the given date format cannot be converted.
     * @param date String representation of a date.
     */
     function normalizedDateFormat(date) {
        if (moment(date, 'M/D/YY', true).isValid()) {
            return moment(date, 'M/D/YY', true).format("MM/DD/YYYY");
        }
        else if (moment(date, 'M/D/YYYY', true).isValid()) {
            return moment(date, 'M/D/YYYY', true).format("MM/DD/YYYY");
        }
        return undefined;
    }

    $scope.checkCounties = function (legs) {
        var deferred = $q.defer();
        var addrsMissingCounty = findAddressesWithoutCounty(legs);

        if (addrsMissingCounty.isEmpty) {
            deferred.resolve();
        }
        else {
            $scope.openLoadingModal();
            countyService.updateWithGeocodeCounty(addrsMissingCounty)
                .then(countyService.addressesMissingCounty) // filter out addresses that were updated with a county.
                .then(countyService.promptUserForCounty)
                .then($scope.closeLoadingModal)
                .then(function () {
                    deferred.resolve();
                })
                .catch(function () {
                    deferred.reject();
                })
        }
        return deferred.promise;


        function findAddressesWithoutCounty(legs) {
            var addresses = legs.map(function (leg) {
                return leg.from.address;
            }).concat(legs.map(function (leg) {
                return leg.to.address;
            }));
            return countyService.addressesMissingCounty(addresses);
        }
    };

    /**
     * Focuses the Other box if method of travel is Other.
     */
    $scope.motChange = function (leg, index, focusInputName) {
        $timeout(function () { // Execute on next digest cycle, giving input box a chance to render.
            if (leg.methodOfTravelDisplayName === 'Other') {
                document.getElementById(focusInputName + index).focus();
            }
        });

    };

    $scope.handleDataProviderError = function () {
        modals.open("external-api-error")
            .then(function () {
                reload();
            })
            .catch(function () {
                locationService.go("/logout", true);
            });
    }
}

function Leg () {
    this.from = new Destination();
    this.to = new Destination();
    this.travelDate = "";
}

function Destination () {
    this.address = new Address();
}

function Address () {
    this.placeId = "";
    this.name = "";
    this.addr1 = "";
    this.city = "";
    this.zip5 = "";
    this.county = "";
    this.state = "";
    this.country = "";
    this.formattedAddress = "";
}