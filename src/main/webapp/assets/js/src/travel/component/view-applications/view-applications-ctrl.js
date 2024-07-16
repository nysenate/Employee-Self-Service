var essTravel = angular.module('essTravel');

essTravel.controller('UserAppsCtrl', ['$scope', 'appProps', 'modals', 'LocationService', 'TravelApplicationsForTravelerApi', 'PaginationModel', viewApplicationsCtrl]);

function viewApplicationsCtrl($scope, appProps, modals, locationService, travelerAppApi) {

    const DATEPICKER_FORMAT = "MM/DD/YYYY";
    const ISO_FORMAT = "YYYY-MM-DD";
    
    var vm = this;
    vm.apps = {
        all: [],
        filtered: []
    };
    vm.appRequest = {};
    vm.date = {
        from: moment().subtract(1, 'month').format(DATEPICKER_FORMAT),
        to: moment().add(6, 'month').format(DATEPICKER_FORMAT)
    };

    function init() {
        fetchApplications();
    }

    function fetchApplications() {
        vm.appRequest = travelerAppApi.get(onSuccess, $scope.handleErrorResponse);

        function onSuccess (resp) {
            parseResponse(resp);
            vm.applyFilters();
        }

        function parseResponse(resp) {
            var result = resp.result;
            for (var i = 0; i < result.length; i++) {
                vm.apps.all.push(result[i]);
            }
        }
    }

    vm.applyFilters = function () {
        vm.apps.filtered = angular.copy(vm.apps.all);
        vm.apps.filtered = vm.apps.filtered.filter(function (app) {
            return moment(app.activeAmendment.startDate, ISO_FORMAT) >= moment(vm.date.from, DATEPICKER_FORMAT) &&
                moment(app.activeAmendment.startDate, ISO_FORMAT) <= moment(vm.date.to, DATEPICKER_FORMAT);
        });
        // Sort by start date unit timestamp.
        vm.apps.filtered.sort(function(a, b) {
            return moment(a.activeAmendment.startDate, ISO_FORMAT).format('x')
                - moment(b.activeAmendment.startDate, ISO_FORMAT).format('x');
        })
    };

    vm.viewApplicationForm = function(app) {
        modals.open("app-form-view-modal", app, true)
            .catch(function() {})
    };

    // Called by the app-form-view-modal when clicking the Edit and Resubmit button.
    vm.onEditAndResubmit = function (app) {
        modals.reject();
        locationService.go("/travel/application/edit", true, {appId: app.id, role: 'NONE'});
    }

    init();
}