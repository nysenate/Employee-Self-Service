var essTravel = angular.module('essTravel');

essTravel.controller('TravelController', ['$scope', travelController]);

function travelController($scope) {
    console.log("Travel controller init");
    $scope.title = "Stub travel controller"
}