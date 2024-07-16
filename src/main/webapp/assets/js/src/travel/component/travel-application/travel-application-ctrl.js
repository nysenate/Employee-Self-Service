var essTravel = angular.module("essTravel");

essTravel.controller("TravelApplicationCtrl", ["$scope", "$routeParams", "modals", "TravelApplicationByIdApi", travelAppCtrl])

function travelAppCtrl($scope, $routeParams, modals, appApi) {

    var vm = this;
    vm.data = {
        app: {},
        isLoading: true,
        isAuthorized: true
    };

    (function () {
        appApi.get({id: $routeParams.id}, function (response) {
            vm.data.app = response.result;
            vm.data.isLoading = false;
        }, function (error) {
            vm.data.isAuthorized = false;
        })
    })();
}
