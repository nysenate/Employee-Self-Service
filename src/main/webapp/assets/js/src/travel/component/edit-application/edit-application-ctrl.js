var essTravel = angular.module('essTravel');

essTravel.controller('EditApplicationCtrl',
                     ['$scope', 'LocationService', 'modals', 'AppEditStateService',
                      'TravelAppEditApi', 'TravelAppEditResubmitApi', editAppCtrl]);

function editAppCtrl($scope, locationService, modals, stateService, appEditApi, appResubmitApi) {

    var vm = this;
    vm.draft = undefined; // Contains the `traveler` and `amendment` fields which can be edited.
    vm.appId = 0;
    // Some editing functionality is only available to the TRAVEL_ADMIN.
    vm.activeRole = 'NONE';
    vm.dirtyRoute = {};

    (function init() {
        vm.stateService = stateService;
        vm.stateService.setPurposeState();

        vm.appId = locationService.getSearchParam("appId");
        vm.activeRole = locationService.getSearchParam("role");
        appEditApi.get({id: vm.appId, role: vm.activeRole}, function (response) {
            vm.draft = response.result;
            vm.dirtyRoute = angular.copy(vm.draft.amendment.route);
        }, $scope.handleErrorResponse);
    })();

    vm.savePurpose = function (draft) {
        vm.draft = draft;
        stateService.setOutboundState();
    };

    vm.saveOutbound = function (route) {
        vm.dirtyRoute = route;
        stateService.setReturnState();
    };

    vm.saveRoute = function (draft) {
        vm.draft = draft;
        vm.dirtyRoute = angular.copy(draft.amendment.route);
        stateService.setAllowancesState();
    };

    vm.saveAllowances = function (draft) {
        vm.draft = draft
        if (vm.activeRole === 'TRAVEL_ADMIN' || vm.activeRole === 'SECRETARY_OF_THE_SENATE') {
            stateService.setOverridesState();
        } else {
            stateService.setReviewState();
        }
    };

    vm.saveOverrides = function (draft) {
        vm.draft = draft
        stateService.setReviewState();
    };

    vm.saveEdits = function (draft) {
        if (vm.activeRole === 'NONE') {
            appResubmitApi.save({id: vm.appId}, vm.draft, function (response) {
                locationService.go("/travel/applications", false);
            }, $scope.handleErrorResponse);
        } else {
            appEditApi.save({id: vm.appId}, vm.draft, function (response) {
                locationService.go("/travel/manage/review", false, {appId: vm.appId, role: vm.activeRole});
            }, $scope.handleErrorResponse);
        }
    };

    vm.cancelEdit = function (draft) {
        modals.open('cancel-edits').catch(function () {
            if (vm.activeRole === 'NONE') {
                locationService.go("/travel/applications", false);
            } else {
                locationService.go("/travel/manage/review", false, {appId: vm.appId, role: vm.activeRole});
            }
        })
    };

    vm.toPurposeState = function (draft) {
        stateService.setPurposeState();
    };

    vm.toOutboundState = function (draft) {
        stateService.setOutboundState();
    };

    vm.toReturnState = function (draft) {
        stateService.setReturnState();
    };

    vm.toAllowancesState = function (draft) {
        stateService.setAllowancesState();
    };

    vm.toOverridesState = function (draft) {
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