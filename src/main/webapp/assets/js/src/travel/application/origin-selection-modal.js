var essTravel = angular.module('essTravel');

essTravel.directive('originSelectionModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/origin-selection-modal',
        scope: {},
        controller: 'OriginSelectionModalCtrl'
    }
}])
    .controller('OriginSelectionModalCtrl', ['$scope', originSelectionCtrl]);

function originSelectionCtrl($scope) {

    $scope.addressString = '';

    $scope.address = {
        street1: "",
        street2: "",
        city: "",
        state: "",
        zip: ""
    };

    $scope.locationpickerOptions = {
        location: {
            latitude: 42.65262457001009,
            longitude: -73.75740575790405
        },
        inputBinding: {
            locationNameInput: $('#travel-origin-address')
        },
        radius: 0,
        enableAutocomplete: true,
        addressFormat: 'street_address',
        onchanged: function(currentLocation, radius, isMarkerDropped) {
            var addressComponents = $(this).locationpicker('map').location.addressComponents;
            console.log(addressComponents);
            console.log(currentLocation);
        },
        oninitialized: function(component) {
            // Clear address input.
            $scope.addressString = '';
            $scope.$digest();
        }
    };

    function init() {
        $scope.addressString = '';
    }

    init();
}
