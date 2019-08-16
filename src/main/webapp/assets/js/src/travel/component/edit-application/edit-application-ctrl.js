var essTravel = angular.module('essTravel');

essTravel.controller('EditApplicationCtrl',
                     ['$scope', 'LocationService', 'modals', 'AppEditStateService', 'TravelApplicationByIdApi', editAppCtrl]);

function editAppCtrl($scope, locationService, modals, stateService, appIdApi) {

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
        appIdApi.update({id: app.id}, {purposeOfTravel: app.purposeOfTravel}, function (response) {
            vm.app = response.result;
            stateService.setOutboundState();
        }, $scope.handleErrorResponse)
    };

    vm.saveOutbound = function (app) {
        vm.app.route.outboundLegs = app.route.outboundLegs;
        stateService.setReturnState();
    };

    vm.saveRoute = function (app) {
        vm.openLoadingModal();
        appIdApi.update({id: app.id}, {route: JSON.stringify(app.route)}, function (response) {
            vm.app = response.result;
            stateService.setAllowancesState();
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
            stateService.setOverridesState();
        }, $scope.handleErrorResponse)
    };

    vm.saveOverrides = function (app) {
        appIdApi.update({id: app.id}, {perDiemOverrides: JSON.stringify(app.perDiemOverrides)}, function (response) {
            vm.app = response.result;
            stateService.setReviewState();
            console.log(vm.app);
        }, $scope.handleErrorResponse);
    };

    vm.doneEditing = function (app) {
        locationService.go("/travel/review", false, {appId: app.id});
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