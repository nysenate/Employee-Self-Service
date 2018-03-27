var essTravel = angular.module('essTravel');

essTravel.controller('TravelHistoryController', ['$scope', 'appProps', 'modals', 'TravelApplicationApi', 'PaginationModel', historyController]);

function historyController($scope, appProps, modals, travelApplicationApi) {

    var DATE_FORMAT = "MM/DD/YYYY";

    $scope.apps = {
        all: [],
        filtered: []
    };

    $scope.date = {
        from: moment().subtract(1, 'month').format(DATE_FORMAT),
        to: moment().add(6, 'month').format(DATE_FORMAT)
    };

    $scope.init = function() {
        initDateRanges();
        console.log($scope.date);
        fetchApplications(appProps.user.employeeId)
    };

    function initDateRanges() {
        // $scope.date.from = angular.element("#dateFrom").val();
        // $scope.date.to = angular.element("#dateTo").val();
    }

    function fetchApplications(empId) {
        travelApplicationApi.get({empId: empId}, onSuccess, $scope.handleErrorResponse);

        function onSuccess (resp) {
            parseResponse(resp);
            applyFilters();
            sort($scope.apps.filtered);
            console.log($scope.apps);
            console.log($scope.date);
        }
    }

    function parseResponse(resp) {
        var result = resp.result;
        for (var i = 0; i < result.length; i++) {
            $scope.apps.all.push(result[i]);
        }
    }

    function applyFilters() {
        $scope.apps.filtered = angular.copy($scope.apps.all);
        $scope.apps.filtered = $scope.apps.filtered.filter(function (app) {
            return Date.parse(app.startDate) >= Date.parse($scope.date.from) &&
                Date.parse(app.startDate) <= Date.parse($scope.date.to)
        });
    }

    function sort(apps) {
        apps.sort(function(a, b) {
            // Turn your strings into dates, and then subtract them
            // to get a value that is either negative, positive, or zero.
            return new Date(b.startDate.date) - new Date(a.startDate.date);
        });
    }

    $scope.viewApplicationDetails = function(requestId) {
        // request = {};
        // for (i = 0; i < $scope.travelHistory.length; i++) {
        //     element = $scope.travelHistory[i];
        //     if (element.id == requestId) {
        //         request = element;
        //         break;
        //     }
        // }
        // modals.open("travel-history-detail-modal", {info: request}, true).catch(function() {})
    };

    $scope.init();
}