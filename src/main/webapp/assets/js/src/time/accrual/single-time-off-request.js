(function () {

    var essTime = angular.module('essTime');

    essTime.config(['$locationProvider', function ($locationProvider) {
        $locationProvider.html5Mode(true);
    }]);

    essTime.factory('RequestApi', ['$resource', function ($resource) {
        return $resource("/api/v1/accruals/request/:requestId");
    }]);

    essTime.controller('SingleRequestCtrl', ['$scope', '$routeParams', 'appProps', 'RequestApi',
                                        singleRequestCtrl]);

    function singleRequestCtrl ($scope, $routeParams, appProps, RequestApi) {

        $scope.requestId = $routeParams.requestId;
        $scope.loadingRequest = true;

        //initial view mode
        $scope.viewMode = "output";

        //variables to be set after API call
        $scope.startDate = "";
        $scope.endDate = "";
        $scope.request = {};

        //make the call to the backend to get the active requests for the user
        RequestApi.get({requestId: $scope.requestId}).$promise.then(
            //successful query
            function (data) {
                $scope.request = data;
                $scope.startDate = new Date(data.startDate).toLocaleDateString();
                $scope.endDate = new Date(data.endDate).toLocaleDateString();
                $scope.request.days.forEach(function(day){
                   day.date = new Date(day.date).toDateString();
                });
                console.log($scope.request);
            },
            //failed query
            function () {
                $scope.errmsg = "Request number " + $scope.requestId + " does not exist, or\n you do not have" +
                    " permission to access it." ;
                console.log($scope.errmsg);
            }).finally(function() {
                $scope.loadingRequest = false;
            });
    }

})();