var essTravel = angular.module('essTravel');

essTravel.controller('NewTravelApplicationCtrl',
                     ['$scope', '$q', 'appProps', 'modals', 'LocationService', 'TravelGsaAllowanceApi',
                      'TravelMileageAllowanceApi', 'TravelApplicationApi', travelAppController]);

function travelAppController($scope, $q, appProps, modals, locationService, gsaApi,
                             mileageAllowanceApi, travelApplicationApi) {

    /* --- Container Page --- */

    const STATES = ['LOCATION_SELECTION', 'METHOD_AND_PURPOSE', 'REVIEW_AND_SUBMIT'];
    $scope.state = '';
    $scope.app = {
        travelerEmpId: undefined,
        allowances: {
            gsa: {
                meals: 0,
                lodging: 0,
                incidental: 0
            },
            mileage: 0,
            tolls: 0,
            parking: 0,
            alternate: 0,
            registrationFee: 0
        },
        itinerary: {
            origin: undefined,
            destinations: {
                items: []
            }
        },
        purposeOfTravel: ''
    };

    function init() {
        $scope.state = STATES[0];
        $scope.app.travelerEmpId = appProps.user.employeeId;
    }

    init();

    /* --- Location Selection --- */

    $scope.setOrigin = function (address) {
        $scope.app.itinerary.origin = address;
    };

    $scope.addDestinationOnClick = function() {
        var params = {
            defaultModeOfTransportation: undefined
        };
        if ($scope.app.itinerary.destinations.items.length > 0) {
            params.defaultModeOfTransportation = $scope.app.itinerary.destinations.items[$scope.app.itinerary.destinations.items.length - 1].modeOfTransportation
        }
        modals.open('destination-selection-modal', params)
            .then(function (destination) {
                $scope.app.itinerary.destinations.items.push(destination);
            });
    };

    $scope.editDestination = function (dest) {
        var params = {
            destination: dest,
            defaultModeOfTransportation: undefined
        };
        modals.open('destination-selection-modal', params)
            .then(function (destination) {
                dest = destination;
            });
    };

    $scope.removeDestination = function(dest) {
        var index = $scope.app.itinerary.destinations.items.indexOf(dest);
        $scope.app.itinerary.destinations.items.splice(index, 1);
    };

    $scope.locationsCompleted = function() {
        return $scope.app.itinerary.origin && $scope.app.itinerary.destinations.items.length > 0;
    };

    $scope.toMethodAndPurpose = function() {
        $scope.state = STATES[1];
    };

    /* --- Method and Purpose --- */

    $scope.methodAndPurposeCompleted = function () {
        return $scope.app.allowances.tolls != null
            && $scope.app.allowances.parking != null
            && $scope.app.allowances.alternate != null
            && $scope.app.allowances.registrationFee != null
            && $scope.app.purposeOfTravel != null;
    };

    $scope.toReviewAndSubmit = function () {
        $scope.state = STATES[2];
        $scope.initReviewAndSubmit();
    };

    $scope.backToLocationSelection = function () {
        $scope.state = STATES[0];
    };

    /* --- Review and Submit --- */

    $scope.initReviewAndSubmit = function () {
        var promises = [];
        modals.open('calculating-allowances');

        promises.push(gsaApi.save($scope.app.itinerary, function (response) {
            $scope.app.allowances.gsa = response.result;
        }).$promise);

        promises.push(mileageAllowanceApi.save($scope.app.itinerary, function (response) {
            $scope.app.allowances.mileage = response.result.mileage;
        }).$promise);

        $q.all(promises)
            .then(function () {
                sumAllowances();
                modals.resolve({});
            })
    };

    function sumAllowances() {
        $scope.app.allowances.total = parseFloat($scope.app.allowances.gsa.meals)
            + parseFloat($scope.app.allowances.gsa.lodging)
            + parseFloat($scope.app.allowances.mileage)
            + $scope.app.allowances.tolls
            + $scope.app.allowances.parking
            + $scope.app.allowances.alternate
            + $scope.app.allowances.registrationFee;
    }

    $scope.submitApplication = function () {
        modals.open('submit-progress');
        travelApplicationApi.save($scope.app, function () {
            modals.resolve();
            locationService.go("/travel/upcoming-travel");
        });
    };

    $scope.backToMethodAndPurpose = function () {
        $scope.state = STATES[1];
    }
}
