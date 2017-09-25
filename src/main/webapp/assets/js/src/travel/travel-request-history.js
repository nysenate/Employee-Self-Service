var essTravel = angular.module('essTravel');

essTravel.controller('TravelHistoryController', ['$scope', historyController]);

function historyController($scope) {
    $scope.test = "Hello angular"
}