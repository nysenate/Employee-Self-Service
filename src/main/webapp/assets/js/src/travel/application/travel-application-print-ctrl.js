var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationPrintCtrl',
                     ['$scope', 'LocationService', 'TravelApplicationApi', appPrintCtrl]);

function appPrintCtrl($scope, locationService, travelAppApi) {
    console.log("print page ctrl");
}
