var essTravel = angular.module('essTravel');

essTravel.directive('destinationSelectionModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/destination-selection-modal',
        scope: {},
        controller: 'DestinationSelectionModalCtrl'
    }
}])
    .controller('DestinationSelectionModalCtrl', ['$scope', 'modals', destSelectionCtrl]);

function destSelectionCtrl($scope, modals) {

    $scope.destination = {
        address: undefined,
        arrivalDate: undefined,
        departureDate: undefined
    };

    $scope.setAddressCallback = function(address) {
        $scope.destination.address = address;
    };
    
    $scope.allFieldsEntered = function () {
        return $scope.destination.address !== undefined
            &&  $scope.destination.arrivalDate !== undefined
            && $scope.destination.departureDate !== undefined
    };

    $scope.submit = function () {
        modals.resolve($scope.destination);
    };

    $scope.cancel = function () {
        modals.reject();
    };
}
