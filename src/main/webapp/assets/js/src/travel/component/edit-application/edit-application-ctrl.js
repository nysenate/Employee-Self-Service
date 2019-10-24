var essTravel = angular.module('essTravel');

essTravel.controller('EditApplicationCtrl',
                     ['$scope', 'LocationService', 'modals', 'AppEditStateService', 'TravelApplicationByIdApi',
                      'TravelRouteCalcApi', editAppCtrl]);

function editAppCtrl($scope, locationService, modals, stateService, appIdApi, routeCalcApi) {

    var vm = this;
    vm.app = undefined;

    (function init() {
        vm.stateService = stateService;
        vm.stateService.setPurposeState();

        var appId = locationService.getSearchParam("appId");
        appIdApi.get({id: appId}, function (response) {
            vm.app = response.result;
        }, $scope.handleErrorResponse);
    })();

    vm.savePurpose = function (app) {
        vm.app = app;
        stateService.setOutboundState();
    };

    vm.saveOutbound = function (app) {
        vm.app = app;
        stateService.setReturnState();
    };

    vm.saveRoute = function (app) {
        vm.openLoadingModal();
        routeCalcApi.save({}, app.route, function (response) {
            console.log(response);
            vm.app.route = response.result;
            stateService.setAllowancesState();
            vm.closeLoadingModal();
        }, function (error) {
            vm.closeLoadingModal();
            $scope.handleErrorResponse(error);
        });
    };

    vm.saveAllowances = function (app) {
        vm.app = app;
        stateService.setOverridesState();
        // var patches = {
        //     allowances: JSON.stringify(app.allowances),
        //     mealPerDiems: JSON.stringify(app.mealPerDiems),
        //     lodgingPerDiems: JSON.stringify(app.lodgingPerDiems),
        //     mileagePerDiems: JSON.stringify(app.mileagePerDiems)
        // };
        // appIdApi.update({id: app.id}, patches, function (response) {
        //     vm.app = response.result;
        //     stateService.setOverridesState();
        // }, $scope.handleErrorResponse)
    };

    vm.saveOverrides = function (app) {
        vm.app = app;
        stateService.setReviewState();
    };

    vm.saveEdits = function (app) {
        appIdApi.save({appId: app.id}, app, function (response) {
            locationService.go("/travel/review", false, {appId: app.id});
        }, $scope.handleErrorResponse);
    };

    vm.cancelEdit = function (app) {
        modals.open('cancel-edits').then(function () {
            locationService.go("/travel/review", false, {appId: app.id});
        })
    };

    vm.toPurposeState = function (app) {
        stateService.setPurposeState();
    };

    vm.toOutboundState = function (app) {
        stateService.setOutboundState();
    };

    vm.toReturnState = function (app) {
        stateService.setReturnState();
    };

    vm.toAllowancesState = function (app) {
        stateService.setAllowancesState();
    };

    vm.toOverridesState = function (app) {
        stateService.setOverridesState();
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