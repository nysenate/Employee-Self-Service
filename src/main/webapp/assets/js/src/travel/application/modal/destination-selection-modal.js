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

    const DATEPICKER_FORMAT = 'MM-DD-YYYY';
    const ISO_FORMAT = 'YYYY-MM-DD';
    $scope.MODES_OF_TRANSPORTATION = ['Personal Auto', 'Senate Vehicle', 'Train', 'Airplane', 'Other'];

    $scope.destination = {
        address: undefined,
        arrivalDate: undefined,
        departureDate: undefined,
        modeOfTransportation: undefined
    };

    $scope.addressCallback = function(address) {
        $scope.destination.address = address;
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
        modals.resolve($scope.destination);
    };

    $scope.cancel = function () {
        modals.reject();
    };

    $scope.init = function() {
        if (modals.params().destination) {
            $scope.destination = modals.params().destination;
        }
        else if(modals.params().defaultModeOfTransportation) {
            $scope.destination.modeOfTransportation = modals.params().defaultModeOfTransportation;
        }
    };

    $scope.init();
}
