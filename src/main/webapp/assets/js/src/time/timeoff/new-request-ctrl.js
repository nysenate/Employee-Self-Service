var essTimeOff = angular.module('essTime');

essTimeOff.controller('NewRequestCtrl', ['$scope', '$http', function($scope, $http){
    $scope.vac = 21;
    $scope.sick = 482;
    $scope.personal = 34;
}]);