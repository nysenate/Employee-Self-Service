var essTravel = angular.module('essTravel');

essTravel.directive('destinationSelectionModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/destination-selection-modal',
        scope: {},
        controller: 'DestinationSelectionModalCtrl'
    }
}])
    .controller('DestinationSelectionModalCtrl', ['$scope', 'modals', destSelectionCtrl]);

function destSelectionCtrl($scope, modals) {

    const DATEPICKER_FORMAT = 'MM/DD/YYYY';
    const ISO_FORMAT = 'YYYY-MM-DD';
    $scope.MODES_OF_TRANSPORTATION = ['Personal Auto', 'Senate Vehicle', 'Car Pool', 'Train', 'Airplane', 'Other'];

    $scope.destination = {
        address: undefined,
        arrivalDate: undefined,
        departureDate: undefined,
        modeOfTransportation: undefined
    };

    $scope.addressCallback = function(address) {
        // Not sure why a digest does not run when updating $scope.destination.address.
        $scope.$apply(function () {
            $scope.destination.address = address;
        });
    };
    
    $scope.allFieldsEntered = function () {
        return $scope.destination.address !== undefined
            &&  $scope.destination.arrivalDate !== undefined
            && $scope.destination.departureDate !== undefined
            && $scope.destination.modeOfTransportation !== undefined
    };

    $scope.submit = function () {
        // Convert arrival/departure dates to ISO format.
        $scope.destination.arrivalDate = moment($scope.destination.arrivalDate, DATEPICKER_FORMAT).format(ISO_FORMAT);
        $scope.destination.departureDate = moment($scope.destination.departureDate, DATEPICKER_FORMAT).format(ISO_FORMAT);

        // Initialize with default allowance requests
        $scope.destination.isMealsRequested = true;
        if ($scope.destination.modeOfTransportation === 'Personal Auto') {
            $scope.destination.isMileageRequested = true;
        }
        var arrival = moment($scope.destination.arrivalDate, ISO_FORMAT);
        var departure = moment($scope.destination.departureDate, ISO_FORMAT);
        if (Math.abs(arrival.diff(departure, 'days')) > 0) {
            $scope.destination.isLodgingRequested = true;
        }

        modals.resolve($scope.destination);
    };

    $scope.cancel = function () {
        modals.reject();
    };

    $scope.calculateMinToDate = function() {
        var date = undefined;
        if ($scope.destination.arrivalDate) {
            date = $scope.destination.arrivalDate;
        }
        return date;
    };

    $scope.init = function() {
        if (modals.params().destination) {
            // If editing a destination.
            $scope.destination = modals.params().destination;
            // Convert dates to the datepicker format.
            $scope.destination.arrivalDate = moment($scope.destination.arrivalDate, ISO_FORMAT).format(DATEPICKER_FORMAT);
            $scope.destination.departureDate = moment($scope.destination.departureDate, ISO_FORMAT).format(DATEPICKER_FORMAT);
        }
        else {
            // Otherwise, we are adding a new destination.
            $scope.destination.modeOfTransportation = defaultModeOfTransportation();
            $scope.destination.arrivalDate = defaultArrivalDate();
        }
    };

    function defaultModeOfTransportation() {
        return modals.params().defaultModeOfTransportation;
    }

    function defaultArrivalDate() {
        var date = undefined;
        if (modals.params().defaultArrivalDate) {
            date = moment(modals.params().defaultArrivalDate, ISO_FORMAT).format(DATEPICKER_FORMAT);
        }
        return date;
    }

    $scope.init();
}
