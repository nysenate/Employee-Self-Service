var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationOutboundCtrl', ['$scope', '$q', '$timeout', 'AddressGeocoder', 'modals',
                                                       'AddressCountyService', 'TravelApplicationOutboundApi', outboundCtrl]);

function outboundCtrl($scope, $q, $timeout, geocoder, modals, countyService, outboundApi) {

    this.$onInit = function () {
        $scope.outbound = {
            form: {}
        };
        $scope.dirtyApp = angular.copy($scope.data.app);

        if ($scope.dirtyApp.route.outboundLegs.length === 0) {
            var segment = new Segment();
            // Init from address to employees work address.
            segment.from = $scope.data.app.traveler.empWorkLocation.address;
            $scope.dirtyApp.route.outboundLegs.push(segment);
        }
    };

    $scope.addSegment = function () {
        // Initialize new leg
        var segment = new Segment();
        var prevSeg = $scope.dirtyApp.route.outboundLegs[$scope.dirtyApp.route.outboundLegs.length - 1];
        segment.from = prevSeg.to;
        segment.modeOfTransportation = prevSeg.modeOfTransportation;
        segment.isMileageRequested = prevSeg.isMileageRequested;
        segment.isMealsRequested = prevSeg.isMealsRequested;
        segment.isLodgingRequested = prevSeg.isLodgingRequested;
        $scope.dirtyApp.route.outboundLegs.push(segment);
    };

    $scope.setFromAddress = function (leg, address) {
        leg.from = address;
    };

    $scope.setToAddress = function (leg, address) {
        leg.to = address;
    };

    $scope.isLastSegment = function (index) {
        return $scope.dirtyApp.route.outboundLegs.length - 1 === index;
    };

    $scope.deleteSegment = function () {
        $scope.dirtyApp.route.outboundLegs.pop();
    };

    $scope.next = function () {
        for (var prop in $scope.outbound.form) {
            // Set all form elements as touched so they can be styled appropriately if they have errors.
            if ($scope.outbound.form[prop] && typeof($scope.outbound.form[prop].$setTouched) === 'function') {
                $scope.outbound.form[prop].$setTouched();
            }
        }

        // If the entire form is valid, continue.
        if ($scope.outbound.form.$valid) {
            var addrsMissingCounty = findAddressesWithoutCounty();

            if (addrsMissingCounty.isEmpty) {
                $scope.continue();
            }
            else {
                $scope.openLoadingModal();
                countyService.updateWithGeocodeCounty(addrsMissingCounty)
                    .then(countyService.addressesMissingCounty) // filter out addresses that were updated with a county.
                    .then(countyService.promptUserForCounty)
                    .then($scope.closeLoadingModal)
                    .then($scope.continue)
                    .catch(function () {
                        console.log("Canceling county input")
                    });
            }
        }

        function findAddressesWithoutCounty() {
            var addresses = $scope.dirtyApp.route.outboundLegs.map(function (leg) {
                return leg.from;
            }).concat($scope.dirtyApp.route.outboundLegs.map(function (leg) {
                return leg.to;
            }));
            return countyService.addressesMissingCounty(addresses);
        }
    };

    $scope.continue = function () {
        outboundApi.update({id: $scope.data.app.id}, $scope.dirtyApp.route, function (response) {
            $scope.data.app = response.result;
            $scope.nextState();
        }, $scope.handleErrorResponse);
    };

    /**
     * Sets the focus to the Other MOT input box when selecting Other MOT.
     * @param leg
     */
    $scope.motChange = function (leg, index) {
        $timeout(function () { // Execute on next digest cycle, giving input box a chance to render.
            if (leg.modeOfTransportation.methodOfTravel === 'OTHER') {
                document.getElementById('outboundMotOtherInput_' + index).focus();
            }
        });

    };
}
