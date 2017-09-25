var essTravel = angular.module('essTravel');

essTravel.controller('TravelManageHistoryController', ['$scope', historyController]);

function historyController($scope) {
    $scope.test = "Hello angular"
}