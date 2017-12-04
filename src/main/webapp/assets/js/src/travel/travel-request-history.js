var essTravel = angular.module('essTravel');

essTravel.controller('TravelHistoryController', ['$scope', 'appProps', 'modals', 'TravelApplicationApi', 'PaginationModel', historyController]);

function historyController($scope, appProps, modals, travelApplicationApi) {

    var DATE_FORMAT = "MM/DD/YYYY";
    var completeTravelHistory = [];

    $scope.date = {
        from: moment().subtract(1, 'month').format(DATE_FORMAT),
        to: moment().format(DATE_FORMAT)
    }

    $scope.init = function() {
        var empId = 11168;  //for testing purposes
        //TODO: var empId = appProps.empId;
        var status = 'APPROVED';
        var params = {
            empId: empId,
            status: status
        };
        return travelApplicationApi.get(params, onSuccess, onFail);

        function onSuccess (resp) {
            parseResponse(resp);
            $scope.updateDateRange();

        }
        function onFail (resp) {
            modals.open('500', {details: resp});
            console.error(resp);
        }
    };

    function parseResponse(resp) {
        result = resp.result;
        for (var i = 0; i < result.length; i++) {
            completeTravelHistory.push(result[i]);
        }
    }

    $scope.updateDateRange = function() {
        $scope.date.from = angular.element("#dateFrom").val();
        $scope.date.to = angular.element("#dateTo").val();
        $scope.travelHistory = [];
        for (var i = 0; i < completeTravelHistory.length; i++) {
            if (Date.parse(completeTravelHistory[i].travelDate) >= Date.parse($scope.date.from) &&
                Date.parse(completeTravelHistory[i].travelDate) <= Date.parse($scope.date.to)) {
                $scope.travelHistory.push(completeTravelHistory[i]);
            }
        }
        $scope.travelHistory.sort(function(a, b) {
            // Turn your strings into dates, and then subtract them
            // to get a value that is either negative, positive, or zero.
            return new Date(b.travelDate.date) - new Date(a.travelDate.date);
        });
    }

    $scope.viewApplicationDetails = function(requestId) {
        request = {};
        for (i = 0; i < $scope.travelHistory.length; i++) {
            element = $scope.travelHistory[i];
            if (element.id == requestId) {
                request = element;
                break;
            }
        }
        modals.open("travel-history-detail-modal", {info: request}, true).catch(function() {})
    }

    $scope.init();
}