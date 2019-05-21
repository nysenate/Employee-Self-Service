var essTravel = angular.module('essTravel');

essTravel.controller('EditApplicationCtrl',
                     ['$scope', 'LocationService', 'NewAppStateService', 'TravelApplicationByIdApi', editAppCtrl]);

function editAppCtrl($scope, locationService, stateService, appByIdApi) {

    var vm = this;
    vm.data = {
        app: {}
    };

    (function init () {
        vm.stateService = stateService;
        vm.stateService.setPurposeState();

        var appId = locationService.getSearchParam("appId");
        appByIdApi.get({id: appId}, function (response) {
            vm.data.app = response.result;

            console.log(vm.data.app);
        }, $scope.handleErrorResponse);
    })();
}