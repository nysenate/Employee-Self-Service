var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationOutboundCtrl', ['$scope', '$q', 'AddressGeocoder', 'modals',
                                                       'AddressCountyService', outboundCtrl]);

function outboundCtrl($scope, $q, geocoder, modals, countyService) {

    $scope.outbound = {
        form: {}
    };

    $scope.route = angular.copy($scope.app.route);

    if ($scope.route.outboundLegs.length === 0) {
        var segment = new Segment();
        // Init from address to employees work address.
        segment.from = $scope.app.traveler.empWorkLocation.address;
        $scope.route.outboundLegs.push(segment);
    }

    $scope.addSegment = function () {
        // Initialize new leg
        var segment = new Segment();
        var prevSeg = $scope.route.outboundLegs[$scope.route.outboundLegs.length - 1];
        segment.from = prevSeg.to;
        segment.modeOfTransportation = prevSeg.modeOfTransportation;
        segment.isMileageRequested = prevSeg.isMileageRequested;
        segment.isMealsRequested = prevSeg.isMealsRequested;
        segment.isLodgingRequested = prevSeg.isLodgingRequested;
        $scope.route.outboundLegs.push(segment);
    };

    $scope.setFromAddress = function (leg, address) {
        leg.from = address;
    };

    $scope.setToAddress = function (leg, address) {
        leg.to = address;
    };

    $scope.isLastSegment = function (index) {
        return $scope.route.outboundLegs.length - 1 === index;
    };

    $scope.deleteSegment = function () {
        $scope.route.outboundLegs.pop();
    };

    $scope.submit = function () {
        console.log($scope.outbound.form);
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
                    .then($scope.continue);
            }
        }

        function findAddressesWithoutCounty() {
            var addresses = $scope.route.outboundLegs.map(function (leg) {
                return leg.from;
            }).concat($scope.route.outboundLegs.map(function (leg) {
                return leg.to;
            }));
            return countyService.addressesMissingCounty(addresses);
        }
    };

    $scope.continue = function () {
        $scope.outboundCallback($scope.ACTIONS.NEXT, $scope.route);
    }
}
