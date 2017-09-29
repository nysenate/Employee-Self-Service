var essTravel = angular.module('essTravel');

essTravel.controller('TravelHistoryController', ['$scope', 'TravelApplicationApi', 'PaginationModel', historyController]);

function historyController($scope, travelApplicationApi) {

    var DATE_FORMAT = "MM/DD/YYYY";
    $scope.travelHistory = [];

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
            $scope.travelHistory.push({
               travelDate: row.travelDate,
               empName: row.applicant.lastName,
               destination: row.itinerary.destinations[0].address.city,
               allottedFunds: "$" + row.totalAllowance,
               status: row.status
           });
        }
    }

    $scope.init();
}