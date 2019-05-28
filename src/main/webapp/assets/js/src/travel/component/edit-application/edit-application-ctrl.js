var essTravel = angular.module('essTravel');

essTravel.controller('EditApplicationCtrl',
                     ['$scope', 'LocationService', 'modals', 'NewAppStateService', 'TravelApplicationByIdApi', editAppCtrl]);

function editAppCtrl($scope, locationService, modals, stateService, appIdApi) {

    var vm = this;
    vm.app = {};

    (function init () {
        vm.stateService = stateService;
        vm.stateService.setPurposeState();

        var appId = locationService.getSearchParam("appId");
        console.log(appId);
        appIdApi.get({id: appId}, function (response) {
            vm.app = response.result;
        }, $scope.handleErrorResponse);
    })();

    vm.savePurpose = function (app) {
        appIdApi.update({id: app.id}, {purposeOfTravel: app.purposeOfTravel}, function (response) {
            vm.app = response.result;
            stateService.nextState();
        }, $scope.handleErrorResponse)
    };

    vm.saveOutbound = function (app) {
        vm.app.route.outboundLegs = app.route.outboundLegs;
        stateService.nextState();
    };

    vm.saveRoute = function (app) {
        vm.openLoadingModal();
        appIdApi.update({id: app.id}, {route: JSON.stringify(app.route)}, function (response) {
            vm.app = response.result;
            stateService.nextState();
            vm.closeLoadingModal();
        }, function (error) {
            vm.closeLoadingModal();
            if (error.status === 502) {
                vm.handleDataProviderError();
            } else {
                $scope.handleErrorResponse(error);
            }
        });
    };

    vm.saveAllowances = function (app) {
        var patches = {
            allowances: JSON.stringify(app.allowances),
            mealPerDiems: JSON.stringify(app.mealPerDiems),
            lodgingPerDiems: JSON.stringify(app.lodgingPerDiems),
            mileagePerDiems: JSON.stringify(app.mileagePerDiems)
        };
        appIdApi.update({id: app.id}, patches, function (response) {
            vm.app = response.result;
            stateService.nextState();
        }, $scope.handleErrorResponse)
    };

    vm.doneEditing = function (app) {
        locationService.go("/travel/review", true, {appId: app.id});
    };

    vm.previousStep = function (app) {
        stateService.previousState();
    };

    vm.openLoadingModal = function () {
        modals.open('loading');
    };

    vm.closeLoadingModal = function () {
        if (modals.isTop('loading')) {
            modals.resolve();
        }
    };

    vm.handleDataProviderError = function () {
        modals.open("external-api-error")
            .then(function () {
                reload();
            })
            .catch(function () {
                locationService.go("/logout", true);
            });
    };
}