var essTimeOff = angular.module('essTime');

essTimeOff.controller('NewRequestCtrl', ['$scope', '$http', function($scope, $http){
    $scope.vac = 0;
    $scope.sick = 0;
    $scope.personal = 0;
}]);