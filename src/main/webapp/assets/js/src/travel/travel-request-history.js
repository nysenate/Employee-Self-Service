var essTravel = angular.module('essTravel');

essTravel.controller('TravelHistoryController', ['$scope', 'TravelApplicationApi', 'PaginationModel', historyController]);

function historyController($scope, travelApplicationApi) {

    var DATE_FORMAT = "MM/DD/YYYY";
    var completeTravelHistory = [];

    $scope.date = {
        from: moment().subtract(1, 'month').format(DATE_FORMAT),
        to: moment().format(DATE_FORMAT)
    }

    $scope.init = function() {
        var empId = 11168;
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
            var row = result[i];
            completeTravelHistory.push({
               travelDate: row.travelDate,
               empName: row.applicant.lastName,
               destination: row.itinerary.destinations[0].address.city,
               allottedFunds: "$" + row.totalAllowance,
               status: row.status
           });
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
    }

    $scope.init();
}