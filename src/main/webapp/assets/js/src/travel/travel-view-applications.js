var essTravel = angular.module('essTravel');

essTravel.controller('TravelHistoryController', ['$scope', 'appProps', 'modals', 'TravelApplicationsForTravelerApi', 'PaginationModel', historyController]);

function historyController($scope, appProps, modals, travelerAppApi) {

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
        fetchApplications(appProps.user.employeeId);
    };

    function fetchApplications(empId) {
        $scope.appRequest = travelerAppApi.get({travelerId: empId}, onSuccess, $scope.handleErrorResponse);

        function onSuccess (resp) {
            parseResponse(resp);
            $scope.applyFilters();
            console.log($scope.apps.all);
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
        sortByTravelDateAsc($scope.apps.filtered);
    };

    function sortByTravelDateAsc(apps) {
        apps.sort(function(a, b) {
            // Turn your strings into dates, and then subtract them
            // to get a value that is either negative, positive, or zero.
            return new Date(b.startDate) - new Date(a.startDate);
        });
    }

    $scope.viewApplicationDetails = function(app) {
        modals.open("travel-history-detail-modal", app, true)
            .catch(function() {})
    };

    $scope.getDestinations = function(app) {
        var destinations = app.route.destinations[0].city || app.route.destinations[0].addr1 || "N/A";
        if (app.route.destinations.length > 1) {
            destinations += " ..."
        }
        return destinations;
    };

    $scope.init();
}