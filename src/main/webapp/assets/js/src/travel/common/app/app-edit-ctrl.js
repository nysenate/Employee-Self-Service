var essTravel = angular.module('essTravel');

/**
 * This controller contains function shared by app edit form directives such as purpose-edit-form-directive.js
 */
essTravel.controller('AppEditCtrl', ['$scope', '$q', 'modals', 'AddressCountyService', 'TravelModeOfTransportationApi', appEditCtrl]);

function appEditCtrl($scope, $q, modals, countyService, motApi) {

    this.$onInit = function () {
        motApi.get({}, function (response) {
            $scope.methodsOfTravel = [];
            response.result.forEach(function (modeOfTransportation) {
                $scope.methodsOfTravel.push(modeOfTransportation.displayName);
            });
        }, $scope.handleErrorResponse);
    };

    $scope.cancel = function () {
        modals.open("cancel-application").then(function () {
            modals.resolve({});
            $scope.openLoadingModal();
            cancelApplication();
        });
    };

    function cancelApplication() {
        appIdApi.remove({id: $scope.data.app.id})
            .$promise
            .then(reload)
            .catch($scope.handleErrorResponse)
    }

    $scope.openLoadingModal = function () {
        modals.open('loading');
    };

    $scope.closeLoadingModal = function () {
        if (modals.isTop('loading')) {
            modals.resolve();
        }
    };

    // Set all form elements as touched so they can be styled appropriately if they have errors.
    $scope.setFormElementsTouched = function (form) {
        for (var prop in form) {
            if (form[prop] && typeof(form[prop].$setTouched) === 'function') {
                form[prop].$setTouched();
            }
        }
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
                return leg.from;
            }).concat(legs.map(function (leg) {
                return leg.to;
            }));
            return countyService.addressesMissingCounty(addresses);
        }
    };

    /**
     * Focuses the Other box if method of travel is Other.
     */
    $scope.motChange = function (leg, index) {
        $timeout(function () { // Execute on next digest cycle, giving input box a chance to render.
            if (leg.methodOfTravelDisplayName === 'Other') {
                document.getElementById('outboundMotOtherInput_' + index).focus();
            }
        });

    };
}
