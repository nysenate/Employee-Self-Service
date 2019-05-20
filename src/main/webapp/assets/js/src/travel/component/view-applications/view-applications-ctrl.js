var essTravel = angular.module('essTravel');

essTravel.controller('UserAppsCtrl', ['$scope', 'appProps', 'modals', 'TravelApplicationsForTravelerApi', 'PaginationModel', viewApplicationsCtrl]);

function viewApplicationsCtrl($scope, appProps, modals, travelerAppApi) {

    var vm = this;
    vm.DATE_FORMAT = "MM/DD/YYYY";
    vm.apps = {
        all: [],
        filtered: []
    };
    vm.appRequest = {};
    vm.date = {
        from: moment().subtract(1, 'month').format(vm.DATE_FORMAT),
        to: moment().add(6, 'month').format(vm.DATE_FORMAT)
    };

    function init() {
        fetchApplications(appProps.user.employeeId);
    }

    function fetchApplications(empId) {
        vm.appRequest = travelerAppApi.get({travelerId: empId}, onSuccess, $scope.handleErrorResponse);

        function onSuccess (resp) {
            parseResponse(resp);
            vm.applyFilters();
            console.log(vm.apps.all);
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
            return Date.parse(app.startDate) >= Date.parse(vm.date.from) &&
                Date.parse(app.startDate) <= Date.parse(vm.date.to)
        });
    };

    vm.viewApplicationForm = function(app) {
        modals.open("app-form-view-modal", app, true)
            .catch(function() {})
    };

    init();
}