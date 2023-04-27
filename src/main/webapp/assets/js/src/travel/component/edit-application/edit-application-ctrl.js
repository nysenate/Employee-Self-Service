var essTravel = angular.module('essTravel');

essTravel.controller('EditApplicationCtrl',
                     ['$scope', 'LocationService', 'modals', 'AppEditStateService', 'UnsubmittedAppApi',
                      'TravelAppEditApi', 'TravelAppEditResubmitApi', 'TravelAppEditRouteApi',
                      'TravelAppEditAllowancesApi', editAppCtrl]);

function editAppCtrl($scope, locationService, modals, stateService, appPatchApi, appEditApi, appResubmitApi,
                     editRouteApi, editAllowancesApi) {

    var vm = this;
    vm.dto = undefined; // Contains the `traveler` and `amendment` fields which can be edited.
    vm.appId = 0;
    // Some editing functionality is only available to the TRAVEL_ADMIN.
    vm.activeRole = 'NONE';

    (function init() {
        vm.stateService = stateService;
        vm.stateService.setPurposeState();

        vm.appId = locationService.getSearchParam("appId");
        vm.activeRole = locationService.getSearchParam("role");
        appEditApi.get({id: vm.appId, role: vm.activeRole}, function (response) {
            vm.dto = response.result;
        }, $scope.handleErrorResponse);

        console.log(vm);
    })();

    vm.savePurpose = function (dto) {
        vm.dto = dto;
        stateService.setOutboundState();
    };

    vm.saveOutbound = function (amendment) {
        vm.dto.amendment = amendment;
        stateService.setReturnState();
    };

    vm.saveRoute = function (amendment) {
        vm.openLoadingModal();
        editRouteApi.save({id: vm.appId}, {
            traveler: vm.dto.traveler,
            amendment: amendment
        }, function (response) {
            vm.dto = response.result;
            stateService.setAllowancesState();
            vm.closeLoadingModal();
        }, function (error) {
            vm.closeLoadingModal();
            $scope.handleErrorResponse(error);
        });
    };

    vm.saveAllowances = function (amendment) {
        vm.openLoadingModal();
        editAllowancesApi.save({id: vm.appId},
                               {traveler: vm.dto.traveler, amendment: amendment},
                               success, error)

        function success(response) {
            vm.dto = response.result;
            vm.closeLoadingModal();
            if (vm.activeRole === 'TRAVEL_ADMIN' || vm.activeRole === 'SECRETARY_OF_THE_SENATE') {
                stateService.setOverridesState();
            } else {
                stateService.setReviewState();
            }
        }

        function error(error) {
            vm.closeLoadingModal();
            $scope.handleErrorResponse(error);
        }
    };

    vm.saveOverrides = function (amendment) {
        vm.openLoadingModal();
        editAllowancesApi.save({id: vm.appId},
                               {traveler: vm.dto.traveler, amendment: amendment},
                               success, error)

        function success(response) {
            vm.dto = response.result;
            vm.closeLoadingModal();
            stateService.setReviewState();
        }

        function error(error) {
            vm.closeLoadingModal();
            $scope.handleErrorResponse(error);
        }
    };

    vm.saveEdits = function (amendment) {
        if (vm.activeRole === 'NONE') {
            appResubmitApi.save({id: vm.appId}, vm.dto, function (response) {
                locationService.go("/travel/applications", false);
            }, $scope.handleErrorResponse);
        } else {
            appEditApi.save({id: vm.appId}, vm.dto, function (response) {
                locationService.go("/travel/manage/review", false, {appId: vm.appId, role: vm.activeRole});
            }, $scope.handleErrorResponse);
        }
    };

    vm.cancelEdit = function (app) {
        modals.open('cancel-edits').catch(function () {
            if (vm.activeRole === 'NONE') {
                locationService.go("/travel/applications", false);
            } else {
                locationService.go("/travel/manage/review", false, {appId: vm.appId, role: vm.activeRole});
            }
        })
    };

    vm.toPurposeState = function (amendment) {
        stateService.setPurposeState();
    };

    vm.toOutboundState = function (amendment) {
        stateService.setOutboundState();
    };

    vm.toReturnState = function (amendment) {
        stateService.setReturnState();
    };

    vm.toAllowancesState = function (amendment) {
        stateService.setAllowancesState();
    };

    vm.toOverridesState = function (amendment) {
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