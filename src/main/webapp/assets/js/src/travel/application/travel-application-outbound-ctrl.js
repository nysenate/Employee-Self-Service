var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationOutboundCtrl', ['$scope', '$q', '$timeout', 'AddressGeocoder', 'modals', outboundCtrl]);

function outboundCtrl($scope, $q, $timeout, geocoder, modals) {

    this.$onInit = function () {
        $scope.outbound = {
            form: {}
        };
        $scope.dirtyApp = angular.copy($scope.data.app);
        console.log($scope.dirtyApp);

        if ($scope.dirtyApp.route.outboundLegs.length === 0) {
            var leg = new Leg();
            // Init from address to employees work address.
            leg.from.address = $scope.data.app.traveler.empWorkLocation.address;
            $scope.dirtyApp.route.outboundLegs.push(leg);
        }
    };

    $scope.addSegment = function () {
        // Initialize new leg
        var segment = new Leg();
        var prevSeg = $scope.dirtyApp.route.outboundLegs[$scope.dirtyApp.route.outboundLegs.length - 1];
        segment.from = prevSeg.to;
        segment.modeOfTransportation = prevSeg.modeOfTransportation;
        segment.isMileageRequested = prevSeg.isMileageRequested;
        segment.isMealsRequested = prevSeg.isMealsRequested;
        segment.isLodgingRequested = prevSeg.isLodgingRequested;
        $scope.dirtyApp.route.outboundLegs.push(segment);
    };

    $scope.setFromAddress = function (leg, address) {
        leg.from.address = address;
    };

    $scope.setToAddress = function (leg, address) {
        leg.to.address = address;
    };

    $scope.isLastSegment = function (index) {
        return $scope.dirtyApp.route.outboundLegs.length - 1 === index;
    };

    $scope.deleteSegment = function () {
        $scope.dirtyApp.route.outboundLegs.pop();
    };

    $scope.next = function () {
        $scope.setFormElementsTouched($scope.outbound.form);
        if ($scope.outbound.form.$valid) {
            $scope.normalizeTravelDates($scope.dirtyApp.route.outboundLegs);
            $scope.checkCounties($scope.dirtyApp.route.outboundLegs)
                .then($scope.continue);
        }
    };

    $scope.continue = function () {
        $scope.data.app.route.outboundLegs = $scope.dirtyApp.route.outboundLegs;
        $scope.nextState();
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

