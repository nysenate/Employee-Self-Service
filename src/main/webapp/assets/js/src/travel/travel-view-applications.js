var essTravel = angular.module('essTravel');

essTravel.controller('TravelHistoryController', ['$scope', 'appProps', 'modals', 'TravelApplicationApi', 'PaginationModel', historyController]);

function historyController($scope, appProps, modals, travelApplicationApi) {

    var DATE_FORMAT = "MM/DD/YYYY";

    $scope.apps = {
        all: [],
        filtered: []
    };

    $scope.appRequest = {};

    $scope.date = {
        from: moment().subtract(1, 'month').format(DATE_FORMAT),
        to: moment().add(6, 'month').format(DATE_FORMAT)
    };

    $scope.init = function() {
        fetchApplications(appProps.user.employeeId)
    };

    function fetchApplications(empId) {
        $scope.appRequest = travelApplicationApi.get({empId: empId}, onSuccess, $scope.handleErrorResponse);

        function onSuccess (resp) {
            parseResponse(resp);
            $scope.applyFilters();
            sort($scope.apps.filtered);
        }

        function parseResponse(resp) {
            var result = resp.result;
            for (var i = 0; i < result.length; i++) {
                $scope.apps.all.push(result[i]);
            }
        }
    }

    $scope.applyFilters = function () {
        $scope.apps.filtered = angular.copy($scope.apps.all);
        $scope.apps.filtered = $scope.apps.filtered.filter(function (app) {
            return Date.parse(app.startDate) >= Date.parse($scope.date.from) &&
                Date.parse(app.startDate) <= Date.parse($scope.date.to)
        });
    };

    function sort(apps) {
        apps.sort(function(a, b) {
            // Turn your strings into dates, and then subtract them
            // to get a value that is either negative, positive, or zero.
            return new Date(b.startDate.date) - new Date(a.startDate.date);
        });
    }

    $scope.viewApplicationDetails = function(app) {
        modals.open("travel-history-detail-modal", app, true)
            .catch(function() {})
    };

    $scope.shortAddress = function(app) {
        var addr = app.accommodations[0].address;
        return addr.city || addr.county || addr.addr1;
    };

    $scope.init();
}